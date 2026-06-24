package cl.vetnova.soporte.repository;

import cl.vetnova.soporte.model.EscalamientoTicket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EscalamientoTicketRepository extends JpaRepository<EscalamientoTicket, Long> {
    boolean existsByTicketIdAndEstado(Long ticketId, String estado);
}
