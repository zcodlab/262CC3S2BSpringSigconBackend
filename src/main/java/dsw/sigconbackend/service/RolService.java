package dsw.sigconbackend.service;


import dsw.sigconbackend.dto.RolRequest;
import dsw.sigconbackend.dto.RolResponse;
import dsw.sigconbackend.model.Rol;
import dsw.sigconbackend.repository.RolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RolService {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    RolRepository rolRepository;

    public List<RolResponse> list(RolRequest filter){
        return RolResponse.fromEntities(rolRepository.findByFilter(filter.getDescripcion()));
    }

    public RolResponse find(Integer id){
        return rolRepository.findById(id)
                .map(RolResponse::fromEntity)
                .orElse(null);
    }

    @Transactional
    public RolResponse save(RolRequest rolRequest){
        Rol rol = RolRequest.toEntity(rolRequest);
        rol = rolRepository.saveAndFlush(rol);
        RolResponse rolResponse = RolResponse.fromEntity(rol);
        return rolResponse;
    }

    @Transactional
    public void delete(Integer id){
        try {
            rolRepository.deleteById(id);
            rolRepository.flush();
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            String detail = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
            String userFriendlyMessage = "No se puede eliminar el rol porque se encuentra registrada y referenciada en otras tablas del sistema.";
            if (detail != null && detail.contains("propietario")) {
                userFriendlyMessage = "No se puede eliminar el rol (ID " + id + ") porque se encuentra vinculada a usuarios en la base de datos.";
            }
            throw new IllegalArgumentException(userFriendlyMessage);
        }
    }

}
