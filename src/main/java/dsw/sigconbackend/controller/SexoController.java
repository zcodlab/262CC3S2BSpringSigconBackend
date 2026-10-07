package dsw.sigconbackend.controller;

import dsw.sigconbackend.model.Sexo;
import dsw.sigconbackend.service.SexoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path="api/v1/sexo")
public class SexoController {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    SexoService sexoService;

    @GetMapping
    public ResponseEntity<?> getSexo(){
        List<Sexo> listaSexo = sexoService.getSexo();
        return ResponseEntity.ok(listaSexo);
    }
}
