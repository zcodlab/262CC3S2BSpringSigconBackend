package dsw.sigconbackend.service;

import dsw.sigconbackend.model.Sexo;
import dsw.sigconbackend.repository.SexoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SexoService {
    @Autowired
    SexoRepository sexoRepository;

    public List<Sexo> getSexo(){
        return sexoRepository.findAll();
    }
}
