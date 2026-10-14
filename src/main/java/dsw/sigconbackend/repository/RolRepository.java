package dsw.sigconbackend.repository;

import dsw.sigconbackend.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    @Query("select r from Rol r where r.activo = true "
            + "and (:descripcion is null or r.descripcion like CONCAT('%', :descripcion, '%')) "
            + "order by r.idRol desc")
    List<Rol> findByFilter(@Param("descripcion") String descripcion);
}
