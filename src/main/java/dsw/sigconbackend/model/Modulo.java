package dsw.sigconbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "modulo")
public class Modulo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_mod")
    private Integer idMod;

    @Column(nullable = false, unique = true)
    private String descripcion;

    @Column(nullable = false, unique = true)
    private String url;

    @Column(nullable = false, unique = true)
    private Boolean activo;
}
