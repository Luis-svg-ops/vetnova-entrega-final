package cl.vetnova.inventario.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cl.vetnova.inventario.dto.MovimientoStockRequest;
import cl.vetnova.inventario.dto.MovimientoStockResponse;
import cl.vetnova.inventario.exception.BusinessRuleException;
import cl.vetnova.inventario.exception.ResourceNotFoundException;
import cl.vetnova.inventario.model.Inventario;
import cl.vetnova.inventario.model.MovimientoStock;
import cl.vetnova.inventario.model.TipoMovimiento;
import cl.vetnova.inventario.repository.InventarioRepository;
import cl.vetnova.inventario.repository.MovimientoStockRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class MovimientoStockServiceTest {

    @Mock
    private MovimientoStockRepository movimientoStockRepository;
    @Mock
    private InventarioRepository inventarioRepository;
    @Mock
    private OperacionStock operacionStock;
    @InjectMocks
    private MovimientoStockService movimientoStockService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private MovimientoStockRequest req(Long invId, String tipo, Integer cantidad, String motivo, String responsable) {
        MovimientoStockRequest r = new MovimientoStockRequest();
        r.setInventarioId(invId);
        r.setTipo(tipo);
        r.setCantidad(cantidad);
        r.setMotivo(motivo);
        r.setResponsable(responsable);
        return r;
    }

    private Inventario inv(Integer disponible) {
        Inventario i = new Inventario();
        i.setId(1L);
        i.setSucursal("SANTIAGO");
        i.setStockDisponible(disponible);
        return i;
    }

    private void inventarioExiste(Integer disponible) {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inv(disponible)));
    }

    private MovimientoStock movimiento(TipoMovimiento tipo) {
        MovimientoStock m = new MovimientoStock();
        m.setId(7L);
        m.setInventarioId(1L);
        m.setTipo(tipo);
        m.setCantidad(5);
        m.setMotivo("Compra");
        m.setResponsable("Juan");
        m.setSucursal("SANTIAGO");
        m.setFecha(LocalDateTime.now());
        return m;
    }

    @Test
    void testInventarioIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> movimientoStockService.registrar(req(null, "ENTRADA", 5, "Compra", "Juan")));
        assertEquals("El inventarioId es obligatorio", ex.getMessage());
    }

    @Test
    void testInventarioInexistente() {
        when(inventarioRepository.findById(999L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> movimientoStockService.registrar(req(999L, "ENTRADA", 5, "Compra", "Juan")));
        assertEquals("Inventario no encontrado", ex.getMessage());
    }

    @Test
    void testTipoNull() {
        inventarioExiste(10);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> movimientoStockService.registrar(req(1L, null, 5, "Compra", "Juan")));
        assertEquals("El tipo es obligatorio", ex.getMessage());
    }

    @Test
    void testTipoInvalido() {
        inventarioExiste(10);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> movimientoStockService.registrar(req(1L, "AJUSTE", 5, "Compra", "Juan")));
        assertEquals("Tipo no válido. Valores permitidos: ENTRADA, SALIDA", ex.getMessage());
    }

    @Test
    void testCantidadNull() {
        inventarioExiste(10);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> movimientoStockService.registrar(req(1L, "ENTRADA", null, "Compra", "Juan")));
        assertEquals("La cantidad es obligatoria", ex.getMessage());
    }

    @Test
    void testCantidadNoPositiva() {
        inventarioExiste(10);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> movimientoStockService.registrar(req(1L, "ENTRADA", 0, "Compra", "Juan")));
        assertEquals("La cantidad debe ser mayor a 0", ex.getMessage());
    }

    @Test
    void testMotivoNull() {
        inventarioExiste(10);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> movimientoStockService.registrar(req(1L, "ENTRADA", 5, null, "Juan")));
        assertEquals("El motivo es obligatorio", ex.getMessage());
    }

    @Test
    void testMotivoVacio() {
        inventarioExiste(10);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> movimientoStockService.registrar(req(1L, "ENTRADA", 5, "   ", "Juan")));
        assertEquals("El motivo no puede estar vacío", ex.getMessage());
    }

    @Test
    void testResponsableNull() {
        inventarioExiste(10);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> movimientoStockService.registrar(req(1L, "ENTRADA", 5, "Compra", null)));
        assertEquals("El responsable es obligatorio", ex.getMessage());
    }

    @Test
    void testResponsableVacio() {
        inventarioExiste(10);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> movimientoStockService.registrar(req(1L, "ENTRADA", 5, "Compra", "   ")));
        assertEquals("El responsable no puede estar vacío", ex.getMessage());
    }

    @Test
    void testSalidaStockInsuficiente() {
        inventarioExiste(3);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> movimientoStockService.registrar(req(1L, "SALIDA", 5, "Venta", "Ana")));
        assertEquals("Stock insuficiente. Disponible: 3", ex.getMessage());
    }

    @Test
    void testSalidaStockNullInsuficiente() {
        inventarioExiste(null);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> movimientoStockService.registrar(req(1L, "SALIDA", 1, "Venta", "Ana")));
        assertEquals("Stock insuficiente. Disponible: 0", ex.getMessage());
    }

    @Test
    void testEntradaCasoFeliz() {
        Inventario inventario = inv(10);
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));
        when(operacionStock.aplicarMovimiento(any(Inventario.class), any(TipoMovimiento.class),
                any(), any(), any())).thenReturn(movimiento(TipoMovimiento.ENTRADA));
        MovimientoStockResponse resp = movimientoStockService.registrar(req(1L, "ENTRADA", 5, "Compra", "Juan"));
        assertEquals("ENTRADA", resp.getTipo());
        assertEquals(1L, resp.getInventarioId());
        assertEquals("SANTIAGO", resp.getSucursal());
    }

    @Test
    void testSalidaCasoFeliz() {
        Inventario inventario = inv(10);
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));
        when(operacionStock.aplicarMovimiento(any(Inventario.class), any(TipoMovimiento.class),
                any(), any(), any())).thenReturn(movimiento(TipoMovimiento.SALIDA));
        MovimientoStockResponse resp = movimientoStockService.registrar(req(1L, "SALIDA", 3, "Venta", "Ana"));
        assertEquals("SALIDA", resp.getTipo());
    }

    @Test
    void testListar() {
        when(movimientoStockRepository.findAll()).thenReturn(List.of(new MovimientoStock()));
        assertEquals(1, movimientoStockService.listar().size());
    }

    @Test
    void testObtenerPorId() {
        when(movimientoStockRepository.findById(7L)).thenReturn(Optional.of(new MovimientoStock()));
        assertNotNull(movimientoStockService.obtenerPorId(7L));
    }

    @Test
    void testObtenerPorIdInexistente() {
        when(movimientoStockRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> movimientoStockService.obtenerPorId(99L));
    }
}
