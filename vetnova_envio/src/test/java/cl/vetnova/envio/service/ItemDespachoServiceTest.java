package cl.vetnova.envio.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import cl.vetnova.envio.model.Despacho;
import cl.vetnova.envio.model.ItemDespacho;
import cl.vetnova.envio.repository.DespachoRepository;
import cl.vetnova.envio.repository.ItemDespachoRepository;

public class ItemDespachoServiceTest {

    @Mock
    private ItemDespachoRepository itemDespachoRepository;
    @Mock
    private DespachoRepository despachoRepository;
    @InjectMocks
    private ItemDespachoService itemDespachoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Despacho despachoConEstado(String estado) {
        Despacho d = new Despacho();
        d.setId(1L);
        d.setEstado(estado);
        return d;
    }

    private ItemDespacho item(Long productoId, Integer cantidad) {
        ItemDespacho i = new ItemDespacho();
        i.setProductoId(productoId);
        i.setCantidad(cantidad);
        return i;
    }

    // ---- CRUD genérico ----

    @Test
    void testListar() {
        when(itemDespachoRepository.findAll()).thenReturn(List.of(new ItemDespacho()));
        assertEquals(1, itemDespachoService.listar().size());
    }

    @Test
    void testCrear() {
        when(itemDespachoRepository.save(any(ItemDespacho.class))).thenAnswer(inv -> inv.getArgument(0));
        assertNotNull(itemDespachoService.crear(new ItemDespacho()));
    }

    @Test
    void testObtenerPorIdInexistenteLanzaNotFound() {
        when(itemDespachoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> itemDespachoService.obtenerPorId(99L));
    }

    @Test
    void testActualizarExistente() {
        when(itemDespachoRepository.findById(1L)).thenReturn(Optional.of(new ItemDespacho()));
        when(itemDespachoRepository.save(any(ItemDespacho.class))).thenAnswer(inv -> inv.getArgument(0));
        assertNotNull(itemDespachoService.actualizar(1L, item(10L, 2)));
    }

    @Test
    void testEliminarExistente() {
        when(itemDespachoRepository.existsById(1L)).thenReturn(true);
        itemDespachoService.eliminar(1L);
        verify(itemDespachoRepository).deleteById(1L);
    }

    @Test
    void testEliminarInexistenteLanzaNotFound() {
        when(itemDespachoRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> itemDespachoService.eliminar(99L));
        verify(itemDespachoRepository, never()).deleteById(anyLong());
    }

    // ---- agregarItem (CA-IDE-02..12) ----

    @Test
    void testAgregarItemDespachoInexistente() {
        when(despachoRepository.findById(999L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> itemDespachoService.agregarItem(999L, item(10L, 2)));
        assertEquals("Despacho no encontrado", ex.getMessage());
    }

    @Test
    void testAgregarItemDespachoNoEditable() {
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(despachoConEstado("ENTREGADO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> itemDespachoService.agregarItem(1L, item(10L, 2)));
        assertEquals("No se pueden agregar ítems a un despacho entregado o cancelado", ex.getMessage());
    }

    @Test
    void testAgregarItemProductoNull() {
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(despachoConEstado("CREADO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> itemDespachoService.agregarItem(1L, item(null, 2)));
        assertEquals("El productoId es obligatorio", ex.getMessage());
    }

    @Test
    void testAgregarItemCantidadNull() {
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(despachoConEstado("CREADO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> itemDespachoService.agregarItem(1L, item(10L, null)));
        assertEquals("La cantidad es obligatoria", ex.getMessage());
    }

    @Test
    void testAgregarItemCantidadNoPositiva() {
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(despachoConEstado("CREADO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> itemDespachoService.agregarItem(1L, item(10L, 0)));
        assertEquals("La cantidad debe ser mayor a 0", ex.getMessage());
    }

    @Test
    void testAgregarItemDuplicado() {
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(despachoConEstado("CREADO")));
        when(itemDespachoRepository.existsByDespachoIdAndProductoId(1L, 10L)).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class,
                () -> itemDespachoService.agregarItem(1L, item(10L, 2)));
        assertEquals("El producto ya está en este despacho", ex.getMessage());
    }

    @Test
    void testAgregarItemCasoFelizHeredaEstado() {
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(despachoConEstado("PREPARANDO")));
        when(itemDespachoRepository.existsByDespachoIdAndProductoId(1L, 10L)).thenReturn(false);
        when(itemDespachoRepository.save(any(ItemDespacho.class))).thenAnswer(inv -> inv.getArgument(0));

        ItemDespacho creado = itemDespachoService.agregarItem(1L, item(10L, 2));

        assertEquals("PREPARANDO", creado.getEstado());
        assertEquals(1L, creado.getDespachoId());
    }

    // ---- eliminarItem (CA-IDE-13/14) ----

    @Test
    void testEliminarItemInexistente() {
        when(itemDespachoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> itemDespachoService.eliminarItem(1L, 99L));
    }

    @Test
    void testEliminarItemUnicoImpedido() {
        ItemDespacho existente = item(10L, 2);
        existente.setId(5L);
        when(itemDespachoRepository.findById(5L)).thenReturn(Optional.of(existente));
        when(itemDespachoRepository.countByDespachoId(1L)).thenReturn(1L);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> itemDespachoService.eliminarItem(1L, 5L));
        assertEquals("El despacho debe tener al menos un ítem", ex.getMessage());
    }

    @Test
    void testEliminarItemCasoFeliz() {
        ItemDespacho existente = item(10L, 2);
        existente.setId(5L);
        when(itemDespachoRepository.findById(5L)).thenReturn(Optional.of(existente));
        when(itemDespachoRepository.countByDespachoId(1L)).thenReturn(2L);
        itemDespachoService.eliminarItem(1L, 5L);
        verify(itemDespachoRepository).deleteById(5L);
    }
}
