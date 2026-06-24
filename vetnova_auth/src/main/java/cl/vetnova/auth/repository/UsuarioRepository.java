package cl.vetnova.auth.repository;

import cl.vetnova.auth.model.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    long countByRolNombreRolAndActivoTrue(String nombreRol);
    boolean existsByRolId(Long rolId);
}
