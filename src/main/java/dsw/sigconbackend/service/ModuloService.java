package dsw.sigconbackend.service;

import dsw.sigconbackend.dto.ModuloRequest;
import dsw.sigconbackend.dto.ModuloResponse;
import dsw.sigconbackend.repository.ModuloRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModuloService {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());
    @Autowired
    ModuloRepository moduloRepository;

    public List<ModuloResponse> list(ModuloRequest filter){
        return ModuloResponse.fromEntities(moduloRepository.findByFilter(filter.getDescripcion()));
    }

    public ModuloResponse find(Integer id){
        return moduloRepository.findById(id)
                .map(ModuloResponse::fromEntity)
                .orElse(null);
    }

}
