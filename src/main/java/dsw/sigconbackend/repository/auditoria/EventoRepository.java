package dsw.sigconbackend.repository.auditoria;

import dsw.sigconbackend.model.auditoria.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad {@link Evento}.
 * Permite persistir y consultar registros en auditoria.evento.
 */
@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
}
