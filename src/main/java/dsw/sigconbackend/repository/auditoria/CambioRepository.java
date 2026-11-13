package dsw.sigconbackend.repository.auditoria;

import dsw.sigconbackend.model.auditoria.Cambio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad {@link Cambio}.
 * Permite persistir y consultar registros en auditoria.cambio.
 */
@Repository
public interface CambioRepository extends JpaRepository<Cambio, Long> {
}
