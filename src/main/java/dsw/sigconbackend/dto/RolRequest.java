package dsw.sigconbackend.dto;

import dsw.sigconbackend.model.Persona;
import dsw.sigconbackend.model.Rol;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolRequest {
    private Integer idRol;
    private String codigo;
    private String descripcion;
    private Boolean activo;

    public static Rol toEntity(RolRequest rolRequest){
        Rol rol = new Rol();
        if(rolRequest.getIdRol()!=null && rolRequest.getIdRol()>0)
            rol.setIdRol(rolRequest.getIdRol());
        else
            rol.setIdRol(null);
        rol.setCodigo(rolRequest.getCodigo());
        rol.setDescripcion(rolRequest.getDescripcion());
        rol.setActivo(rolRequest.getActivo());
        return rol;
    }
}
