package cl.vetnova.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.vetnova.inventario.model.AlertaStock;

@Repository
public interface AlertaStockRepository extends JpaRepository<AlertaStock, Long> {
    boolean existsByInventarioIdAndTipoAndLeidaFalse(Long inventarioId, String tipo);
}
