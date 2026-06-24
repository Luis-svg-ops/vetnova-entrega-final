package cl.vetnova.soporte.repository;

import cl.vetnova.soporte.model.RespuestaTicket;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RespuestaTicketRepository extends JpaRepository<RespuestaTicket, Long> {
    List<RespuestaTicket> findByTicketIdOrderByFechaAsc(Long ticketId);
}
