package cl.vetnova.soporte.repository;

import cl.vetnova.soporte.model.DerivacionTicket;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DerivacionTicketRepository extends JpaRepository<DerivacionTicket, Long> {
    List<DerivacionTicket> findByTicketIdOrderByFechaAsc(Long ticketId);
}
