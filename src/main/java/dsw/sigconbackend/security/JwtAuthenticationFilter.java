package dsw.sigconbackend.security;

import dsw.sigconbackend.controller.AuthController;
import dsw.sigconbackend.model.Usuario;
import dsw.sigconbackend.repository.UsuarioRepository;
import dsw.sigconbackend.service.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtService;
    private final UsuarioRepository usuarioRepository;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public JwtAuthenticationFilter(
            JwtUtil jwtService,
            UsuarioRepository usuarioRepository,
            @Qualifier("handlerExceptionResolver") @Lazy HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        logger.info(">doFilterInternal:"+  authHeader);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final String userEmail = jwtService.extractUsername(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Usuario usuario = usuarioRepository.findByEmail(userEmail)
                        .orElse(null);

                if (usuario != null && jwtService.isTokenValid(jwt, usuario)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        usuario,
                        null,
                        Collections.emptyList() // Since we're not using roles, empty authorities
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException ex) {
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }
    }
}