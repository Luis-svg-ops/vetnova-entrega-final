package cl.vetnova.soporte.service;

import cl.vetnova.soporte.client.AuthClient;
import cl.vetnova.soporte.dto.RespuestaRequest;
import cl.vetnova.soporte.exception.BusinessRuleException;
import cl.vetnova.soporte.exception.ResourceNotFoundException;
import cl.vetnova.soporte.model.RespuestaTicket;
import cl.vetnova.soporte.model.Ticket;
import cl.vetnova.soporte.repository.RespuestaTicketRepository;
import cl.vetnova.soporte.repository.TicketRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RespuestaTicketService {

    private final RespuestaTicketRepository respuestaRepository;
    private final TicketRepository ticketRepository;
    private final AuthClient authClient;

    public RespuestaTicketService(RespuestaTicketRepository respuestaRepository, TicketRepository ticketRepository,
                                  AuthClient authClient) {
        this.respuestaRepository = respuestaRepository;
        this.ticketRepository = ticketRepository;
        this.authClient = authClient;
    }

    @Transactional
    public RespuestaTicket registrar(RespuestaRequest request) {
        if (request.getTicketId() == null) {
            throw new BusinessRuleException("El ticketId es obligatorio");
        }
        Ticket ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado"));
        if ("CERRADO".equals(ticket.getEstado())) {
            throw new BusinessRuleException("No se puede responder un ticket cerrado");
        }
        if (request.getAutorId() == null) {
            throw new BusinessRuleException("El autorId es obligatorio");
        }
        if (!authClient.usuarioExiste(request.getAutorId())) {
            throw new ResourceNotFoundException("Autor no encontrado");
        }
        if (request.getContenido() == null) {
            throw new BusinessRuleException("El contenido es obligatorio");
        }
        if (request.getContenido().trim().isEmpty()) {
            throw new BusinessRuleException("El contenido no puede estar vacío");
        }
        RespuestaTicket respuesta = new RespuestaTicket();
        respuesta.setTicketId(ticket.getId());
        respuesta.setAutorId(request.getAutorId());
        respuesta.setContenido(request.getContenido());
        respuesta.setVisible(request.getVisible() == null || request.getVisible());
        respuesta.setFecha(LocalDateTime.now(ZoneOffset.UTC));
        return respuestaRepository.save(respuesta);
    }

    @Transactional(readOnly = true)
    public List<RespuestaTicket> listarPorTicket(Long ticketId) {
        return respuestaRepository.findByTicketIdOrderByFechaAsc(ticketId);
    }
}
