package cl.vetnova.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.vetnova.inventario.model.TransferenciaStock;

@Repository
public interface TransferenciaStockRepository extends JpaRepository<TransferenciaStock, Long> {
}
