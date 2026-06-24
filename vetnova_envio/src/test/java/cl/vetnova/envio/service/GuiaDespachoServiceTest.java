package cl.vetnova.envio.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cl.vetnova.envio.exception.BusinessRuleException;
import cl.vetnova.envio.exception.ConflictException;
import cl.vetnova.envio.exception.ResourceNotFoundException;
import cl.vetnova.envio.model.GuiaDespacho;
import cl.vetnova.envio.model.ItemDespacho;
import cl.vetnova.envio.repository.DespachoRepository;
import cl.vetnova.envio.repository.GuiaDespachoRepository;
import cl.vetnova.envio.repository.ItemDespachoRepository;

public class GuiaDespachoServiceTest {

    @Mock
    private GuiaDespachoRepository guiaDespachoRepository;
    @Mock
    private DespachoRepository despachoRepository;
    @Mock
    private ItemDespachoRepository itemDespachoRepository;
    @InjectMocks
    private GuiaDespachoService guiaDespachoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private GuiaDespacho guia(Long despachoId, String origen, String destino, String responsable) {
        GuiaDespacho g = new GuiaDespacho();
        g.setDespachoId(despachoId);
        g.setOrigen(origen);
        g.setDestino(destino);
        g.setResponsable(responsable);
        return g;
    }

    private void despachoConItems() {
        when(despachoRepository.existsById(1L)).thenReturn(true);
        ItemDespacho item = new ItemDespacho();
        item.setProductoId(10L);
        item.setCantidad(2);
        when(itemDespachoRepository.findByDespachoId(1L)).thenReturn(List.of(item));
    }

    // ---- crear ----

    @Test
    void testCrearDespachoIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> guiaDespachoService.crear(guia(null, "A", "B", "5")));
        assertEquals("El despachoId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearDespachoInexistente() {
        when(despachoRepository.existsById(999L)).thenReturn(false);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> guiaDespachoService.crear(guia(999L, "A", "B", "5")));
        assertEquals("Despacho no encontrado", ex.getMessage());
    }

    @Test
    void testCrearDespachoYaTieneGuia() {
        when(despachoRepository.existsById(1L)).thenReturn(true);
        when(guiaDespachoRepository.existsByDespachoId(1L)).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class,
                () -> guiaDespachoService.crear(guia(1L, "A", "B", "5")));
        assertEquals("El despacho ya tiene una guía de despacho generada", ex.getMessage());
    }

    @Test
    void testCrearDespachoSinItems() {
        when(despachoRepository.existsById(1L)).thenReturn(true);
        when(itemDespachoRepository.findByDespachoId(1L)).thenReturn(List.of());
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> guiaDespachoService.crear(guia(1L, "A", "B", "5")));
        assertEquals("No se puede generar guía para un despacho sin ítems", ex.getMessage());
    }

    @Test
    void testCrearOrigenNull() {
        despachoConItems();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> guiaDespachoService.crear(guia(1L, null, "B", "5")));
        assertEquals("El origen es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearDestinoNull() {
        despachoConItems();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> guiaDespachoService.crear(guia(1L, "A", null, "5")));
        assertEquals("El destino es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearOrigenIgualDestino() {
        despachoConItems();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> guiaDespachoService.crear(guia(1L, "A", "A", "5")));
        assertEquals("El destino debe ser distinto al origen", ex.getMessage());
    }

    @Test
    void testCrearResponsableNull() {
        despachoConItems();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> guiaDespachoService.crear(guia(1L, "A", "B", null)));
        assertEquals("El responsable es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearFolioDuplicado() {
        despachoConItems();
        when(guiaDespachoRepository.count()).thenReturn(0L);
        when(guiaDespachoRepository.existsByFolio(anyString())).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class,
                () -> guiaDespachoService.crear(guia(1L, "A", "B", "5")));
        assertEquals("Ya existe una guía con ese folio", ex.getMessage());
    }

    @Test
    void testCrearCasoFeliz() {
        despachoConItems();
        when(guiaDespachoRepository.count()).thenReturn(0L);
        when(guiaDespachoRepository.existsByFolio("GD-0001")).thenReturn(false);
        when(guiaDespachoRepository.save(any(GuiaDespacho.class))).thenAnswer(inv -> inv.getArgument(0));

        GuiaDespacho creada = guiaDespachoService.crear(guia(1L, "Sucursal Central", "Cliente Juan", "5"));

        assertEquals("GD-0001", creada.getFolio());
        assertNotNull(creada.getFechaEmision());
        assertEquals(1, creada.getProductos().size());
    }

    // ---- CRUD ----

    @Test
    void testListar() {
        when(guiaDespachoRepository.findAll()).thenReturn(List.of(new GuiaDespacho()));
        assertEquals(1, guiaDespachoService.listar().size());
    }

    @Test
    void testObtenerPorIdInexistenteLanzaNotFound() {
        when(guiaDespachoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> guiaDespachoService.obtenerPorId(99L));
    }

    @Test
    void testActualizarExistente() {
        when(guiaDespachoRepository.findById(1L)).thenReturn(Optional.of(new GuiaDespacho()));
        when(guiaDespachoRepository.save(any(GuiaDespacho.class))).thenAnswer(inv -> inv.getArgument(0));
        assertNotNull(guiaDespachoService.actualizar(1L, guia(1L, "A", "B", "5")));
    }

    @Test
    void testEliminarExistente() {
        when(guiaDespachoRepository.existsById(1L)).thenReturn(true);
        guiaDespachoService.eliminar(1L);
        verify(guiaDespachoRepository).deleteById(1L);
    }

    @Test
    void testEliminarInexistenteLanzaNotFound() {
        when(guiaDespachoRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> guiaDespachoService.eliminar(99L));
        verify(guiaDespachoRepository, never()).deleteById(any());
    }
}
