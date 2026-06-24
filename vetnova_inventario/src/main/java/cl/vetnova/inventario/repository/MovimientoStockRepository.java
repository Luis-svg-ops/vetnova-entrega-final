package cl.vetnova.inventario.repository;

import cl.vetnova.inventario.model.MovimientoStock;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoStockRepository extends JpaRepository<MovimientoStock, Long> {
    List<MovimientoStock> findByInventarioIdOrderByFechaDesc(Long inventarioId);
}
