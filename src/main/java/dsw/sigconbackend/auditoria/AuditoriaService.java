package dsw.sigconbackend.auditoria;

import dsw.sigconbackend.model.auditoria.Cambio;
import dsw.sigconbackend.model.auditoria.Evento;
import dsw.sigconbackend.repository.auditoria.CambioRepository;
import dsw.sigconbackend.repository.auditoria.EventoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Servicio generico de auditoria que persiste Evento y Cambio
 * para cualquier operacion CUD (Create, Update, Delete) del sistema.
 *
 * Lee los datos HTTP del hilo actual desde AuditoriaContext,
 * que el AuditoriaInterceptor ya habra poblado antes de que llegue
 * la peticion al servicio de negocio.
 *
 * Para UPDATE y DELETE compara campos automaticamente mediante Java
 * Reflection, lo que lo hace reutilizable para cualquier entidad futura.
 */
@Service
public class AuditoriaService {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaService.class);

    private final EventoRepository eventoRepository;
    private final CambioRepository cambioRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private HttpServletResponse httpResponse;

    public AuditoriaService(EventoRepository eventoRepository,
                            CambioRepository cambioRepository) {
        this.eventoRepository = eventoRepository;
        this.cambioRepository = cambioRepository;
    }

    // ======================================================================
    //  API publica
    // ======================================================================

    /** Registra una operacion INSERT. Solo persiste el Evento. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarCreacion(String esquema, String tabla,
                                  String idRegistro, String modulo) {
        Evento evento = construirEvento(esquema, tabla, idRegistro, modulo, "INSERT");
        eventoRepository.save(evento);
        log.debug("[Auditoria] INSERT registrado - tabla={}.{} id={}", esquema, tabla, idRegistro);
    }

    /** Registra una operacion UPDATE comparando campos via reflexion. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarActualizacion(String esquema, String tabla,
                                       String idRegistro, String modulo,
                                       Object estadoAnterior, Object estadoNuevo) {
        Evento evento = construirEvento(esquema, tabla, idRegistro, modulo, "UPDATE");
        eventoRepository.save(evento);
        List<Cambio> cambios = compararCampos(evento, estadoAnterior, estadoNuevo);
        if (!cambios.isEmpty()) cambioRepository.saveAll(cambios);
        log.debug("[Auditoria] UPDATE registrado - tabla={}.{} id={} cambios={}",
                esquema, tabla, idRegistro, cambios.size());
    }

    /** Registra una operacion DELETE guardando el snapshot previo. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarEliminacion(String esquema, String tabla,
                                     String idRegistro, String modulo,
                                     Object estadoAnterior) {
        Evento evento = construirEvento(esquema, tabla, idRegistro, modulo, "DELETE");
        eventoRepository.save(evento);
        List<Cambio> cambios = capturarEstado(evento, estadoAnterior);
        if (!cambios.isEmpty()) cambioRepository.saveAll(cambios);
        log.debug("[Auditoria] DELETE registrado - tabla={}.{} id={} campos={}",
                esquema, tabla, idRegistro, cambios.size());
    }

    // ======================================================================
    //  Metodos internos
    // ======================================================================

    private Evento construirEvento(String esquema, String tabla,
                                   String idRegistro, String modulo,
                                   String operacion) {
        return Evento.builder()
                .nombreEsquema(esquema)
                .nombreTabla(tabla)
                .idRegistro(idRegistro)
                .modulo(modulo)
                .operacion(operacion)
                .fechaEvento(OffsetDateTime.now())
                .resultado("SUCCESS")
                .idUsuario(nullSafeLong(AuditoriaContext.getIdUsuario()))
                .ipOrigen(AuditoriaContext.getIpOrigen())
                .metodoHttp(AuditoriaContext.getMetodoHttp())
                .endpoint(AuditoriaContext.getEndpoint())
                // Lee el código HTTP directamente del response del request actual.
                // Spring inyecta el HttpServletResponse del thread correcto de forma thread-safe.
                .codigoHttp(httpResponse != null ? httpResponse.getStatus() : null)
                .idInteraccion(AuditoriaContext.getIdInteraccion())
                .versionAplicacion(AuditoriaContext.getVersionAplicacion())
                .build();
    }

    /** Compara dos objetos campo a campo (via Reflection) y retorna los Cambio con diferencias. */
    private List<Cambio> compararCampos(Evento evento, Object anterior, Object nuevo) {
        List<Cambio> cambios = new ArrayList<>();
        if (anterior == null || nuevo == null) return cambios;
        Class<?> clazz = anterior.getClass();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    Object va = field.get(anterior);
                    Object vn = field.get(nuevo);
                    if (!Objects.equals(va, vn)) {
                        cambios.add(Cambio.builder()
                                .evento(evento)
                                .nombreCampo(field.getName())
                                .valorAnterior(toStr(va))
                                .valorNuevo(toStr(vn))
                                .build());
                    }
                } catch (IllegalAccessException e) {
                    log.warn("[Auditoria] No se pudo acceder al campo '{}': {}", field.getName(), e.getMessage());
                }
            }
            clazz = clazz.getSuperclass();
        }
        return cambios;
    }

    /** Captura todos los campos de un objeto como Cambio con valorNuevo=null (DELETE). */
    private List<Cambio> capturarEstado(Evento evento, Object objeto) {
        List<Cambio> cambios = new ArrayList<>();
        if (objeto == null) return cambios;
        Class<?> clazz = objeto.getClass();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    cambios.add(Cambio.builder()
                            .evento(evento)
                            .nombreCampo(field.getName())
                            .valorAnterior(toStr(field.get(objeto)))
                            .valorNuevo(null)
                            .build());
                } catch (IllegalAccessException e) {
                    log.warn("[Auditoria] No se pudo acceder al campo '{}': {}", field.getName(), e.getMessage());
                }
            }
            clazz = clazz.getSuperclass();
        }
        return cambios;
    }

    private String toStr(Object obj) { return obj == null ? null : obj.toString(); }
    private long nullSafeLong(Long value) { return value != null ? value : 0L; }
}