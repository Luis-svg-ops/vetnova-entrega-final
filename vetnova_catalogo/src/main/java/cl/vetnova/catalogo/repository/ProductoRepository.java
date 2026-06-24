package cl.vetnova.catalogo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.vetnova.catalogo.model.Producto;
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByCategoriaId(Long categoriaId);
    List<Producto> findByActivoTrueAndNombreContainingIgnoreCase(String nombre);
    List<Producto> findByCategoriaIdAndActivoTrue(Long categoriaId);
    List<Producto> findByActivoTrueAndPrecioBetween(Double min, Double max);
    List<Producto> findByActivoTrue();
}
