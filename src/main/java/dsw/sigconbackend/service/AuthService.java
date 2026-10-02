package dsw.sigconbackend.service;

import dsw.sigconbackend.dto.AuthResponseDTO;
import dsw.sigconbackend.dto.LoginRequestDTO;
import dsw.sigconbackend.dto.PersonaRequest;
import dsw.sigconbackend.dto.RegisterRequestDTO;
import dsw.sigconbackend.model.Modulo;
import dsw.sigconbackend.model.Persona;
import dsw.sigconbackend.model.Usuario;
import dsw.sigconbackend.repository.ModuloRepository;
import dsw.sigconbackend.repository.PersonaRepository;
import dsw.sigconbackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final ModuloRepository moduloRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtService;

    public AuthResponseDTO register(RegisterRequestDTO request){
        if(usuarioRepository.findByEmail(request.getEmail()).isPresent())
            throw new IllegalArgumentException("Hay un usuario registrado con ese email");

        PersonaRequest personaDTO= request.getPersona();
        if(personaDTO==null)
            throw new IllegalArgumentException("Los datos de la persona son obligatorios");

        if(personaRepository.existsByNumDocumento(personaDTO.getNumDocumento()))
            throw new IllegalArgumentException("Hay un usuario registrado con ese numero de documento");

        Persona persona = PersonaRequest.toEntity(personaDTO);
        personaRepository.save(persona);

        //Crear el usuario
        var usuario= Usuario.builder()
                .email(request.getEmail())
                .persona(persona)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();
        usuarioRepository.save(usuario);

        var token=jwtService.generateToken(usuario, Collections.emptyList());
        return AuthResponseDTO.builder()
                .token(token)
                .build();
    }

    //se tiene una cuenta existente y se desea loguerse
    public AuthResponseDTO login(LoginRequestDTO request){
        if (request == null || request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        var usuario=usuarioRepository.findByEmail(request.getEmail().trim())
                .orElseThrow(()-> new UsernameNotFoundException("Email no encontrado: " + request.getEmail().trim()));

        if(!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash()))
            throw new BadCredentialsException("Contraseña incorrecta");

        List<Modulo> modules=Collections.emptyList();
        if(usuario.getRol()!=null){
            modules=moduloRepository.findByRolId(usuario.getRol().getIdRol());
        }

        var token= jwtService.generateToken(usuario,modules);
        return AuthResponseDTO.builder()
                .token(token)
                .build();

    }




}
