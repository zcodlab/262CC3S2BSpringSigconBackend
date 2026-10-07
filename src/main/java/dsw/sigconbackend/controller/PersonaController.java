package dsw.sigconbackend.controller;

import dsw.sigconbackend.dto.PersonaRequest;
import dsw.sigconbackend.dto.PersonaResponse;
import dsw.sigconbackend.model.Persona;
import dsw.sigconbackend.service.PersonaService;
import dsw.sigconbackend.util.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path="/api/v1/persona")
public class PersonaController {
    private final Logger logger=LoggerFactory.getLogger(this.getClass());
    @Autowired
    PersonaService personaService;        
    
    @GetMapping()
    public ResponseEntity<?> getPersonas(){
        List<PersonaResponse> listaPersonaResponse= Collections.emptyList();
        try{
            listaPersonaResponse=personaService.listPersonas();
        }catch(Exception e){
            logger.error("Error inesperado", e);
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (listaPersonaResponse.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder().message("Personas not found").build());
        return ResponseEntity.ok(listaPersonaResponse);
    }

    @PostMapping("/find")
    public ResponseEntity<?> findPersonaById(@RequestBody Optional<PersonaRequest> personaRequest){
        logger.info(">find" +  personaRequest.toString());
        PersonaResponse personaResponse;
        try{
            personaResponse=personaService.findPersona(personaRequest.get().getIdPersona());
        }catch(Exception e){
            logger.error("Error inesperado", e);
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (personaResponse==null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder().message("Persona not found").build());           
        return ResponseEntity.ok(personaResponse);        
        
    }
    
    @PostMapping("/findNumdocumento")
    public ResponseEntity<?> findByNumdocumento(@RequestBody PersonaRequest personaRequest){
        logger.info(">findNumdocumento" +  personaRequest.toString());
        PersonaResponse personaResponse;
        try{
            personaResponse=personaService.findByNumdocumento(personaRequest.getNumDocumento());
        }catch(Exception e){
            logger.error("Error inesperado", e);
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (personaResponse==null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder().message("Persona-Ndocumento not found").build());           
        return ResponseEntity.ok(personaResponse);        
        
    }
    
    @PostMapping()
    public ResponseEntity<?> insertPersona(@RequestBody PersonaRequest personaRequest){
        logger.info(">insert " +  personaRequest.toString());
        PersonaResponse personaResponse;
        try{            
            personaResponse=(PersonaResponse)personaService.insertPersona(personaRequest);
        }catch(Exception e){
            logger.error("Error inesperado", e);
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (personaResponse==null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder().message("Persona not insert").build());           
        return ResponseEntity.ok(personaResponse);        
    }    
    
    @PutMapping()
    public ResponseEntity<?> updatePersona(@RequestBody PersonaRequest personaRequest){
        logger.info(">update1" +  personaRequest.toString());
        PersonaResponse personaResponse;
        try{
            personaResponse=personaService.findPersona(personaRequest.getIdPersona());

            if (personaResponse==null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder().message("Persona not found").build());

            personaResponse=personaService.updatePersona(personaRequest);
        }catch(Exception e){
            logger.error("Error inesperado", e);
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (personaResponse==null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder().message("Persona not update").build());           
        return ResponseEntity.ok(personaResponse);  
    }    
    
    @DeleteMapping()
    public ResponseEntity<?> deletePersona(@RequestBody Optional<Persona> personaRequest){
        logger.info(">delete" +  personaRequest.toString());
        PersonaResponse personaResponse;
        try{            
            personaResponse=personaService.findPersona(personaRequest.get().getIdPersona());
            if(!personaRequest.isPresent())                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder().message("Persona not found for delete").build());  
            personaService.deletePersona(personaRequest.get().getIdPersona());                        
        }catch(Exception e){
            logger.error("Error inesperado", e);
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }        
        return ResponseEntity.ok(personaResponse);  
    }
    
}
