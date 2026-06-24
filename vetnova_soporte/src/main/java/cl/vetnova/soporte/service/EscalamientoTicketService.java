package cl.vetnova.soporte.service;

import cl.vetnova.soporte.client.AuthClient;
import cl.vetnova.soporte.dto.CerrarEscalamientoRequest;
import cl.vetnova.soporte.dto.EscalamientoRequest;
import cl.vetnova.soporte.dto.GestionarEscalamientoRequest;
import cl.vetnova.soporte.exception.BusinessRuleException;
import cl.vetnova.soporte.exception.ConflictException;
import cl.vetnova.soporte.exception.ResourceNotFoundException;
import cl.vetnova.soporte.model.EscalamientoTicket;
import cl.vetnova.soporte.model.Ticket;
import cl.vetnova.soporte.repository.EscalamientoTicketRepository;
import cl.vetnova.soporte.repository.TicketRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EscalamientoTicketService {

    private final EscalamientoTicketRepository escalamientoRepository;
    private final TicketRepository ticketRepository;
    private final AuthClient authClient;

    public EscalamientoTicketService(EscalamientoTicketRepository escalamientoRepository, TicketRepository ticketRepository,
                                     AuthClient authClient) {
        this.escalamientoRepository = escalamientoRepository;
        this.ticketRepository = ticketRepository;
        this.authClient = authClient;
    }

    @Transactional
    public EscalamientoTicket crear(EscalamientoRequest request) {
        if (request.getTicketId() == null) {
            throw new BusinessRuleException("El ticketId es obligatorio");
        }
        Ticket ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado"));
        if (escalamientoRepository.existsByTicketIdAndEstado(ticket.getId(), "ABIERTO")) {
            throw new ConflictException("El ticket ya tiene un escalamiento abierto");
        }
        if ("CERRADO".equals(ticket.getEstado())) {
            throw new BusinessRuleException("No se puede escalar un ticket cerrado");
        }
        if (request.getAdministradorId() == null) {
            throw new BusinessRuleException("El administradorId es obligatorio");
        }
        String rol = authClient.obtenerRol(request.getAdministradorId());
        if (rol == null || !rol.startsWith("ADMIN")) {
            throw new BusinessRuleException("El usuario indicado no tiene rol de administrador");
        }
        if (request.getMotivo() == null) {
            throw new BusinessRuleException("El motivo es obligatorio");
        }
        if (request.getMotivo().trim().isEmpty()) {
            throw new BusinessRuleException("El motivo no puede estar vacío");
        }
        EscalamientoTicket escalamiento = new EscalamientoTicket();
        escalamiento.setTicketId(ticket.getId());
        escalamiento.setAdministradorId(request.getAdministradorId());
        escalamiento.setMotivo(request.getMotivo());
        escalamiento.setEstado("ABIERTO");
        escalamiento.setFechaEscalamiento(LocalDateTime.now(ZoneOffset.UTC));
        EscalamientoTicket guardado = escalamientoRepository.save(escalamiento);
        ticket.setEstado("ESCALADO");
        ticketRepository.save(ticket);
        return guardado;
    }

    @Transactional
    public EscalamientoTicket gestionar(Long id, GestionarEscalamientoRequest request) {
        EscalamientoTicket escalamiento = buscar(id);
        if ("RESUELTO".equals(escalamiento.getEstado())) {
            throw new BusinessRuleException("No se puede gestionar un escalamiento ya resuelto");
        }
        escalamiento.setUltimaAccion(request.getAccion());
        escalamiento.setFechaGestion(LocalDateTime.now(ZoneOffset.UTC));
        return escalamientoRepository.save(escalamiento);
    }

    @Transactional
    public EscalamientoTicket cerrar(Long id, CerrarEscalamientoRequest request) {
        EscalamientoTicket escalamiento = buscar(id);
        if (request.getResolucion() == null || request.getResolucion().trim().isEmpty()) {
            throw new BusinessRuleException("La resolución es obligatoria para cerrar el escalamiento");
        }
        escalamiento.setEstado("RESUELTO");
        escalamiento.setResolucion(request.getResolucion());
        escalamiento.setFechaResolucion(LocalDateTime.now(ZoneOffset.UTC));
        return escalamientoRepository.save(escalamiento);
    }

    @Transactional(readOnly = true)
    public EscalamientoTicket buscar(Long id) {
        return escalamientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Escalamiento no encontrado"));
    }
}
