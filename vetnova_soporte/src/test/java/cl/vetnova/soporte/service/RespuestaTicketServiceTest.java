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
import cl.vetnova.soporte.dto.RespuestaRequest;
import cl.vetnova.soporte.exception.BusinessRuleException;
import cl.vetnova.soporte.exception.ResourceNotFoundException;
import cl.vetnova.soporte.model.RespuestaTicket;
import cl.vetnova.soporte.model.Ticket;
import cl.vetnova.soporte.repository.RespuestaTicketRepository;
import cl.vetnova.soporte.repository.TicketRepository;

public class RespuestaTicketServiceTest {

    @Mock private RespuestaTicketRepository respuestaRepository;
    @Mock private TicketRepository ticketRepository;
    @Mock private AuthClient authClient;
    @InjectMocks private RespuestaTicketService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private RespuestaRequest req(Long ticketId, Long autorId, String contenido, Boolean visible) {
        RespuestaRequest r = new RespuestaRequest();
        r.setTicketId(ticketId);
        r.setAutorId(autorId);
        r.setContenido(contenido);
        r.setVisible(visible);
        return r;
    }

    private Ticket ticket(Long id, String estado) {
        Ticket t = new Ticket();
        t.setId(id);
        t.setEstado(estado);
        return t;
    }

    @Test
    void testRegistrarTicketIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req(null, 3L, "c", null)));
        assertEquals("El ticketId es obligatorio", ex.getMessage());
    }

    @Test
    void testRegistrarTicketInexistente() {
        when(ticketRepository.findById(999L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.registrar(req(999L, 3L, "c", null)));
        assertEquals("Ticket no encontrado", ex.getMessage());
    }

    @Test
    void testRegistrarTicketCerrado() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "CERRADO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req(1L, 3L, "c", null)));
        assertEquals("No se puede responder un ticket cerrado", ex.getMessage());
    }

    @Test
    void testRegistrarAutorIdNull() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "DERIVADO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req(1L, null, "c", null)));
        assertEquals("El autorId es obligatorio", ex.getMessage());
    }

    @Test
    void testRegistrarAutorInexistente() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "DERIVADO")));
        when(authClient.usuarioExiste(999L)).thenReturn(false);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.registrar(req(1L, 999L, "c", null)));
        assertEquals("Autor no encontrado", ex.getMessage());
    }

    @Test
    void testRegistrarContenidoNull() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "DERIVADO")));
        when(authClient.usuarioExiste(3L)).thenReturn(true);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req(1L, 3L, null, null)));
        assertEquals("El contenido es obligatorio", ex.getMessage());
    }

    @Test
    void testRegistrarContenidoVacio() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "DERIVADO")));
        when(authClient.usuarioExiste(3L)).thenReturn(true);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req(1L, 3L, "  ", null)));
        assertEquals("El contenido no puede estar vacío", ex.getMessage());
    }

    @Test
    void testRegistrarCasoFelizVisiblePorDefecto() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "DERIVADO")));
        when(authClient.usuarioExiste(3L)).thenReturn(true);
        when(respuestaRepository.save(any(RespuestaTicket.class))).thenAnswer(inv -> inv.getArgument(0));
        RespuestaTicket r = service.registrar(req(1L, 3L, "Hemos procesado", null));
        assertTrue(r.getVisible());
    }

    @Test
    void testRegistrarCasoFelizVisibleFalse() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "DERIVADO")));
        when(authClient.usuarioExiste(3L)).thenReturn(true);
        when(respuestaRepository.save(any(RespuestaTicket.class))).thenAnswer(inv -> inv.getArgument(0));
        RespuestaTicket r = service.registrar(req(1L, 3L, "Nota interna", false));
        assertFalse(r.getVisible());
    }

    @Test
    void testRegistrarCasoFelizVisibleTrue() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "DERIVADO")));
        when(authClient.usuarioExiste(3L)).thenReturn(true);
        when(respuestaRepository.save(any(RespuestaTicket.class))).thenAnswer(inv -> inv.getArgument(0));
        RespuestaTicket r = service.registrar(req(1L, 3L, "Respuesta visible", true));
        assertTrue(r.getVisible());
    }

    @Test
    void testListarPorTicket() {
        when(respuestaRepository.findByTicketIdOrderByFechaAsc(1L)).thenReturn(List.of(new RespuestaTicket()));
        assertEquals(1, service.listarPorTicket(1L).size());
    }
}
