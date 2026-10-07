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
        List<PersonaResponse> listaPersonaResponse = personaService.listPersonas();
        if (listaPersonaResponse.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.builder().status(404).error("Not Found").message("Personas not found").build());
        return ResponseEntity.ok(listaPersonaResponse);
    }

    @PostMapping("/find")
    public ResponseEntity<?> findPersonaById(@RequestBody Optional<PersonaRequest> personaRequest){
        logger.info(">find " + personaRequest.toString());
        if (personaRequest.isEmpty() || personaRequest.get().getIdPersona() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.builder().status(400).error("Bad Request").message("El ID de la persona es obligatorio").build());
        }
        PersonaResponse personaResponse = personaService.findPersona(personaRequest.get().getIdPersona());
        if (personaResponse == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.builder().status(404).error("Not Found").message("Persona not found").build());           
        return ResponseEntity.ok(personaResponse);        
    }
    
    @PostMapping("/findNumdocumento")
    public ResponseEntity<?> findByNumdocumento(@RequestBody PersonaRequest personaRequest){
        logger.info(">findNumdocumento " + personaRequest.toString());
        if (personaRequest == null || personaRequest.getNumDocumento() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.builder().status(400).error("Bad Request").message("El número de documento es obligatorio").build());
        }
        PersonaResponse personaResponse = personaService.findByNumdocumento(personaRequest.getNumDocumento());
        if (personaResponse == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.builder().status(404).error("Not Found").message("Persona-Ndocumento not found").build());           
        return ResponseEntity.ok(personaResponse);        
    }
    
    @PostMapping()
    public ResponseEntity<?> insertPersona(@RequestBody PersonaRequest personaRequest){
        logger.info(">insert " + personaRequest.toString());
        PersonaResponse personaResponse = personaService.insertPersona(personaRequest);
        if (personaResponse == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.builder().status(404).error("Not Found").message("Persona not insert").build());           
        return ResponseEntity.ok(personaResponse);        
    }    
    
    @PutMapping()
    public ResponseEntity<?> updatePersona(@RequestBody PersonaRequest personaRequest){
        logger.info(">update " + personaRequest.toString());
        if (personaRequest == null || personaRequest.getIdPersona() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.builder().status(400).error("Bad Request").message("El ID de la persona es obligatorio para actualizar").build());
        }
        PersonaResponse personaResponse = personaService.findPersona(personaRequest.getIdPersona());
        if (personaResponse == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.builder().status(404).error("Not Found").message("Persona not found").build());

        personaResponse = personaService.updatePersona(personaRequest);
        if (personaResponse == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.builder().status(404).error("Not Found").message("Persona not update").build());           
        return ResponseEntity.ok(personaResponse);  
    }    
    
    @DeleteMapping()
    public ResponseEntity<?> deletePersona(
            @RequestBody(required = false) Persona personaRequest,
            @RequestParam(required = false) Long idPersona) {
        
        Long targetId = null;
        if (personaRequest != null && personaRequest.getIdPersona() != null) {
            targetId = personaRequest.getIdPersona();
        } else if (idPersona != null) {
            targetId = idPersona;
        }

        if (targetId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.builder()
                            .status(400)
                            .error("Bad Request")
                            .message("El ID de la persona es obligatorio para eliminar")
                            .build());
        }

        PersonaResponse personaResponse = personaService.findPersona(targetId);
        if (personaResponse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.builder()
                            .status(404)
                            .error("Not Found")
                            .message("Persona not found for delete")
                            .build());  
        }

        personaService.deletePersona(targetId);                        
        return ResponseEntity.ok(personaResponse);  
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePersonaById(@PathVariable("id") Long idPersona) {
        PersonaResponse personaResponse = personaService.findPersona(idPersona);
        if (personaResponse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.builder()
                            .status(404)
                            .error("Not Found")
                            .message("Persona not found for delete")
                            .build());  
        }
        personaService.deletePersona(idPersona);                        
        return ResponseEntity.ok(personaResponse);  
    }
}
