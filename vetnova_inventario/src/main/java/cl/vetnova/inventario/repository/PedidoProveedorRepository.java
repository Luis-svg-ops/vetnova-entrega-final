package cl.vetnova.inventario.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.vetnova.inventario.model.PedidoProveedor;

@Repository
public interface PedidoProveedorRepository extends JpaRepository<PedidoProveedor, Long> {
    boolean existsByProveedorIdAndEstadoIn(Long proveedorId, Collection<String> estados);
}
