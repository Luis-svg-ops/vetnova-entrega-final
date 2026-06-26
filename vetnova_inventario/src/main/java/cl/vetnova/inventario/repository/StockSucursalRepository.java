package cl.vetnova.inventario.repository;

import cl.vetnova.inventario.model.StockSucursal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StockSucursalRepository extends JpaRepository<StockSucursal, Long> {
    Optional<StockSucursal> findByProductoIdAndIdSucursal(Long productoId, String idSucursal);
    List<StockSucursal> findByProductoId(Long productoId);

    @Query("SELECT s FROM StockSucursal s WHERE s.cantidad <= s.stockMinimo")
    List<StockSucursal> findStockCritico();
}
