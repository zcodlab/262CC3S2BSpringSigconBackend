package dsw.sigconbackend.auditoria;

import dsw.sigconbackend.model.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * Interceptor HTTP que inicializa el {@link AuditoriaContext} antes de cada
 * request y lo limpia al finalizar, garantizando que no haya fugas de datos
 * entre hilos.
 *
 * <p>Extrae del {@link SecurityContextHolder}:
 * <ul>
 *   <li>ID del usuario autenticado (0 si es anónimo)</li>
 *   <li>IP de origen del cliente</li>
 *   <li>Método HTTP y endpoint</li>
 *   <li>Un UUID de correlación único por request</li>
 * </ul>
 * </p>
 */
@Component
public class AuditoriaInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaInterceptor.class);

    @Value("${spring.application.name:SigconBackend}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        // 1. Inicializar el contexto de auditoría para este thread
        AuditoriaContext.inicializar();

        // 2. ID del usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof Usuario usuario) {
            AuditoriaContext.setIdUsuario(usuario.getId());
        } else {
            AuditoriaContext.setIdUsuario(0L);
        }

        // 3. IP de origen (considera proxies X-Forwarded-For)
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        } else {
            // X-Forwarded-For puede contener una lista "ip1, ip2, ..."
            ip = ip.split(",")[0].trim();
        }

        // Si la IP es loopback (::1 / 127.0.0.1), el cliente y el servidor están
        // en la misma máquina. En ese caso intentamos obtener la IP de red local
        // del servidor como referencia. Para obtener la IP real del cliente remoto,
        // el frontend debe enviar el header X-Forwarded-For o acceder por IP de red.
        if ("::1".equals(ip) || "127.0.0.1".equals(ip)) {
            try {
                String localIp = java.net.InetAddress.getLocalHost().getHostAddress();
                ip = localIp + " (loopback→local)";
            } catch (java.net.UnknownHostException e) {
                // Dejar la ip loopback tal cual si falla la resolución
            }
        }
        AuditoriaContext.setIpOrigen(ip);


        // 4. Método HTTP y endpoint
        AuditoriaContext.setMetodoHttp(request.getMethod());
        AuditoriaContext.setEndpoint(request.getRequestURI());

        // 5. UUID de correlación único por request
        AuditoriaContext.setIdInteraccion(UUID.randomUUID());

        // 6. Versión de la aplicación
        AuditoriaContext.setVersionAplicacion(appVersion);

        log.debug("[Auditoria] Context inicializado — usuario={} ip={} endpoint={}",
                AuditoriaContext.getIdUsuario(), ip, request.getRequestURI());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        // Limpiar SIEMPRE para evitar fugas de datos entre requests del pool
        AuditoriaContext.limpiar();
    }
}
