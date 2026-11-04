package dsw.sigconbackend.repository;

import dsw.sigconbackend.dto.NPersonasXTipoDocumento;
import dsw.sigconbackend.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PersonaRepository extends JpaRepository<Persona, Long>  {
    boolean existsByNumDocumento(String numDocumento);
    List<Persona> findByNumDocumento(String numDocumento);
    List<Persona> findAllByOrderByIdPersonaDesc();
    @Query(value = """
    SELECT tp.descripcion AS descripcion, COUNT(p.id_persona) AS cantidad
    FROM persona p
    JOIN tipo_documento tp ON p.id_tipo_documento = tp.id_tipo_documento
    GROUP BY tp.descripcion
    ORDER BY tp.descripcion
    """, nativeQuery = true)
    List<NPersonasXTipoDocumento> getNPersonasXTipoDocumento();
}
