package cl.vetnova.reportes.repository;

import cl.vetnova.reportes.model.ReporteAtencion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReporteAtencionRepository extends JpaRepository<ReporteAtencion, Long> {

    boolean existsByReporteId(Long reporteId);
}
