package cl.vetnova.auth.repository;

import cl.vetnova.auth.model.RolPermiso;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolPermisoRepository extends JpaRepository<RolPermiso, Long> {
    Optional<RolPermiso> findByNombreRolIgnoreCase(String nombreRol);
    boolean existsByNombreRolIgnoreCase(String nombreRol);
}
