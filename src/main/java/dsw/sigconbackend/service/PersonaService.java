package dsw.sigconbackend.service;

import dsw.sigconbackend.auditoria.AuditoriaService;
import dsw.sigconbackend.dto.NPersonasXTipoDocumento;
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
    @Autowired
    AuditoriaService auditoriaService;
    
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
        persona = personaRepository.saveAndFlush(persona);
        // Auditoria: registrar INSERT
        auditoriaService.registrarCreacion(
                "public", "persona", persona.getIdPersona().toString(), "Personas");
        PersonaResponse personaResponse = PersonaResponse.fromEntity(persona);
        return personaResponse;
    } 
    
    @Transactional
    public PersonaResponse updatePersona(PersonaRequest personaRequest){
        // Capturar estado anterior ANTES de modificar (para auditoria)
        // Se usa new Persona(snapshot) para desconectarlo del contexto de Hibernate:
        // sin el constructor de copia, saveAndFlush mutaría también estadoAnterior
        // porque Hibernate mantiene ambas referencias apuntando al mismo objeto gestionado.
        Persona estadoAnterior = personaRepository.findById(personaRequest.getIdPersona())
                .map(Persona::new)   // snapshot desconectado del contexto de persistencia
                .orElse(null);


        // Reutilizamos el metodo toEntity de PersonaRequest
        Persona persona = PersonaRequest.toEntity(personaRequest);
        persona.setCreatedAt(estadoAnterior.getCreatedAt());
        persona.setUpdatedAt(java.time.LocalDateTime.now());
        persona = personaRepository.saveAndFlush(persona);

        // Auditoria: registrar UPDATE con comparacion de campos
        auditoriaService.registrarActualizacion(
                "public", "persona", persona.getIdPersona().toString(), "Personas",
                estadoAnterior, persona);

        PersonaResponse personaResponse = PersonaResponse.fromEntity(persona);
        return personaResponse;
    }   
    
    @Transactional
    public void deletePersona(Long id){
        // Capturar estado anterior ANTES de eliminar (para auditoria)
        Persona estadoAnterior = personaRepository.findById(id).orElse(null);
        try {
            personaRepository.deleteById(id);
            personaRepository.flush();
            // Auditoria: registrar DELETE con el snapshot previo
            auditoriaService.registrarEliminacion(
                    "public", "persona", id.toString(), "Personas", estadoAnterior);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            String detail = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
            String userFriendlyMessage = "No se puede eliminar la persona porque se encuentra registrada y referenciada en otras tablas del sistema (ej. propietario).";
            if (detail != null && detail.contains("propietario")) {
                userFriendlyMessage = "No se puede eliminar la persona (ID " + id + ") porque se encuentra registrada como propietario en la base de datos.";
            }
            throw new IllegalArgumentException(userFriendlyMessage);
        }
    }
    public List<NPersonasXTipoDocumento> getNPersonasXTipoDocumento(){
        return personaRepository.getNPersonasXTipoDocumento();
    }
}
