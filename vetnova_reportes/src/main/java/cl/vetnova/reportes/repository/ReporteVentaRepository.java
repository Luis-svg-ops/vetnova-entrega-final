package cl.vetnova.reportes.repository;

import cl.vetnova.reportes.model.ReporteVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReporteVentaRepository extends JpaRepository<ReporteVenta, Long> {

    boolean existsByReporteId(Long reporteId);
}
