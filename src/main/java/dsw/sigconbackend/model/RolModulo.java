package dsw.sigconbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(RolModuloId.class)
@Table(name = "rol_modulo")
public class RolModulo {
    @Id
    @Column(name="id_rol")
    private Integer idRol;

    @Id
    @Column(name="id_mod")
    private Integer idMod;

    @Column(nullable = false)
    private Boolean activo;

    @Column(name = "created_at",nullable = true, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at",nullable = true )
    private LocalDateTime updatedAt = LocalDateTime.now();

}

@Data
@NoArgsConstructor
@AllArgsConstructor
class RolModuloId implements Serializable {
    private Integer idRol;
    private Integer idMod;
}
