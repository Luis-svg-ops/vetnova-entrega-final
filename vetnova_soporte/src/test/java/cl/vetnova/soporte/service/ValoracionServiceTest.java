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

import cl.vetnova.soporte.dto.PromedioValoracionResponse;
import cl.vetnova.soporte.exception.BusinessRuleException;
import cl.vetnova.soporte.exception.ConflictException;
import cl.vetnova.soporte.exception.ForbiddenException;
import cl.vetnova.soporte.exception.ResourceNotFoundException;
import cl.vetnova.soporte.model.Ticket;
import cl.vetnova.soporte.model.Valoracion;
import cl.vetnova.soporte.repository.TicketRepository;
import cl.vetnova.soporte.repository.ValoracionRepository;

public class ValoracionServiceTest {

    @Mock private ValoracionRepository valoracionRepository;
    @Mock private TicketRepository ticketRepository;
    @InjectMocks private ValoracionService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Valoracion req(Long ticketId, Long clienteId, Integer puntuacion) {
        Valoracion v = new Valoracion();
        v.setTicketId(ticketId);
        v.setClienteId(clienteId);
        v.setPuntuacion(puntuacion);
        return v;
    }

    private Ticket ticket(Long id, String estado, Long clienteId) {
        Ticket t = new Ticket();
        t.setId(id);
        t.setEstado(estado);
        t.setClienteId(clienteId);
        t.setSucursalId(7L);
        return t;
    }

    @Test
    void testCrearTicketIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(null, 1L, 5)));
        assertEquals("El ticketId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearTicketInexistente() {
        when(ticketRepository.findById(999L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.crear(req(999L, 1L, 5)));
        assertEquals("Ticket no encontrado", ex.getMessage());
    }

    @Test
    void testCrearTicketNoCerrado() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "DERIVADO", 1L)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, 1L, 5)));
        assertEquals("Solo se puede valorar un ticket cerrado", ex.getMessage());
    }

    @Test
    void testCrearTicketYaValorado() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "CERRADO", 1L)));
        when(valoracionRepository.existsByTicketId(1L)).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> service.crear(req(1L, 1L, 5)));
        assertEquals("Este ticket ya fue valorado", ex.getMessage());
    }

    @Test
    void testCrearClienteIdNull() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "CERRADO", 1L)));
        when(valoracionRepository.existsByTicketId(1L)).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, null, 5)));
        assertEquals("El clienteId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearClienteNoDueno() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "CERRADO", 1L)));
        when(valoracionRepository.existsByTicketId(1L)).thenReturn(false);
        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> service.crear(req(1L, 2L, 5)));
        assertEquals("Solo el cliente dueño del ticket puede valorarlo", ex.getMessage());
    }

    @Test
    void testCrearPuntuacionNull() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "CERRADO", 1L)));
        when(valoracionRepository.existsByTicketId(1L)).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, 1L, null)));
        assertEquals("La puntuación es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearPuntuacionMayorA5() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "CERRADO", 1L)));
        when(valoracionRepository.existsByTicketId(1L)).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, 1L, 6)));
        assertEquals("La puntuación debe estar entre 1 y 5", ex.getMessage());
    }

    @Test
    void testCrearPuntuacionCero() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "CERRADO", 1L)));
        when(valoracionRepository.existsByTicketId(1L)).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, 1L, 0)));
        assertEquals("La puntuación debe estar entre 1 y 5", ex.getMessage());
    }

    @Test
    void testCrearCasoFeliz() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, "CERRADO", 1L)));
        when(valoracionRepository.existsByTicketId(1L)).thenReturn(false);
        when(valoracionRepository.save(any(Valoracion.class))).thenAnswer(inv -> inv.getArgument(0));
        Valoracion v = req(1L, 1L, 5);
        v.setComentario("Excelente atención");
        Valoracion creada = service.crear(v);
        assertEquals(5, creada.getPuntuacion());
        assertEquals(7L, creada.getSucursalId());
        assertNotNull(creada.getFecha());
    }

    @Test
    void testObtenerPorTicketEncontrada() {
        Valoracion v = new Valoracion();
        when(valoracionRepository.findByTicketId(1L)).thenReturn(v);
        assertEquals(v, service.obtenerPorTicket(1L));
    }

    @Test
    void testObtenerPorTicketNoEncontrada() {
        when(valoracionRepository.findByTicketId(1L)).thenReturn(null);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.obtenerPorTicket(1L));
        assertEquals("El ticket no tiene valoración", ex.getMessage());
    }

    @Test
    void testPromedioSinValoraciones() {
        when(valoracionRepository.findBySucursalId(1L)).thenReturn(List.of());
        PromedioValoracionResponse r = service.promedioPorSucursal(1L);
        assertEquals(0.0, r.getPromedio());
        assertEquals(0, r.getTotal());
    }

    @Test
    void testPromedioConValoraciones() {
        Valoracion v1 = req(1L, 1L, 4);
        Valoracion v2 = req(2L, 1L, 5);
        when(valoracionRepository.findBySucursalId(1L)).thenReturn(List.of(v1, v2));
        PromedioValoracionResponse r = service.promedioPorSucursal(1L);
        assertEquals(4.5, r.getPromedio());
        assertEquals(2, r.getTotal());
    }
}
