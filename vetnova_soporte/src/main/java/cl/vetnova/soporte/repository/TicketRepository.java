package cl.vetnova.soporte.repository;

import cl.vetnova.soporte.model.Ticket;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByClienteId(Long clienteId);
    List<Ticket> findByEstadoIgnoreCase(String estado);
    boolean existsByCategoria_Id(Long categoriaId);
}
