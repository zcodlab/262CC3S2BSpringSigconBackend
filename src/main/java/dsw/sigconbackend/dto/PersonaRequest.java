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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaRequest {
    private Long idPersona;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String nombres;
    private String idSexo;
    private LocalDate fechaNacimiento;
    private Integer idTipoDocumento;
    private String numDocumento;
    private String direccion;
    private String telefono;
    private String idUbigeo;

    public static Persona toEntity(PersonaRequest personaRequest){
        Persona persona = new Persona();
        if(personaRequest.getIdPersona()!=null && personaRequest.getIdPersona()>0)
            persona.setIdPersona(persona.getIdPersona());
        else
            persona.setIdPersona(null);

        persona.setApellidoPaterno(personaRequest.getApellidoPaterno());
        persona.setApellidoMaterno(personaRequest.getApellidoMaterno());
        persona.setNombres(personaRequest.getNombres());
        persona.setFechaNacimiento(personaRequest.getFechaNacimiento());
        persona.setDireccion(personaRequest.getDireccion());
        persona.setNumDocumento(personaRequest.getNumDocumento());
        persona.setTelefono(personaRequest.getTelefono());
        if(personaRequest.getIdSexo()!=null)
            persona.setSexo(Sexo.builder().idSexo(personaRequest.getIdSexo()).build());
        if(personaRequest.getIdTipoDocumento()!=null)
            persona.setTipoDocumento(TipoDocumento.builder().idTipoDocumento(personaRequest.getIdTipoDocumento()).build());
        if(personaRequest.getIdUbigeo()!=null)
            persona.setUbigeo(Ubigeo.builder().idUbigeo(personaRequest.getIdUbigeo()).build());
        return persona;
    }


}
