package dsw.sigconbackend.repository;

import dsw.sigconbackend.model.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuloRepository extends JpaRepository<Modulo, Integer> {
    @Query("SELECT m FROM Modulo m JOIN RolModulo rm ON m.idMod = rm.idMod WHERE m.activo='true' and rm.idRol = :idRol")
    List<Modulo> findByRolId(@Param("idRol") Integer idRol);
}
