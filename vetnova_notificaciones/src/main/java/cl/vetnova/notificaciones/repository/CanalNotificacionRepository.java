package cl.vetnova.notificaciones.repository;

import cl.vetnova.notificaciones.model.CanalNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CanalNotificacionRepository extends JpaRepository<CanalNotificacion, Long> {
    boolean existsByUsuarioIdAndTipo(Long usuarioId, String tipo);
    Optional<CanalNotificacion> findByUsuarioIdAndTipo(Long usuarioId, String tipo);
    List<CanalNotificacion> findByUsuarioId(Long usuarioId);
}
