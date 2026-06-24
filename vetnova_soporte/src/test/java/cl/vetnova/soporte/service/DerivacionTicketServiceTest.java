package cl.vetnova.soporte.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cl.vetnova.soporte.client.AuthClient;
import cl.vetnova.soporte.dto.DerivacionRequest;
import cl.vetnova.soporte.exception.BusinessRuleException;
import cl.vetnova.soporte.exception.ResourceNotFoundException;
import cl.vetnova.soporte.model.DerivacionTicket;
import cl.vetnova.soporte.model.Ticket;
import cl.vetnova.soporte.repository.DerivacionTicketRepository;
import cl.vetnova.soporte.repository.TicketRepository;

public class DerivacionTicketServiceTest {

    @Mock private DerivacionTicketRepository derivacionRepository;
    @Mock private TicketRepository ticketRepository;
    @Mock private AuthClient authClient;
    @InjectMocks private DerivacionTicketService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private DerivacionRequest req(Long ticketId, Long responsableNuevo, String motivo) {
        DerivacionRequest r = new DerivacionRequest();
        r.setTicketId(ticketId);
        r.setResponsableNuevo(responsableNuevo);
        r.setMotivo(motivo);
        return r;
    }

    private Ticket ticket(Long id, String estado, Long responsableId) {
        Ticket t = new Ticket();
        t.setId(id);
        t.setEstado(estado);
        t.setResponsableId(responsableId);
        return t;
    }

    @Test
    void testRegistrarTicketIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req(null, 3L, "m")));
        assertEquals("El ticketId es obligatorio", ex.getMessage());
    }

    @Test
    void testRegistrarTicketInexistente() {
        when(ticketRepository.findById(999L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.registrar(req(999L, 3L, "m")));
        assertEquals("Ticket no encontrado", ex.getMessage());
    }

    @Test
    void testRegistrarTicketCerrado() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "CERRADO", 2L)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req(1L, 3L, "m")));
        assertEquals("No se puede derivar un ticket cerrado", ex.getMessage());
    }

    @Test
    void testRegistrarResponsableNuevoNull() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "CLASIFICADO", 2L)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req(1L, null, "m")));
        assertEquals("El responsable nuevo es obligatorio", ex.getMessage());
    }

    @Test
    void testRegistrarResponsableInexistente() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "CLASIFICADO", 2L)));
        when(authClient.usuarioExiste(999L)).thenReturn(false);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.registrar(req(1L, 999L, "m")));
        assertEquals("Responsable no encontrado", ex.getMessage());
    }

    @Test
    void testRegistrarResponsableIgualAnterior() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "CLASIFICADO", 3L)));
        when(authClient.usuarioExiste(3L)).thenReturn(true);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req(1L, 3L, "m")));
        assertEquals("El responsable nuevo debe ser distinto al anterior", ex.getMessage());
    }

    @Test
    void testRegistrarMotivoNull() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "CLASIFICADO", 2L)));
        when(authClient.usuarioExiste(3L)).thenReturn(true);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req(1L, 3L, null)));
        assertEquals("El motivo es obligatorio", ex.getMessage());
    }

    @Test
    void testRegistrarMotivoVacio() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "CLASIFICADO", 2L)));
        when(authClient.usuarioExiste(3L)).thenReturn(true);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req(1L, 3L, "  ")));
        assertEquals("El motivo no puede estar vacío", ex.getMessage());
    }

    @Test
    void testRegistrarCasoFeliz() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "CLASIFICADO", 2L)));
        when(authClient.usuarioExiste(3L)).thenReturn(true);
        when(derivacionRepository.save(any(DerivacionTicket.class))).thenAnswer(inv -> inv.getArgument(0));
        DerivacionTicket d = service.registrar(req(1L, 3L, "Especialista requerido"));
        assertEquals(2L, d.getResponsableAnterior());
        assertEquals(3L, d.getResponsableNuevo());
        verify(ticketRepository).save(any());
    }

    @Test
    void testListarPorTicket() {
        when(derivacionRepository.findByTicketIdOrderByFechaAsc(1L)).thenReturn(List.of(new DerivacionTicket()));
        assertEquals(1, service.listarPorTicket(1L).size());
    }
}
