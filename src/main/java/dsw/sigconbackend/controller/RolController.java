package dsw.sigconbackend.controller;

import dsw.sigconbackend.dto.PersonaRequest;
import dsw.sigconbackend.dto.PersonaResponse;
import dsw.sigconbackend.dto.RolRequest;
import dsw.sigconbackend.dto.RolResponse;
import dsw.sigconbackend.model.Persona;
import dsw.sigconbackend.service.PersonaService;
import dsw.sigconbackend.service.RolService;
import dsw.sigconbackend.util.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path="/api/v1/rol")
public class RolController {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    RolService rolService;

    @PostMapping("/list")
    public ResponseEntity<?> list(@RequestBody RolRequest request) {
        try {
            return ResponseEntity.ok(rolService.list(request));
        } catch (Exception e) {
            logger.error("Error list "+this.getClass().getName(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.builder().status(404).error("Not Found").message("Rol not found").build());
        }
    }

    @PostMapping("/find")
    public ResponseEntity<?> find(@RequestBody Optional<RolRequest> request){
        logger.info(">find " + request.toString());
        if (request.isEmpty() || request.get().getIdRol() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.builder().status(400).error("Bad Request").message("El ID del rol es obligatorio").build());
        }
        RolResponse rolResponse = rolService.find(request.get().getIdRol());
        if (rolResponse == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.builder().status(404).error("Not Found").message("Rol not found").build());
        return ResponseEntity.ok(rolResponse);
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody RolRequest request){
        logger.info(">insert " + request.toString());
        RolResponse rolResponse = rolService.save(request);
        if (rolResponse == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.builder().status(404).error("Not Found").message("Rol not insert").build());
        return ResponseEntity.ok(rolResponse);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(
            @RequestBody(required = false) RolRequest request,
            @RequestParam(required = false) Integer idRol) {

        Integer targetId = null;
        if (request != null && request.getIdRol() != null) {
            targetId = request.getIdRol();
        } else if (idRol != null) {
            targetId = idRol;
        }

        if (targetId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.builder()
                            .status(400)
                            .error("Bad Request")
                            .message("El ID del rol es obligatorio para eliminar")
                            .build());
        }

        RolResponse rolResponse = rolService.find(targetId);
        if (rolResponse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.builder()
                            .status(404)
                            .error("Not Found")
                            .message("Rol not found for delete")
                            .build());
        }

        rolService.delete(targetId);
        return ResponseEntity.ok(rolResponse);
    }

}
