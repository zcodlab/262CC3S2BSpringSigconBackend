package dsw.sigconbackend.model.auditoria;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA que mapea la tabla auditoria.cambio.
 * Conserva el detalle de cada campo modificado dentro de un evento.
 */
@Entity
@Table(name = "cambio", schema = "auditoria")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cambio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cambio")
    private Long idCambio;

    /** Evento al que pertenece este cambio de campo. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evento", nullable = false)
    private Evento evento;

    /** Nombre del campo (atributo Java o columna) que cambió. */
    @Column(name = "nombre_campo", nullable = false, length = 150)
    private String nombreCampo;

    /** Valor antes de la operación. Nulo en INSERT. */
    @Column(name = "valor_anterior", columnDefinition = "TEXT")
    private String valorAnterior;

    /** Valor después de la operación. Nulo en DELETE. */
    @Column(name = "valor_nuevo", columnDefinition = "TEXT")
    private String valorNuevo;
}
