package cl.vetnova.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.vetnova.inventario.model.ProveedorProducto;

@Repository
public interface ProveedorProductoRepository extends JpaRepository<ProveedorProducto, Long> {
    boolean existsByProveedorIdAndProductoId(Long proveedorId, Long productoId);
}
