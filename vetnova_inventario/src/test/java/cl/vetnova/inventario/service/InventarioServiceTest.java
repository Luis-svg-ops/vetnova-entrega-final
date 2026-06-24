package cl.vetnova.inventario.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import cl.vetnova.inventario.exception.BusinessRuleException;
import cl.vetnova.inventario.exception.ConflictException;
import cl.vetnova.inventario.exception.ResourceNotFoundException;
import cl.vetnova.inventario.model.AlertaStock;
import cl.vetnova.inventario.model.Inventario;
import cl.vetnova.inventario.model.MovimientoStock;
import cl.vetnova.inventario.repository.AlertaStockRepository;
import cl.vetnova.inventario.repository.InventarioRepository;
import cl.vetnova.inventario.repository.MovimientoStockRepository;
import cl.vetnova.inventario.repository.ProductoRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;
    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private MovimientoStockRepository movimientoStockRepository;
    @Mock
    private AlertaStockRepository alertaStockRepository;
    @InjectMocks
    private InventarioService inventarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Inventario inventario(Long productoId, String sucursal, Integer stockMinimo) {
        Inventario i = new Inventario();
        i.setProductoId(productoId);
        i.setSucursal(sucursal);
        i.setStockMinimo(stockMinimo);
        return i;
    }

    private Inventario stock(Long id, Integer disponible, Integer minimo, Integer transito) {
        Inventario i = new Inventario();
        i.setId(id);
        i.setSucursal("SANTIAGO");
        i.setStockDisponible(disponible);
        i.setStockMinimo(minimo);
        i.setStockTransito(transito);
        return i;
    }

    private void productoExiste() {
        when(productoRepository.existsById(1L)).thenReturn(true);
    }

    private void guardaTodo() {
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(movimientoStockRepository.save(any(MovimientoStock.class))).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(alertaStockRepository.save(any(AlertaStock.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    // ---- crear / CRUD ----

    @Test
    void testCrearProductoIdNullLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> inventarioService.crear(inventario(null, "SANTIAGO", 5)));
        assertEquals("El productoId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearProductoInexistenteLanzaNotFound() {
        when(productoRepository.existsById(99L)).thenReturn(false);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> inventarioService.crear(inventario(99L, "SANTIAGO", 5)));
        assertEquals("Producto no encontrado", ex.getMessage());
    }

    @Test
    void testCrearSucursalNullLanzaBusinessRule() {
        productoExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> inventarioService.crear(inventario(1L, null, 5)));
        assertEquals("La sucursal es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearSucursalInexistenteLanzaNotFound() {
        productoExiste();
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> inventarioService.crear(inventario(1L, "FANTASMA", 5)));
        assertEquals("Sucursal no encontrada", ex.getMessage());
    }

    @Test
    void testCrearCombinacionDuplicadaLanzaConflict() {
        productoExiste();
        when(inventarioRepository.existsByProductoIdAndSucursal(1L, "SANTIAGO")).thenReturn(true);
        assertThrows(ConflictException.class, () -> inventarioService.crear(inventario(1L, "SANTIAGO", 5)));
    }

    @Test
    void testCrearStockMinimoNegativoLanzaBusinessRule() {
        productoExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> inventarioService.crear(inventario(1L, "SANTIAGO", -1)));
        assertEquals("El stock mínimo no puede ser negativo", ex.getMessage());
    }

    @Test
    void testCrearCasoFelizDejaStockEnCero() {
        productoExiste();
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(inv -> inv.getArgument(0));
        Inventario creado = inventarioService.crear(inventario(1L, "SANTIAGO", 5));
        assertEquals(0, creado.getStockDisponible());
        assertEquals(0, creado.getStockTransito());
        assertEquals(5, creado.getStockMinimo());
    }

    @Test
    void testCrearStockMinimoCeroEsValido() {
        productoExiste();
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(inv -> inv.getArgument(0));
        assertEquals(0, inventarioService.crear(inventario(1L, "SANTIAGO", 0)).getStockMinimo());
    }

    @Test
    void testCrearStockMinimoNullEsValido() {
        productoExiste();
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(inv -> inv.getArgument(0));
        Inventario creado = inventarioService.crear(inventario(1L, "SANTIAGO", null));
        assertEquals(0, creado.getStockDisponible());
        assertNull(creado.getStockMinimo());
    }

    @Test
    void testListarYObtenerPorId() {
        when(inventarioRepository.findAll()).thenReturn(List.of(new Inventario()));
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(new Inventario()));
        assertEquals(1, inventarioService.listar().size());
        assertNotNull(inventarioService.obtenerPorId(1L));
    }

    @Test
    void testObtenerPorIdInexistenteLanzaNotFound() {
        when(inventarioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> inventarioService.obtenerPorId(99L));
    }

    @Test
    void testActualizarCambiaStockMinimo() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(new Inventario()));
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(inv -> inv.getArgument(0));
        Inventario datos = new Inventario();
        datos.setStockMinimo(10);
        assertEquals(10, inventarioService.actualizar(1L, datos).getStockMinimo());
    }

    @Test
    void testEliminarExistente() {
        when(inventarioRepository.existsById(1L)).thenReturn(true);
        inventarioService.eliminar(1L);
        verify(inventarioRepository).deleteById(1L);
    }

    @Test
    void testEliminarInexistenteLanzaNotFound() {
        when(inventarioRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> inventarioService.eliminar(99L));
    }

    // ---- registrarEntrada (CA-INV-10..14) ----

    @Test
    void testRegistrarEntradaCantidadNull() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(stock(1L, 10, 5, 0)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> inventarioService.registrarEntrada(1L, null, "Juan"));
        assertEquals("La cantidad es obligatoria", ex.getMessage());
    }

    @Test
    void testRegistrarEntradaCantidadCeroONegativa() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(stock(1L, 10, 5, 0)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> inventarioService.registrarEntrada(1L, 0, "Juan"));
        assertEquals("La cantidad debe ser mayor a 0", ex.getMessage());
    }

    @Test
    void testRegistrarEntradaResponsableNull() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(stock(1L, 10, 5, 0)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> inventarioService.registrarEntrada(1L, 5, null));
        assertEquals("El responsable es obligatorio", ex.getMessage());
    }

    @Test
    void testRegistrarEntradaCasoFelizSumaStock() {
        guardaTodo();
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(stock(1L, 10, 5, 0)));
        Inventario actualizado = inventarioService.registrarEntrada(1L, 5, "Juan");
        assertEquals(15, actualizado.getStockDisponible());
        verify(movimientoStockRepository).save(any(MovimientoStock.class));
    }

    @Test
    void testRegistrarEntradaConStockDisponibleNull() {
        guardaTodo();
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(stock(1L, null, 5, 0)));
        Inventario actualizado = inventarioService.registrarEntrada(1L, 5, "Juan");
        assertEquals(5, actualizado.getStockDisponible());
    }

    // ---- registrarSalida (CA-INV-15..18) ----

    @Test
    void testRegistrarSalidaCantidadNull() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(stock(1L, 10, 5, 0)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> inventarioService.registrarSalida(1L, null, "Venta"));
        assertEquals("La cantidad es obligatoria", ex.getMessage());
    }

    @Test
    void testRegistrarSalidaCantidadNoPositiva() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(stock(1L, 10, 5, 0)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> inventarioService.registrarSalida(1L, -1, "Venta"));
        assertEquals("La cantidad debe ser mayor a 0", ex.getMessage());
    }

    @Test
    void testRegistrarSalidaMotivoNull() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(stock(1L, 10, 5, 0)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> inventarioService.registrarSalida(1L, 3, null));
        assertEquals("El motivo es obligatorio", ex.getMessage());
    }

    @Test
    void testRegistrarSalidaStockInsuficiente() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(stock(1L, 3, 5, 0)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> inventarioService.registrarSalida(1L, 5, "Venta"));
        assertEquals("Stock insuficiente. Disponible: 3", ex.getMessage());
    }

    @Test
    void testRegistrarSalidaStockNullInsuficiente() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(stock(1L, null, 5, 0)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> inventarioService.registrarSalida(1L, 1, "Venta"));
        assertEquals("Stock insuficiente. Disponible: 0", ex.getMessage());
    }

    @Test
    void testRegistrarSalidaCasoFeliz() {
        guardaTodo();
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(stock(1L, 10, 0, 0)));
        Inventario actualizado = inventarioService.registrarSalida(1L, 3, "Venta");
        assertEquals(7, actualizado.getStockDisponible());
    }

    @Test
    void testRegistrarSalidaDejaStockEnCeroGeneraAlerta() {
        guardaTodo();
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(stock(1L, 5, 2, 0)));
        when(alertaStockRepository.existsByInventarioIdAndTipoAndLeidaFalse(1L, "SIN_STOCK")).thenReturn(false);
        Inventario actualizado = inventarioService.registrarSalida(1L, 5, "Venta");
        assertEquals(0, actualizado.getStockDisponible());
        verify(alertaStockRepository).save(any(AlertaStock.class));
    }

    // ---- ajustarStockMinimo (CA-INV-19..20) ----

    @Test
    void testAjustarStockMinimoNull() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(stock(1L, 10, 5, 0)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> inventarioService.ajustarStockMinimo(1L, null));
        assertEquals("El stock mínimo es obligatorio", ex.getMessage());
    }

    @Test
    void testAjustarStockMinimoNegativo() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(stock(1L, 10, 5, 0)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> inventarioService.ajustarStockMinimo(1L, -1));
        assertEquals("El stock mínimo no puede ser negativo", ex.getMessage());
    }

    @Test
    void testAjustarStockMinimoCasoFeliz() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(stock(1L, 10, 5, 0)));
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(inv -> inv.getArgument(0));
        assertEquals(10, inventarioService.ajustarStockMinimo(1L, 10).getStockMinimo());
    }

    // ---- getStockTotal (CA-INV-23) ----

    @Test
    void testGetStockTotalSuma() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(stock(1L, 10, 5, 3)));
        assertEquals(13, inventarioService.getStockTotal(1L));
    }

    @Test
    void testGetStockTotalConNulosEsCero() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(stock(1L, null, 5, null)));
        assertEquals(0, inventarioService.getStockTotal(1L));
    }

    // ---- verificarStockCritico (CA-INV-21..22, CA-ALE-05..07) ----

    @Test
    void testVerificarStockCriticoBajoMinimoGeneraAlerta() {
        when(alertaStockRepository.existsByInventarioIdAndTipoAndLeidaFalse(1L, "STOCK_MINIMO")).thenReturn(false);
        when(alertaStockRepository.save(any(AlertaStock.class))).thenAnswer(inv -> inv.getArgument(0));
        assertTrue(inventarioService.verificarStockCritico(stock(1L, 2, 5, 0)));
        verify(alertaStockRepository).save(any(AlertaStock.class));
    }

    @Test
    void testVerificarStockCriticoIgualAlMinimoNoDispara() {
        assertFalse(inventarioService.verificarStockCritico(stock(1L, 5, 5, 0)));
        verify(alertaStockRepository, never()).save(any(AlertaStock.class));
    }

    @Test
    void testVerificarStockCriticoSinStockGeneraSinStock() {
        when(alertaStockRepository.existsByInventarioIdAndTipoAndLeidaFalse(1L, "SIN_STOCK")).thenReturn(false);
        when(alertaStockRepository.save(any(AlertaStock.class))).thenAnswer(inv -> inv.getArgument(0));
        assertTrue(inventarioService.verificarStockCritico(stock(1L, 0, 5, 0)));
    }

    @Test
    void testVerificarStockCriticoConNulosTratadosComoCero() {
        when(alertaStockRepository.existsByInventarioIdAndTipoAndLeidaFalse(anyLong(), anyString())).thenReturn(false);
        when(alertaStockRepository.save(any(AlertaStock.class))).thenAnswer(inv -> inv.getArgument(0));
        assertTrue(inventarioService.verificarStockCritico(stock(1L, null, null, 0)));
    }

    @Test
    void testVerificarStockCriticoNoDuplicaAlertaVigente() {
        when(alertaStockRepository.existsByInventarioIdAndTipoAndLeidaFalse(1L, "SIN_STOCK")).thenReturn(true);
        assertTrue(inventarioService.verificarStockCritico(stock(1L, 0, 5, 0)));
        verify(alertaStockRepository, never()).save(any(AlertaStock.class));
    }
}
