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
        return PersonaResponse.fromEntity(personaRepository.findById(id).get());                
    }    
    public PersonaResponse findByNumdocumento(String nDocumento){
        return PersonaResponse.fromEntity((Persona) personaRepository.findByNumDocumento(nDocumento).get(0));
    }
    
    public PersonaResponse insertPersona(PersonaRequest personaRequest){
        // Reutilizamos el metodo toEntity de PersonaRequest
        Persona persona = PersonaRequest.toEntity(personaRequest);
        persona.setCreatedAt(java.time.LocalDateTime.now());
        persona.setUpdatedAt(java.time.LocalDateTime.now());

        persona=personaRepository.save(persona);        
        PersonaResponse personaResponse=PersonaResponse.fromEntity(persona);        
        return personaResponse;
    } 
    
    public PersonaResponse updatePersona(PersonaRequest personaRequest){
        // Reutilizamos el metodo toEntity de PersonaRequest
        Persona persona = PersonaRequest.toEntity(personaRequest);
        persona.setCreatedAt(java.time.LocalDateTime.now());
        persona.setUpdatedAt(java.time.LocalDateTime.now());
        persona=personaRepository.save(persona);
        PersonaResponse personaResponse=PersonaResponse.fromEntity(persona);
        return personaResponse;
    }   
    
    public void deletePersona(Long id){
        personaRepository.deleteById(id);
    }

    
}
