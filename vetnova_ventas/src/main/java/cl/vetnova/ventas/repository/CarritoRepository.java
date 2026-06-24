package cl.vetnova.ventas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.vetnova.ventas.model.Carrito;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    boolean existsByClienteIdAndActivoTrue(Long clienteId);
}
