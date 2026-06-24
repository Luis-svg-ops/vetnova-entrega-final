package cl.vetnova.notificaciones.repository;

import cl.vetnova.notificaciones.model.HistorialMensaje;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialMensajeRepository extends JpaRepository<HistorialMensaje, Long> {
    List<HistorialMensaje> findByNotificacionIdOrderByFechaEnvioAsc(Long notificacionId);
    List<HistorialMensaje> findByCanalIdOrderByFechaEnvioDesc(Long canalId);
    List<HistorialMensaje> findByEstado(String estado);
    boolean existsByCanalId(Long canalId);
}
