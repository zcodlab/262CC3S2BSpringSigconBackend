package dsw.sigconbackend.dto;

import dsw.sigconbackend.model.Persona;
import dsw.sigconbackend.model.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolResponse {
    private Integer idRol;
    private String codigo;
    private String descripcion;
    private Boolean activo;
    public static RolResponse fromEntity(Rol rol){
        return RolResponse.builder()
                .idRol(rol.getIdRol())
                .codigo(rol.getCodigo())
                .descripcion(rol.getDescripcion())
                .activo(rol.getActivo())
                .build();
    }
    public static List<RolResponse> fromEntities(List<Rol> rol){
        return rol.stream()
                .map(RolResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
