package cl.vetnova.soporte.service;

import cl.vetnova.soporte.client.AuthClient;
import cl.vetnova.soporte.dto.CerrarTicketRequest;
import cl.vetnova.soporte.dto.ClasificarTicketRequest;
import cl.vetnova.soporte.dto.CrearTicketRequest;
import cl.vetnova.soporte.dto.DerivarTicketRequest;
import cl.vetnova.soporte.dto.ResponderTicketRequest;
import cl.vetnova.soporte.exception.BusinessRuleException;
import cl.vetnova.soporte.exception.ResourceNotFoundException;
import cl.vetnova.soporte.model.CategoriaTicket;
import cl.vetnova.soporte.model.DerivacionTicket;
import cl.vetnova.soporte.model.EscalamientoTicket;
import cl.vetnova.soporte.model.RespuestaTicket;
import cl.vetnova.soporte.model.Ticket;
import cl.vetnova.soporte.repository.CategoriaTicketRepository;
import cl.vetnova.soporte.repository.DerivacionTicketRepository;
import cl.vetnova.soporte.repository.EscalamientoTicketRepository;
import cl.vetnova.soporte.repository.RespuestaTicketRepository;
import cl.vetnova.soporte.repository.TicketRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketService {

    private static final Set<String> PRIORIDADES = Set.of("BAJA", "MEDIA", "ALTA", "CRITICA");
    private static final String PRIORIDAD_INVALIDA = "Prioridad no válida. Valores permitidos: BAJA, MEDIA, ALTA, CRITICA";

    private final TicketRepository ticketRepository;
    private final CategoriaTicketRepository categoriaRepository;
    private final DerivacionTicketRepository derivacionRepository;
    private final EscalamientoTicketRepository escalamientoRepository;
    private final RespuestaTicketRepository respuestaRepository;
    private final AuthClient authClient;

    public TicketService(TicketRepository ticketRepository, CategoriaTicketRepository categoriaRepository,
                         DerivacionTicketRepository derivacionRepository, EscalamientoTicketRepository escalamientoRepository,
                         RespuestaTicketRepository respuestaRepository, AuthClient authClient) {
        this.ticketRepository = ticketRepository;
        this.categoriaRepository = categoriaRepository;
        this.derivacionRepository = derivacionRepository;
        this.escalamientoRepository = escalamientoRepository;
        this.respuestaRepository = respuestaRepository;
        this.authClient = authClient;
    }

    @Transactional(readOnly = true)
    public List<Ticket> listar(String estado) {
        return estado == null ? ticketRepository.findAll() : ticketRepository.findByEstadoIgnoreCase(estado);
    }

    @Transactional(readOnly = true)
    public Ticket buscar(Long id) {
        return ticketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado"));
    }

    @Transactional
    public Ticket crear(CrearTicketRequest request) {
        if (request.getClienteId() == null) {
            throw new BusinessRuleException("El clienteId es obligatorio");
        }
        if (!authClient.usuarioExiste(request.getClienteId())) {
            throw new ResourceNotFoundException("Cliente no encontrado");
        }
        if (request.getMotivo() == null) {
            throw new BusinessRuleException("El motivo es obligatorio");
        }
        if (request.getMotivo().trim().isEmpty()) {
            throw new BusinessRuleException("El motivo no puede estar vacío");
        }
        if (request.getDescripcion() == null) {
            throw new BusinessRuleException("La descripción es obligatoria");
        }
        if (request.getDescripcion().trim().isEmpty()) {
            throw new BusinessRuleException("La descripción no puede estar vacía");
        }
        Ticket ticket = new Ticket();
        ticket.setClienteId(request.getClienteId());
        ticket.setMotivo(request.getMotivo());
        ticket.setDescripcion(request.getDescripcion());
        ticket.setSucursalId(request.getSucursalId());
        ticket.setEstado("ABIERTO");
        ticket.setFechaCreacion(LocalDateTime.now(ZoneOffset.UTC));
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket clasificar(Long id, ClasificarTicketRequest request) {
        Ticket ticket = buscar(id);
        CategoriaTicket categoria = request.getCategoriaId() == null ? null
                : categoriaRepository.findById(request.getCategoriaId()).orElse(null);
        if (categoria == null) {
            throw new ResourceNotFoundException("Categoría no encontrada");
        }
        if (request.getPrioridad() == null || !PRIORIDADES.contains(request.getPrioridad())) {
            throw new BusinessRuleException(PRIORIDAD_INVALIDA);
        }
        ticket.setCategoria(categoria);
        ticket.setPrioridad(request.getPrioridad());
        ticket.setEstado("CLASIFICADO");
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket derivar(Long id, DerivarTicketRequest request) {
        Ticket ticket = buscar(id);
        if (!authClient.usuarioExiste(request.getResponsableId())) {
            throw new ResourceNotFoundException("Responsable no encontrado");
        }
        if ("CERRADO".equals(ticket.getEstado())) {
            throw new BusinessRuleException("No se puede derivar un ticket cerrado");
        }
        DerivacionTicket derivacion = new DerivacionTicket();
        derivacion.setTicketId(ticket.getId());
        derivacion.setResponsableAnterior(ticket.getResponsableId());
        derivacion.setResponsableNuevo(request.getResponsableId());
        derivacion.setMotivo("Derivación desde ticket");
        derivacion.setFecha(LocalDateTime.now(ZoneOffset.UTC));
        derivacionRepository.save(derivacion);
        ticket.setResponsableId(request.getResponsableId());
        ticket.setEstado("DERIVADO");
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket escalar(Long id) {
        Ticket ticket = buscar(id);
        if ("ESCALADO".equals(ticket.getEstado())) {
            throw new BusinessRuleException("El ticket ya está escalado");
        }
        EscalamientoTicket escalamiento = new EscalamientoTicket();
        escalamiento.setTicketId(ticket.getId());
        escalamiento.setMotivo("Escalamiento desde ticket");
        escalamiento.setEstado("ABIERTO");
        escalamiento.setFechaEscalamiento(LocalDateTime.now(ZoneOffset.UTC));
        escalamientoRepository.save(escalamiento);
        ticket.setEstado("ESCALADO");
        return ticketRepository.save(ticket);
    }

    @Transactional
    public RespuestaTicket responder(Long id, ResponderTicketRequest request) {
        Ticket ticket = buscar(id);
        if (request.getContenido() == null) {
            throw new BusinessRuleException("El contenido de la respuesta es obligatorio");
        }
        if ("CERRADO".equals(ticket.getEstado())) {
            throw new BusinessRuleException("No se puede responder un ticket cerrado");
        }
        RespuestaTicket respuesta = new RespuestaTicket();
        respuesta.setTicketId(ticket.getId());
        respuesta.setAutorId(request.getAutorId());
        respuesta.setContenido(request.getContenido());
        respuesta.setVisible(request.getVisible() == null || request.getVisible());
        respuesta.setFecha(LocalDateTime.now(ZoneOffset.UTC));
        return respuestaRepository.save(respuesta);
    }

    @Transactional
    public Ticket cerrar(Long id, CerrarTicketRequest request) {
        Ticket ticket = buscar(id);
        if (request.getResolucion() == null || request.getResolucion().trim().isEmpty()) {
            throw new BusinessRuleException("La resolución es obligatoria para cerrar el ticket");
        }
        if (respuestaRepository.findByTicketIdOrderByFechaAsc(ticket.getId()).isEmpty()) {
            throw new BusinessRuleException("El ticket debe tener al menos una respuesta antes de cerrarse");
        }
        ticket.setEstado("CERRADO");
        ticket.setResolucion(request.getResolucion());
        ticket.setFechaCierre(LocalDateTime.now(ZoneOffset.UTC));
        return ticketRepository.save(ticket);
    }
}
