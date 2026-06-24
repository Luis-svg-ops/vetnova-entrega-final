package cl.vetnova.inventario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.vetnova.inventario.model.Inventario;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    boolean existsByProductoIdAndSucursal(Long productoId, String sucursal);

    Optional<Inventario> findByProductoIdAndSucursal(Long productoId, String sucursal);
}
