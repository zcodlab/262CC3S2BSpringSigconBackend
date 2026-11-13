package dsw.sigconbackend.config;

import dsw.sigconbackend.auditoria.AuditoriaInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuracion MVC que registra el AuditoriaInterceptor para que
 * sea ejecutado en cada request HTTP entrante.
 *
 * El interceptor inicializa el AuditoriaContext (ThreadLocal) con la IP,
 * metodo, endpoint e ID de usuario autenticado, y lo limpia al finalizar
 * el request para evitar fugas de datos entre hilos del pool.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuditoriaInterceptor auditoriaInterceptor;

    public WebConfig(AuditoriaInterceptor auditoriaInterceptor) {
        this.auditoriaInterceptor = auditoriaInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(auditoriaInterceptor)
                // Aplica a todos los endpoints de la API
                .addPathPatterns("/api/**")
                // Excluye recursos estaticos, Swagger y actuator
                .excludePathPatterns("/actuator/**", "/docs/**",
                        "/swagger-ui/**", "/v3/api-docs/**");
    }
}