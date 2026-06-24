package cl.vetnova.reportes.repository;

import cl.vetnova.reportes.model.ReporteStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReporteStockRepository extends JpaRepository<ReporteStock, Long> {

    boolean existsByReporteId(Long reporteId);
}
