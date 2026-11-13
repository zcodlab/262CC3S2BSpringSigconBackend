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

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.UUID;

/**
 * Interceptor HTTP que inicializa el {@link AuditoriaContext} antes de cada
 * request y lo limpia al finalizar, garantizando que no haya fugas de datos
 * entre hilos.
 *
 * <p>La IP de origen se obtiene <strong>exclusivamente</strong> de
 * {@link jakarta.servlet.http.HttpServletRequest#getRemoteAddr()}, que refleja
 * la dirección TCP real de la conexión entrante. El header
 * {@code X-Forwarded-For} enviado por el cliente <em>no</em> se lee porque
 * puede ser falsificado (spoofing). Si en el futuro el sistema se despliega
 * detrás de un proxy/load-balancer de confianza (nginx, AWS ALB, etc.), la
 * forma correcta de propagar la IP real es configurar
 * {@code ForwardedHeaderFilter} con la lista de proxies confiables en el
 * servidor, no confiar en el header enviado por el cliente.</p>
 *
 * <p>Extrae del {@link SecurityContextHolder}:
 * <ul>
 *   <li>ID del usuario autenticado (0 si es anónimo)</li>
 *   <li>IP de origen del cliente (normalizada de IPv6 a IPv4 cuando aplica)</li>
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

        // 3. IP de origen — solo se confía en la dirección TCP real de la conexión.
        //    El header X-Forwarded-For NO se lee desde el cliente porque puede
        //    ser falsificado. Si hay un proxy de confianza, configurar
        //    ForwardedHeaderFilter en el servidor con la lista de proxies permitidos.
        String ip = normalizarIp(request.getRemoteAddr());

        // Si la IP es loopback (::1 / 127.0.0.1) el cliente y el servidor están
        // en la misma máquina (entorno de desarrollo). Se registra la IP de red
        // local del servidor solo como referencia.
        if ("::1".equals(ip) || "127.0.0.1".equals(ip)) {
            try {
                String localIp = InetAddress.getLocalHost().getHostAddress();
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

    /**
     * Normaliza una dirección IP a su forma más legible.
     * <ul>
     *   <li>IPv6 mapeada a IPv4 ({@code ::ffff:x.x.x.x}) → devuelve la parte IPv4.</li>
     *   <li>Elimina el sufijo de zona ({@code %eth0}) si existe.</li>
     *   <li>Para cualquier otra IPv6 intenta resolverla mediante {@link InetAddress};
     *       si el resultado es una Inet4Address la devuelve como cadena IPv4.</li>
     * </ul>
     *
     * @param ip dirección IP cruda proveniente del request
     * @return dirección normalizada
     */
    private String normalizarIp(String ip) {
        if (ip == null || ip.isBlank()) return ip;

        // Eliminar sufijo de zona IPv6 (ej. "fe80::1%lo0")
        int zonaIdx = ip.indexOf('%');
        if (zonaIdx != -1) {
            ip = ip.substring(0, zonaIdx);
        }

        // IPv6 mapeada a IPv4: "::ffff:x.x.x.x" o "0:0:0:0:0:ffff:x.x.x.x"
        String lower = ip.toLowerCase();
        if (lower.startsWith("::ffff:")) {
            return ip.substring(7);
        }

        // Intentar resolver mediante InetAddress por si el SO ya resolvió el mapeo
        try {
            InetAddress addr = InetAddress.getByName(ip);
            if (addr instanceof Inet4Address) {
                return addr.getHostAddress();
            }
            // Para IPv6 puras, devolver la forma canónica comprimida
            return addr.getHostAddress();
        } catch (java.net.UnknownHostException e) {
            // Si no se puede resolver, devolver la ip tal cual
            return ip;
        }
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
