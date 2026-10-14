package dsw.sigconbackend.dto;

import dsw.sigconbackend.model.Modulo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuloRequest {
    private Integer idMod;
    private String descripcion;
    private String url;
    private Boolean activo;
    public static Modulo toEntity(ModuloRequest request){
        Modulo modulo = new Modulo();
        if(request.getIdMod()!=null && request.getIdMod()>0)
            modulo.setIdMod(request.getIdMod());
        else
            modulo.setIdMod(null);
        modulo.setDescripcion(request.getDescripcion());
        modulo.setUrl(request.getUrl());
        modulo.setActivo(request.getActivo());
        return modulo;
    }
}
