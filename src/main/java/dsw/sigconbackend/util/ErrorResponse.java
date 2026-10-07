package dsw.sigconbackend.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    @Builder.Default
    private String timestamp = LocalDateTime.now().toString();
    private Integer status;
    private String error;
    private String message;
    private String detail;
}
