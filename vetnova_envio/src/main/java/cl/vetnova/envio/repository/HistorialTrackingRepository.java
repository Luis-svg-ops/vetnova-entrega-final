package cl.vetnova.envio.repository;

import cl.vetnova.envio.model.HistorialTracking;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialTrackingRepository extends JpaRepository<HistorialTracking, Long> {
    List<HistorialTracking> findByEnvioIdOrderByFechaAsc(Long envioId);
}
