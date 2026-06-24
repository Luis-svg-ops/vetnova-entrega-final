package cl.vetnova.ventas.repository;

import cl.vetnova.ventas.model.DetalleOrden;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, Long> {
    List<DetalleOrden> findByOrdenId(Long ordenId);
}
