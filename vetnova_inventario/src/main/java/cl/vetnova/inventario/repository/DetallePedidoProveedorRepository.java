package cl.vetnova.inventario.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.vetnova.inventario.model.DetallePedidoProveedor;

@Repository
public interface DetallePedidoProveedorRepository extends JpaRepository<DetallePedidoProveedor, Long> {
    List<DetallePedidoProveedor> findByPedidoId(Long pedidoId);

    boolean existsByPedidoIdAndProductoId(Long pedidoId, Long productoId);

    long countByPedidoId(Long pedidoId);
}
