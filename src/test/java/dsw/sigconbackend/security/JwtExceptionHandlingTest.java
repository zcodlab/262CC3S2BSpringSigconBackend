package dsw.sigconbackend.security;

import dsw.sigconbackend.exception.GlobalExceptionHandler;
import dsw.sigconbackend.repository.UsuarioRepository;
import dsw.sigconbackend.service.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtExceptionHandlingTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private HandlerExceptionResolver handlerExceptionResolver;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    public void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    public void testFilterDelegatesExpiredJwtExceptionToResolver() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        request.addHeader("Authorization", "Bearer expired_jwt_token");

        ExpiredJwtException expiredException = new ExpiredJwtException(null, null, "JWT expired");
        when(jwtUtil.extractUsername("expired_jwt_token")).thenThrow(expiredException);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(handlerExceptionResolver).resolveException(eq(request), eq(response), eq(null), eq(expiredException));
    }

    @Test
    public void testFilterDelegatesInvalidJwtExceptionToResolver() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        request.addHeader("Authorization", "Bearer invalid_jwt_token");

        JwtException jwtException = new JwtException("Invalid token signature");
        when(jwtUtil.extractUsername("invalid_jwt_token")).thenThrow(jwtException);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(handlerExceptionResolver).resolveException(eq(request), eq(response), eq(null), eq(jwtException));
    }

    @Test
    public void testGlobalExceptionHandlerExpiredJwt() {
        ExpiredJwtException ex = new ExpiredJwtException(null, null, "Token expired");
        ResponseEntity<dsw.sigconbackend.util.ErrorResponse> response = globalExceptionHandler.handleExpiredJwtException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(401, response.getBody().getStatus());
        assertEquals("Unauthorized", response.getBody().getError());
        assertEquals("El token JWT ha expirado", response.getBody().getMessage());
    }

    @Test
    public void testGlobalExceptionHandlerBadCredentials() {
        BadCredentialsException ex = new BadCredentialsException("Contraseña incorrecta");
        ResponseEntity<dsw.sigconbackend.util.ErrorResponse> response = globalExceptionHandler.handleAuthenticationException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(401, response.getBody().getStatus());
        assertEquals("Unauthorized", response.getBody().getError());
        assertEquals("Contraseña incorrecta", response.getBody().getMessage());
    }

    @Test
    public void testGlobalExceptionHandlerDataIntegrityViolationForeignKey() {
        org.springframework.dao.DataIntegrityViolationException ex = new org.springframework.dao.DataIntegrityViolationException(
                "could not execute statement",
                new RuntimeException("ERROR: update or delete on table \"persona\" violates foreign key constraint \"fk_propieta_reference_persona\" on table \"propietario\"\n Detail: Key (id_persona)=(10) is still referenced from table \"propietario\".")
        );
        ResponseEntity<dsw.sigconbackend.util.ErrorResponse> response = globalExceptionHandler.handleDataIntegrityViolationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("No se puede eliminar ni modificar la persona porque está siendo referenciada por otras entidades en el sistema (por ejemplo, 'propietario').", response.getBody().getMessage());
    }
}
