package dsw.sigconbackend.dto;

import dsw.sigconbackend.model.Modulo;
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
public class ModuloResponse {
    private Integer idMod;
    private String descripcion;
    private String url;
    private Boolean activo;
    public static ModuloResponse fromEntity(Modulo modulo){
        return ModuloResponse.builder()
                .idMod(modulo.getIdMod())
                .descripcion(modulo.getDescripcion())
                .url(modulo.getUrl())
                .activo(modulo.getActivo())
                .build();
    }
    public static List<ModuloResponse> fromEntities(List<Modulo> modulo){
        return modulo.stream()
                .map(ModuloResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
