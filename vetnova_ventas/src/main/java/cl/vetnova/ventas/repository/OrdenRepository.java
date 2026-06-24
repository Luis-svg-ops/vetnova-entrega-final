package cl.vetnova.ventas.repository;

import cl.vetnova.ventas.model.Orden;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenRepository extends JpaRepository<Orden, Long> {
}
