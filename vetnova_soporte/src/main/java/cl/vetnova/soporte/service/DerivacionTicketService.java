package cl.vetnova.soporte.service;

import cl.vetnova.soporte.client.AuthClient;
import cl.vetnova.soporte.dto.DerivacionRequest;
import cl.vetnova.soporte.exception.BusinessRuleException;
import cl.vetnova.soporte.exception.ResourceNotFoundException;
import cl.vetnova.soporte.model.DerivacionTicket;
import cl.vetnova.soporte.model.Ticket;
import cl.vetnova.soporte.repository.DerivacionTicketRepository;
import cl.vetnova.soporte.repository.TicketRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DerivacionTicketService {

    private final DerivacionTicketRepository derivacionRepository;
    private final TicketRepository ticketRepository;
    private final AuthClient authClient;

    public DerivacionTicketService(DerivacionTicketRepository derivacionRepository, TicketRepository ticketRepository,
                                   AuthClient authClient) {
        this.derivacionRepository = derivacionRepository;
        this.ticketRepository = ticketRepository;
        this.authClient = authClient;
    }

    @Transactional
    public DerivacionTicket registrar(DerivacionRequest request) {
        if (request.getTicketId() == null) {
            throw new BusinessRuleException("El ticketId es obligatorio");
        }
        Ticket ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado"));
        if ("CERRADO".equals(ticket.getEstado())) {
            throw new BusinessRuleException("No se puede derivar un ticket cerrado");
        }
        if (request.getResponsableNuevo() == null) {
            throw new BusinessRuleException("El responsable nuevo es obligatorio");
        }
        if (!authClient.usuarioExiste(request.getResponsableNuevo())) {
            throw new ResourceNotFoundException("Responsable no encontrado");
        }
        if (request.getResponsableNuevo().equals(ticket.getResponsableId())) {
            throw new BusinessRuleException("El responsable nuevo debe ser distinto al anterior");
        }
        if (request.getMotivo() == null) {
            throw new BusinessRuleException("El motivo es obligatorio");
        }
        if (request.getMotivo().trim().isEmpty()) {
            throw new BusinessRuleException("El motivo no puede estar vacío");
        }
        DerivacionTicket derivacion = new DerivacionTicket();
        derivacion.setTicketId(ticket.getId());
        derivacion.setResponsableAnterior(ticket.getResponsableId());
        derivacion.setResponsableNuevo(request.getResponsableNuevo());
        derivacion.setMotivo(request.getMotivo());
        derivacion.setFecha(LocalDateTime.now(ZoneOffset.UTC));
        DerivacionTicket guardada = derivacionRepository.save(derivacion);
        ticket.setResponsableId(request.getResponsableNuevo());
        ticket.setEstado("DERIVADO");
        ticketRepository.save(ticket);
        return guardada;
    }

    @Transactional(readOnly = true)
    public List<DerivacionTicket> listarPorTicket(Long ticketId) {
        return derivacionRepository.findByTicketIdOrderByFechaAsc(ticketId);
    }
}
