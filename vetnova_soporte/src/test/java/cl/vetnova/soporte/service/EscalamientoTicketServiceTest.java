package cl.vetnova.soporte.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

public class EscalamientoTicketServiceTest {

    @Mock private EscalamientoTicketRepository escalamientoRepository;
    @Mock private TicketRepository ticketRepository;
    @Mock private AuthClient authClient;
    @InjectMocks private EscalamientoTicketService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private EscalamientoRequest req(Long ticketId, Long adminId, String motivo) {
        EscalamientoRequest r = new EscalamientoRequest();
        r.setTicketId(ticketId);
        r.setAdministradorId(adminId);
        r.setMotivo(motivo);
        return r;
    }

    private Ticket ticket(Long id, String estado) {
        Ticket t = new Ticket();
        t.setId(id);
        t.setEstado(estado);
        return t;
    }

    private EscalamientoTicket escalamiento(Long id, String estado) {
        EscalamientoTicket e = new EscalamientoTicket();
        e.setId(id);
        e.setEstado(estado);
        return e;
    }

    @Test
    void testCrearTicketIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(null, 1L, "m")));
        assertEquals("El ticketId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearTicketInexistente() {
        when(ticketRepository.findById(999L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.crear(req(999L, 1L, "m")));
        assertEquals("Ticket no encontrado", ex.getMessage());
    }

    @Test
    void testCrearYaEscalamientoAbierto() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "DERIVADO")));
        when(escalamientoRepository.existsByTicketIdAndEstado(1L, "ABIERTO")).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> service.crear(req(1L, 1L, "m")));
        assertEquals("El ticket ya tiene un escalamiento abierto", ex.getMessage());
    }

    @Test
    void testCrearTicketCerrado() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "CERRADO")));
        when(escalamientoRepository.existsByTicketIdAndEstado(1L, "ABIERTO")).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, 1L, "m")));
        assertEquals("No se puede escalar un ticket cerrado", ex.getMessage());
    }

    @Test
    void testCrearAdministradorIdNull() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "DERIVADO")));
        when(escalamientoRepository.existsByTicketIdAndEstado(1L, "ABIERTO")).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, null, "m")));
        assertEquals("El administradorId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearAdministradorSinRol() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "DERIVADO")));
        when(escalamientoRepository.existsByTicketIdAndEstado(1L, "ABIERTO")).thenReturn(false);
        when(authClient.obtenerRol(5L)).thenReturn("RECEPCIONISTA");
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, 5L, "m")));
        assertEquals("El usuario indicado no tiene rol de administrador", ex.getMessage());
    }

    @Test
    void testCrearAdministradorRolNull() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "DERIVADO")));
        when(escalamientoRepository.existsByTicketIdAndEstado(1L, "ABIERTO")).thenReturn(false);
        when(authClient.obtenerRol(5L)).thenReturn(null);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, 5L, "m")));
        assertEquals("El usuario indicado no tiene rol de administrador", ex.getMessage());
    }

    @Test
    void testCrearMotivoNull() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "DERIVADO")));
        when(escalamientoRepository.existsByTicketIdAndEstado(1L, "ABIERTO")).thenReturn(false);
        when(authClient.obtenerRol(1L)).thenReturn("ADMIN_SISTEMA");
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, 1L, null)));
        assertEquals("El motivo es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearMotivoVacio() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "DERIVADO")));
        when(escalamientoRepository.existsByTicketIdAndEstado(1L, "ABIERTO")).thenReturn(false);
        when(authClient.obtenerRol(1L)).thenReturn("ADMIN_SISTEMA");
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, 1L, "  ")));
        assertEquals("El motivo no puede estar vacío", ex.getMessage());
    }

    @Test
    void testCrearCasoFeliz() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "DERIVADO")));
        when(escalamientoRepository.existsByTicketIdAndEstado(1L, "ABIERTO")).thenReturn(false);
        when(authClient.obtenerRol(1L)).thenReturn("ADMIN_SISTEMA");
        when(escalamientoRepository.save(any(EscalamientoTicket.class))).thenAnswer(inv -> inv.getArgument(0));
        EscalamientoTicket e = service.crear(req(1L, 1L, "Sin resolución tras 72h"));
        assertEquals("ABIERTO", e.getEstado());
        verify(ticketRepository).save(any());
    }

    @Test
    void testGestionarInexistente() {
        when(escalamientoRepository.findById(99L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> service.gestionar(99L, gestionar("Revisar")));
        assertEquals("Escalamiento no encontrado", ex.getMessage());
    }

    @Test
    void testGestionarYaResuelto() {
        when(escalamientoRepository.findById(1L)).thenReturn(Optional.of(escalamiento(1L, "RESUELTO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.gestionar(1L, gestionar("Revisar")));
        assertEquals("No se puede gestionar un escalamiento ya resuelto", ex.getMessage());
    }

    @Test
    void testGestionarCasoFeliz() {
        when(escalamientoRepository.findById(1L)).thenReturn(Optional.of(escalamiento(1L, "ABIERTO")));
        when(escalamientoRepository.save(any(EscalamientoTicket.class))).thenAnswer(inv -> inv.getArgument(0));
        EscalamientoTicket e = service.gestionar(1L, gestionar("Contacto telefónico realizado"));
        assertEquals("ABIERTO", e.getEstado());
        assertEquals("Contacto telefónico realizado", e.getUltimaAccion());
        assertNotNull(e.getFechaGestion());
    }

    @Test
    void testCerrarSinResolucion() {
        when(escalamientoRepository.findById(1L)).thenReturn(Optional.of(escalamiento(1L, "ABIERTO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.cerrar(1L, cerrar(null)));
        assertEquals("La resolución es obligatoria para cerrar el escalamiento", ex.getMessage());
    }

    @Test
    void testCerrarResolucionVacia() {
        when(escalamientoRepository.findById(1L)).thenReturn(Optional.of(escalamiento(1L, "ABIERTO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.cerrar(1L, cerrar("  ")));
        assertEquals("La resolución es obligatoria para cerrar el escalamiento", ex.getMessage());
    }

    @Test
    void testCerrarCasoFeliz() {
        when(escalamientoRepository.findById(1L)).thenReturn(Optional.of(escalamiento(1L, "ABIERTO")));
        when(escalamientoRepository.save(any(EscalamientoTicket.class))).thenAnswer(inv -> inv.getArgument(0));
        EscalamientoTicket e = service.cerrar(1L, cerrar("Reembolso aprobado por gerencia"));
        assertEquals("RESUELTO", e.getEstado());
        assertNotNull(e.getFechaResolucion());
    }

    private GestionarEscalamientoRequest gestionar(String accion) {
        GestionarEscalamientoRequest r = new GestionarEscalamientoRequest();
        r.setAccion(accion);
        return r;
    }

    private CerrarEscalamientoRequest cerrar(String resolucion) {
        CerrarEscalamientoRequest r = new CerrarEscalamientoRequest();
        r.setResolucion(resolucion);
        return r;
    }
}
