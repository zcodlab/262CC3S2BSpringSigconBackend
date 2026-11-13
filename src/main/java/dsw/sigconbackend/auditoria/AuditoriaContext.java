package dsw.sigconbackend.auditoria;

import java.util.UUID;

/**
 * Contenedor de datos de auditoría propagados mediante ThreadLocal.
 * <p>
 * El {@link AuditoriaInterceptor} pobla este contexto al inicio de cada
 * request HTTP y lo limpia al finalizar. Los servicios de negocio lo leen
 * para enriquecer el {@link dsw.sigconbackend.model.auditoria.Evento} sin
 * necesidad de recibir esos datos como parámetros.
 * </p>
 *
 * <pre>
 * // Leer datos en un servicio:
 * String ip = AuditoriaContext.getIpOrigen();
 * Long  id  = AuditoriaContext.getIdUsuario();
 * </pre>
 */
public final class AuditoriaContext {

    // ── ThreadLocal interno ───────────────────────────────────────────────────
    private static final ThreadLocal<AuditoriaContext> HOLDER = new ThreadLocal<>();

    // ── Campos del contexto ───────────────────────────────────────────────────
    private Long    idUsuario;
    private String  ipOrigen;
    private String  metodoHttp;
    private String  endpoint;
    private UUID    idInteraccion;
    private String  versionAplicacion;

    // ── Constructor privado ───────────────────────────────────────────────────
    private AuditoriaContext() {}

    // ── API estática ──────────────────────────────────────────────────────────

    /** Inicializa un contexto vacío para el thread actual. */
    public static void inicializar() {
        HOLDER.set(new AuditoriaContext());
    }

    /** Elimina el contexto del thread (llamar en finally del interceptor). */
    public static void limpiar() {
        HOLDER.remove();
    }

    // ── Getters estáticos ─────────────────────────────────────────────────────

    public static Long getIdUsuario() {
        AuditoriaContext ctx = HOLDER.get();
        return ctx != null ? ctx.idUsuario : 0L;
    }

    public static String getIpOrigen() {
        AuditoriaContext ctx = HOLDER.get();
        return ctx != null ? ctx.ipOrigen : null;
    }

    public static String getMetodoHttp() {
        AuditoriaContext ctx = HOLDER.get();
        return ctx != null ? ctx.metodoHttp : null;
    }

    public static String getEndpoint() {
        AuditoriaContext ctx = HOLDER.get();
        return ctx != null ? ctx.endpoint : null;
    }

    public static UUID getIdInteraccion() {
        AuditoriaContext ctx = HOLDER.get();
        return ctx != null ? ctx.idInteraccion : null;
    }

    public static String getVersionAplicacion() {
        AuditoriaContext ctx = HOLDER.get();
        return ctx != null ? ctx.versionAplicacion : null;
    }

    // ── Setters estáticos ─────────────────────────────────────────────────────

    public static void setIdUsuario(Long idUsuario) {
        AuditoriaContext ctx = HOLDER.get();
        if (ctx != null) ctx.idUsuario = idUsuario;
    }

    public static void setIpOrigen(String ipOrigen) {
        AuditoriaContext ctx = HOLDER.get();
        if (ctx != null) ctx.ipOrigen = ipOrigen;
    }

    public static void setMetodoHttp(String metodoHttp) {
        AuditoriaContext ctx = HOLDER.get();
        if (ctx != null) ctx.metodoHttp = metodoHttp;
    }

    public static void setEndpoint(String endpoint) {
        AuditoriaContext ctx = HOLDER.get();
        if (ctx != null) ctx.endpoint = endpoint;
    }

    public static void setIdInteraccion(UUID idInteraccion) {
        AuditoriaContext ctx = HOLDER.get();
        if (ctx != null) ctx.idInteraccion = idInteraccion;
    }

    public static void setVersionAplicacion(String versionAplicacion) {
        AuditoriaContext ctx = HOLDER.get();
        if (ctx != null) ctx.versionAplicacion = versionAplicacion;
    }
}
