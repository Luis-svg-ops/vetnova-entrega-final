package cl.vetnova.soporte.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cl.vetnova.soporte.dto.CategoriaTicketRequest;
import cl.vetnova.soporte.exception.BusinessRuleException;
import cl.vetnova.soporte.exception.ConflictException;
import cl.vetnova.soporte.exception.ResourceNotFoundException;
import cl.vetnova.soporte.model.CategoriaTicket;
import cl.vetnova.soporte.repository.CategoriaTicketRepository;
import cl.vetnova.soporte.repository.TicketRepository;

public class CategoriaTicketServiceTest {

    @Mock
    private CategoriaTicketRepository repository;
    @Mock
    private TicketRepository ticketRepository;
    @InjectMocks
    private CategoriaTicketService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private CategoriaTicketRequest req(String nombre, String prioridad) {
        CategoriaTicketRequest r = new CategoriaTicketRequest();
        r.setNombre(nombre);
        r.setDescripcion("desc");
        r.setAreaPorDefecto("Finanzas");
        r.setPrioridadDefault(prioridad);
        return r;
    }

    private CategoriaTicket categoria(Long id) {
        CategoriaTicket c = new CategoriaTicket();
        c.setId(id);
        c.setNombre("Facturación");
        return c;
    }

    @Test
    void testCrearNombreNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(null, "ALTA")));
        assertEquals("El nombre es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearNombreVacio() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req("  ", "ALTA")));
        assertEquals("El nombre no puede estar vacío", ex.getMessage());
    }

    @Test
    void testCrearNombreDuplicado() {
        when(repository.existsByNombreIgnoreCase("Facturación")).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> service.crear(req("Facturación", "ALTA")));
        assertEquals("Ya existe una categoría con ese nombre", ex.getMessage());
    }

    @Test
    void testCrearPrioridadInvalida() {
        when(repository.existsByNombreIgnoreCase("Facturación")).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req("Facturación", "MAXIMA")));
        assertEquals("Prioridad no válida. Valores permitidos: BAJA, MEDIA, ALTA, CRITICA", ex.getMessage());
    }

    @Test
    void testCrearCasoFelizSinPrioridad() {
        when(repository.existsByNombreIgnoreCase("Facturación")).thenReturn(false);
        when(repository.save(any(CategoriaTicket.class))).thenAnswer(inv -> inv.getArgument(0));
        CategoriaTicket creada = service.crear(req("Facturación", null));
        assertEquals("Facturación", creada.getNombre());
    }

    @Test
    void testCrearCasoFelizConPrioridad() {
        when(repository.existsByNombreIgnoreCase("Facturación")).thenReturn(false);
        when(repository.save(any(CategoriaTicket.class))).thenAnswer(inv -> inv.getArgument(0));
        CategoriaTicket creada = service.crear(req("Facturación", "ALTA"));
        assertEquals("ALTA", creada.getPrioridadDefault());
    }

    @Test
    void testActualizarInexistente() {
        when(repository.findById(99L)).thenReturn(java.util.Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.actualizar(99L, req("Nueva", "ALTA")));
        assertEquals("Categoría no encontrada", ex.getMessage());
    }

    @Test
    void testActualizarNombreDuplicado() {
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(categoria(1L)));
        when(repository.existsByNombreIgnoreCaseAndIdNot("Facturación", 1L)).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> service.actualizar(1L, req("Facturación", "ALTA")));
        assertEquals("Ya existe una categoría con ese nombre", ex.getMessage());
    }

    @Test
    void testActualizarNombreVacio() {
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(categoria(1L)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.actualizar(1L, req("  ", "ALTA")));
        assertEquals("El nombre no puede estar vacío", ex.getMessage());
    }

    @Test
    void testActualizarPrioridadInvalida() {
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(categoria(1L)));
        when(repository.existsByNombreIgnoreCaseAndIdNot("Otra", 1L)).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.actualizar(1L, req("Otra", "MAXIMA")));
        assertEquals("Prioridad no válida. Valores permitidos: BAJA, MEDIA, ALTA, CRITICA", ex.getMessage());
    }

    @Test
    void testActualizarCasoFelizCompleto() {
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(categoria(1L)));
        when(repository.existsByNombreIgnoreCaseAndIdNot("Otra", 1L)).thenReturn(false);
        when(repository.save(any(CategoriaTicket.class))).thenAnswer(inv -> inv.getArgument(0));
        CategoriaTicket actualizada = service.actualizar(1L, req("Otra", "MEDIA"));
        assertEquals("Otra", actualizada.getNombre());
        assertEquals("MEDIA", actualizada.getPrioridadDefault());
        assertEquals("Finanzas", actualizada.getAreaPorDefecto());
    }

    @Test
    void testActualizarCasoFelizSinCambios() {
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(categoria(1L)));
        when(repository.save(any(CategoriaTicket.class))).thenAnswer(inv -> inv.getArgument(0));
        CategoriaTicketRequest vacio = new CategoriaTicketRequest();
        CategoriaTicket actualizada = service.actualizar(1L, vacio);
        assertEquals("Facturación", actualizada.getNombre());
    }

    @Test
    void testEliminarConTickets() {
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(categoria(1L)));
        when(ticketRepository.existsByCategoria_Id(1L)).thenReturn(true);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.eliminar(1L));
        assertEquals("No se puede eliminar una categoría con tickets asociados", ex.getMessage());
    }

    @Test
    void testEliminarSinTickets() {
        CategoriaTicket c = categoria(1L);
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(c));
        when(ticketRepository.existsByCategoria_Id(1L)).thenReturn(false);
        service.eliminar(1L);
        verify(repository).delete(c);
    }

    @Test
    void testListar() {
        when(repository.findAllByOrderByNombreAsc()).thenReturn(List.of(categoria(1L)));
        assertEquals(1, service.listar().size());
    }
}
