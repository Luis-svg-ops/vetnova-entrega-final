package cl.vetnova.notificaciones.repository;

import cl.vetnova.notificaciones.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuarioIdOrderByFechaEnvioDesc(Long usuarioId);
    List<Notificacion> findByUsuarioIdAndLeidaOrderByFechaEnvioDesc(Long usuarioId, Boolean leida);
    long countByUsuarioIdAndLeidaFalse(Long usuarioId);
}
