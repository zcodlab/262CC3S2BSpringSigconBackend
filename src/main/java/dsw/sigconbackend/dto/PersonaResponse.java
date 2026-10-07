package dsw.sigconbackend.dto;

import dsw.sigconbackend.model.Persona;
import dsw.sigconbackend.model.Sexo;
import dsw.sigconbackend.model.TipoDocumento;
import dsw.sigconbackend.model.Ubigeo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaResponse {
    private Long idPersona;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String nombres;
    private Sexo sexo;
    private LocalDate fechaNacimiento;
    private TipoDocumento tipoDocumento;
    private String numDocumento;
    private String direccion;
    private String telefono;
    private Ubigeo ubigeo;

    public static PersonaResponse fromEntity(Persona persona){
        return PersonaResponse.builder()
                .idPersona(persona.getIdPersona())
                .apellidoPaterno(persona.getApellidoPaterno())
                .apellidoMaterno(persona.getApellidoMaterno())
                .nombres(persona.getNombres())
                .sexo(persona.getSexo())
                .fechaNacimiento(persona.getFechaNacimiento())
                .tipoDocumento(persona.getTipoDocumento())
                .numDocumento(persona.getNumDocumento())
                .direccion(persona.getDireccion())
                .telefono(persona.getTelefono())
                .ubigeo(persona.getUbigeo())
                .build();
    }
    public static List<PersonaResponse> fromEntities(List<Persona> persona){
        return persona.stream()
                .map(PersonaResponse::fromEntity)
                .collect(Collectors.toList());
    }

}
