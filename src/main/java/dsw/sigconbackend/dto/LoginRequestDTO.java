package dsw.sigconbackend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @JsonAlias({"username", "user_name"})
    private String email;
    private String password;
}
