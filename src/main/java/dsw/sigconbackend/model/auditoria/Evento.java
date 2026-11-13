package dsw.sigconbackend.model.auditoria;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnTransformer;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entidad JPA que mapea la tabla auditoria.evento.
 * Registra cada operación (INSERT, UPDATE, DELETE) realizada
 * sobre cualquier entidad del sistema.
 */
@Entity
@Table(name = "evento", schema = "auditoria")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento")
    private Long idEvento;

    // ── Identificación del registro afectado ──────────────────
    @Column(name = "nombre_esquema", nullable = false, length = 100)
    private String nombreEsquema;

    @Column(name = "nombre_tabla", nullable = false, length = 100)
    private String nombreTabla;

    /** PK del registro afectado (serializada como String para genericidad). */
    @Column(name = "id_registro", nullable = false, length = 150)
    private String idRegistro;

    // ── Identificación del responsable ────────────────────────
    /** ID del usuario autenticado que ejecutó la operación. 0 = anónimo. */
    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    // ── Información de la operación ───────────────────────────
    @Column(name = "modulo", length = 100)
    private String modulo;

    /** INSERT | UPDATE | DELETE */
    @Column(name = "operacion", nullable = false, length = 30)
    private String operacion;

    @Column(name = "fecha_evento", nullable = false)
    @Builder.Default
    private OffsetDateTime fechaEvento = OffsetDateTime.now();

    // ── Información de conexión ───────────────────────────────
    // @ColumnTransformer(write) inyecta "?::inet" en el SQL generado por Hibernate,
    // delegando el cast al servidor PostgreSQL y evitando conflictos de tipo JDBC.
    @ColumnTransformer(write = "?::inet")
    @Column(name = "ip_origen", columnDefinition = "inet")
    private String ipOrigen;

    // ── Información HTTP ──────────────────────────────────────
    @Column(name = "metodo_http", length = 10)
    private String metodoHttp;

    @Column(name = "endpoint", length = 500)
    private String endpoint;

    @Column(name = "codigo_http")
    private Integer codigoHttp;

    // ── Resultado ─────────────────────────────────────────────
    /** SUCCESS | FAILURE */
    @Column(name = "resultado", nullable = false, length = 30)
    private String resultado;

    @Column(name = "motivo", length = 500)
    private String motivo;

    // ── Correlación ───────────────────────────────────────────
    @Column(name = "id_interaccion", columnDefinition = "UUID")
    private UUID idInteraccion;

    // ── Identificación de la aplicación ──────────────────────
    @Column(name = "version_aplicacion", length = 50)
    private String versionAplicacion;
}
