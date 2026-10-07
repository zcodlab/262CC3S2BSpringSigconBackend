package dsw.sigconbackend.repository;

import dsw.sigconbackend.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonaRepository extends JpaRepository<Persona, Long>  {
    boolean existsByNumDocumento(String numDocumento);
    List<Persona> findByNumDocumento(String numDocumento);
    List<Persona> findAllByOrderByIdPersonaDesc();
}
