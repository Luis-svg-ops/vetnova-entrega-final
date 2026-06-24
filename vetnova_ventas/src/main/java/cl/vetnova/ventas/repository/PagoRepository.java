package cl.vetnova.ventas.repository;

import cl.vetnova.ventas.model.Pago;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByOrdenId(Long ordenId);

    boolean existsByReferencia(String referencia);

    boolean existsByOrdenIdAndEstado(Long ordenId, String estado);
}
