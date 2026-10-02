package dsw.sigconbackend.service;

import dsw.sigconbackend.model.TipoDocumento;
import dsw.sigconbackend.repository.TipoDocumentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoDocumentoService {

    @Autowired
    TipoDocumentoRepository tipoDocumentoRepository;

    public List<TipoDocumento> getTipoDocumentos(){

        return tipoDocumentoRepository.findAll();
    }
}

