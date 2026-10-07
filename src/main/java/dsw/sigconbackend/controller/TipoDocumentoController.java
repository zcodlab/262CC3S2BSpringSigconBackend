package dsw.sigconbackend.controller;

import dsw.sigconbackend.model.TipoDocumento;
import dsw.sigconbackend.service.TipoDocumentoService;
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
@RequestMapping(path="api/v1/tipodocumento")
public class TipoDocumentoController {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    TipoDocumentoService tipoDocumentoService;

    @GetMapping
    public ResponseEntity<?> getTipoDocumentos(){
        List<TipoDocumento> listaTipoDocumento = tipoDocumentoService.getTipoDocumentos();
        return ResponseEntity.ok(listaTipoDocumento);
    }

}
