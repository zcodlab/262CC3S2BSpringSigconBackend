package dsw.sigconbackend.service;

import dsw.sigconbackend.dto.PersonaRequest;
import dsw.sigconbackend.dto.PersonaResponse;
import dsw.sigconbackend.model.Persona;
import dsw.sigconbackend.repository.PersonaRepository;
import dsw.sigconbackend.repository.SexoRepository;
import dsw.sigconbackend.repository.TipoDocumentoRepository;
import dsw.sigconbackend.repository.UbigeoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PersonaService {
    private final Logger logger=LoggerFactory.getLogger(this.getClass());
    @Autowired
    PersonaRepository personaRepository;
    @Autowired
    TipoDocumentoRepository tipoDocumentoRepository;
    @Autowired
    UbigeoRepository ubigeoRepository;
    @Autowired
    SexoRepository sexoRepository;
    
    public List<PersonaResponse> listPersonas(){
        return PersonaResponse.fromEntities(personaRepository.findAllByOrderByIdPersonaDesc());
    }
    public PersonaResponse findPersona(Long id){
        return personaRepository.findById(id)
                .map(PersonaResponse::fromEntity)
                .orElse(null);                
    }    
    public PersonaResponse findByNumdocumento(String nDocumento){
        List<Persona> list = personaRepository.findByNumDocumento(nDocumento);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return PersonaResponse.fromEntity(list.get(0));
    }
    
    @Transactional
    public PersonaResponse insertPersona(PersonaRequest personaRequest){
        // Reutilizamos el metodo toEntity de PersonaRequest
        Persona persona = PersonaRequest.toEntity(personaRequest);
        persona.setCreatedAt(java.time.LocalDateTime.now());
        persona.setUpdatedAt(java.time.LocalDateTime.now());

        persona = personaRepository.saveAndFlush(persona);        
        PersonaResponse personaResponse = PersonaResponse.fromEntity(persona);        
        return personaResponse;
    } 
    
    @Transactional
    public PersonaResponse updatePersona(PersonaRequest personaRequest){
        // Reutilizamos el metodo toEntity de PersonaRequest
        Persona persona = PersonaRequest.toEntity(personaRequest);
        persona.setCreatedAt(java.time.LocalDateTime.now());
        persona.setUpdatedAt(java.time.LocalDateTime.now());
        persona = personaRepository.saveAndFlush(persona);
        PersonaResponse personaResponse = PersonaResponse.fromEntity(persona);
        return personaResponse;
    }   
    
    @Transactional
    public void deletePersona(Long id){
        try {
            personaRepository.deleteById(id);
            personaRepository.flush();
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            String detail = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
            String userFriendlyMessage = "No se puede eliminar la persona porque se encuentra registrada y referenciada en otras tablas del sistema (ej. propietario).";
            if (detail != null && detail.contains("propietario")) {
                userFriendlyMessage = "No se puede eliminar la persona (ID " + id + ") porque se encuentra registrada como propietario en la base de datos.";
            }
            throw new IllegalArgumentException(userFriendlyMessage);
        }
    }
}
