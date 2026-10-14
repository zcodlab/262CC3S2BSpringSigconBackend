package dsw.sigconbackend.controller;

import dsw.sigconbackend.dto.ModuloRequest;
import dsw.sigconbackend.dto.ModuloResponse;
import dsw.sigconbackend.dto.RolRequest;
import dsw.sigconbackend.dto.RolResponse;
import dsw.sigconbackend.service.ModuloService;
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
@RequestMapping(path="/api/v1/modulo")
public class ModuloController {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    ModuloService moduloService;

    @PostMapping("/list")
    public ResponseEntity<?> list(@RequestBody ModuloRequest request) {
        try {
            return ResponseEntity.ok(moduloService.list(request));
        } catch (Exception e) {
            logger.error("Error list "+this.getClass().getName(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.builder().status(404).error("Not Found").message("Modulo not found").build());
        }
    }

    @PostMapping("/find")
    public ResponseEntity<?> find(@RequestBody Optional<ModuloRequest> request){
        logger.info(">find " + request.toString());
        if (request.isEmpty() || request.get().getIdMod() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.builder().status(400).error("Bad Request").message("El ID del Modulo es obligatorio").build());
        }
        ModuloResponse moduloResponse = moduloService.find(request.get().getIdMod());
        if (moduloResponse == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.builder().status(404).error("Not Found").message("Modulo not found").build());
        return ResponseEntity.ok(moduloResponse);
    }


}
