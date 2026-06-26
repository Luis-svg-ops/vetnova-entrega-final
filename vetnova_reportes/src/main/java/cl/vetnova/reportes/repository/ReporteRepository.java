package cl.vetnova.reportes.repository;

import cl.vetnova.reportes.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    List<Reporte> findBySucursal(String sucursal);
    List<Reporte> findByTipo(String tipo);
}
