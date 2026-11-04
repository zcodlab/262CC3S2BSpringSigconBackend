package dsw.sigconbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NPersonasXTipoDocumento {
    String descripcion;
    Long cantidad;
}
