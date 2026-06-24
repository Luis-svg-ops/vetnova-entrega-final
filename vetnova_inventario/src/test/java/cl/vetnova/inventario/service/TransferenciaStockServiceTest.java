package cl.vetnova.inventario.service;

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

import cl.vetnova.inventario.dto.CancelacionResponse;
import cl.vetnova.inventario.exception.BusinessRuleException;
import cl.vetnova.inventario.exception.ResourceNotFoundException;
import cl.vetnova.inventario.model.Inventario;
import cl.vetnova.inventario.model.TransferenciaStock;
import cl.vetnova.inventario.repository.InventarioRepository;
import cl.vetnova.inventario.repository.ProductoRepository;
import cl.vetnova.inventario.repository.TransferenciaStockRepository;

public class TransferenciaStockServiceTest {

    @Mock
    private TransferenciaStockRepository transferenciaStockRepository;
    @Mock
    private InventarioRepository inventarioRepository;
    @Mock
    private ProductoRepository productoRepository;
    @InjectMocks
    private TransferenciaStockService transferenciaStockService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private TransferenciaStock transferencia(Long productoId, String origen, String destino, Integer cantidad) {
        TransferenciaStock t = new TransferenciaStock();
        t.setProductoId(productoId);
        t.setSucursalOrigen(origen);
        t.setSucursalDestino(destino);
        t.setCantidad(cantidad);
        return t;
    }

    private Inventario inventario(String sucursal, Integer disponible, Integer transito) {
        Inventario i = new Inventario();
        i.setProductoId(1L);
        i.setSucursal(sucursal);
        i.setStockDisponible(disponible);
        i.setStockTransito(transito);
        return i;
    }

    private void guardaTodo() {
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(transferenciaStockRepository.save(any(TransferenciaStock.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private void productoExiste() {
        when(productoRepository.existsById(1L)).thenReturn(true);
    }

    // ---- CRUD básico ----

    @Test
    void testListar() {
        when(transferenciaStockRepository.findAll()).thenReturn(List.of(new TransferenciaStock()));
        assertEquals(1, transferenciaStockService.listar().size());
    }

    @Test
    void testObtenerPorIdExistente() {
        when(transferenciaStockRepository.findById(1L)).thenReturn(Optional.of(new TransferenciaStock()));
        assertNotNull(transferenciaStockService.obtenerPorId(1L));
    }

    @Test
    void testObtenerPorIdInexistenteLanzaNotFound() {
        when(transferenciaStockRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> transferenciaStockService.obtenerPorId(99L));
    }

    @Test
    void testActualizarExistente() {
        when(transferenciaStockRepository.findById(1L)).thenReturn(Optional.of(new TransferenciaStock()));
        when(transferenciaStockRepository.save(any(TransferenciaStock.class))).thenAnswer(inv -> inv.getArgument(0));
        assertNotNull(transferenciaStockService.actualizar(1L, transferencia(1L, "SANTIAGO", "TALCA", 5)));
    }

    @Test
    void testActualizarInexistenteLanzaNotFound() {
        when(transferenciaStockRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> transferenciaStockService.actualizar(99L, new TransferenciaStock()));
    }

    @Test
    void testEliminarExistente() {
        when(transferenciaStockRepository.existsById(1L)).thenReturn(true);
        transferenciaStockService.eliminar(1L);
        verify(transferenciaStockRepository).deleteById(1L);
    }

    @Test
    void testEliminarInexistenteLanzaNotFound() {
        when(transferenciaStockRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> transferenciaStockService.eliminar(99L));
        verify(transferenciaStockRepository, never()).deleteById(any());
    }

    // ---- crear (CA-TRA-01..09) ----

    @Test
    void testCrearProductoIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> transferenciaStockService.crear(transferencia(null, "SANTIAGO", "VALPARAISO", 10)));
        assertEquals("El productoId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearProductoInexistente() {
        when(productoRepository.existsById(99L)).thenReturn(false);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> transferenciaStockService.crear(transferencia(99L, "SANTIAGO", "VALPARAISO", 10)));
        assertEquals("Producto no encontrado", ex.getMessage());
    }

    @Test
    void testCrearSucursalOrigenNull() {
        productoExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> transferenciaStockService.crear(transferencia(1L, null, "VALPARAISO", 10)));
        assertEquals("La sucursal de origen es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearSucursalDestinoNull() {
        productoExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> transferenciaStockService.crear(transferencia(1L, "SANTIAGO", null, 10)));
        assertEquals("La sucursal de destino es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearOrigenIgualDestino() {
        productoExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> transferenciaStockService.crear(transferencia(1L, "SANTIAGO", "SANTIAGO", 10)));
        assertEquals("La sucursal de origen y destino no pueden ser la misma", ex.getMessage());
    }

    @Test
    void testCrearCantidadNull() {
        productoExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> transferenciaStockService.crear(transferencia(1L, "SANTIAGO", "VALPARAISO", null)));
        assertEquals("La cantidad es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearCantidadNoPositiva() {
        productoExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> transferenciaStockService.crear(transferencia(1L, "SANTIAGO", "VALPARAISO", 0)));
        assertEquals("La cantidad debe ser mayor a 0", ex.getMessage());
    }

    @Test
    void testCrearOrigenSinInventarioLanzaNotFound() {
        productoExiste();
        when(inventarioRepository.findByProductoIdAndSucursal(1L, "SANTIAGO")).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> transferenciaStockService.crear(transferencia(1L, "SANTIAGO", "VALPARAISO", 10)));
        assertEquals("Inventario no encontrado en sucursal SANTIAGO", ex.getMessage());
    }

    @Test
    void testCrearStockInsuficienteEnOrigen() {
        productoExiste();
        when(inventarioRepository.findByProductoIdAndSucursal(1L, "SANTIAGO"))
                .thenReturn(Optional.of(inventario("SANTIAGO", 5, 0)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> transferenciaStockService.crear(transferencia(1L, "SANTIAGO", "VALPARAISO", 10)));
        assertEquals("Stock insuficiente en origen. Disponible: 5", ex.getMessage());
    }

    @Test
    void testCrearStockOrigenNullEsInsuficiente() {
        productoExiste();
        when(inventarioRepository.findByProductoIdAndSucursal(1L, "SANTIAGO"))
                .thenReturn(Optional.of(inventario("SANTIAGO", null, 0)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> transferenciaStockService.crear(transferencia(1L, "SANTIAGO", "VALPARAISO", 1)));
        assertEquals("Stock insuficiente en origen. Disponible: 0", ex.getMessage());
    }

    @Test
    void testCrearCasoFelizConDestinoExistente() {
        productoExiste();
        guardaTodo();
        Inventario origen = inventario("SANTIAGO", 20, 0);
        Inventario destino = inventario("VALPARAISO", 0, 0);
        when(inventarioRepository.findByProductoIdAndSucursal(1L, "SANTIAGO")).thenReturn(Optional.of(origen));
        when(inventarioRepository.findByProductoIdAndSucursal(1L, "VALPARAISO")).thenReturn(Optional.of(destino));

        TransferenciaStock creada = transferenciaStockService.crear(transferencia(1L, "SANTIAGO", "VALPARAISO", 10));

        assertEquals("creada", creada.getEstado());
        assertNotNull(creada.getFechaSolicitud());
        assertEquals(10, origen.getStockDisponible());
        assertEquals(10, destino.getStockTransito());
    }

    @Test
    void testCrearCasoFelizCreaInventarioDestino() {
        productoExiste();
        guardaTodo();
        when(inventarioRepository.findByProductoIdAndSucursal(1L, "SANTIAGO"))
                .thenReturn(Optional.of(inventario("SANTIAGO", 20, 0)));
        when(inventarioRepository.findByProductoIdAndSucursal(1L, "VALPARAISO")).thenReturn(Optional.empty());

        TransferenciaStock creada = transferenciaStockService.crear(transferencia(1L, "SANTIAGO", "VALPARAISO", 10));

        assertEquals("creada", creada.getEstado());
        verify(inventarioRepository, times(2)).save(any(Inventario.class));
    }

    // ---- iniciar (CA-TRA-10..11) ----

    @Test
    void testIniciarDesdeCreada() {
        when(transferenciaStockRepository.findById(1L)).thenReturn(Optional.of(estado("creada")));
        when(transferenciaStockRepository.save(any(TransferenciaStock.class))).thenAnswer(inv -> inv.getArgument(0));
        assertEquals("en tránsito", transferenciaStockService.iniciar(1L).getEstado());
    }

    @Test
    void testIniciarYaEnTransito() {
        when(transferenciaStockRepository.findById(1L)).thenReturn(Optional.of(estado("en tránsito")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> transferenciaStockService.iniciar(1L));
        assertEquals("La transferencia ya está en tránsito", ex.getMessage());
    }

    // ---- confirmarRecepcion (CA-TRA-12..13) ----

    @Test
    void testConfirmarRecepcionCasoFeliz() {
        guardaTodo();
        TransferenciaStock t = estado("en tránsito");
        when(transferenciaStockRepository.findById(1L)).thenReturn(Optional.of(t));
        Inventario destino = inventario("VALPARAISO", 0, 10);
        when(inventarioRepository.findByProductoIdAndSucursal(1L, "VALPARAISO")).thenReturn(Optional.of(destino));

        TransferenciaStock recibida = transferenciaStockService.confirmarRecepcion(1L);

        assertEquals("recibida", recibida.getEstado());
        assertNotNull(recibida.getFechaConfirmacion());
        assertEquals(0, destino.getStockTransito());
        assertEquals(10, destino.getStockDisponible());
    }

    @Test
    void testConfirmarRecepcionYaRecibida() {
        when(transferenciaStockRepository.findById(1L)).thenReturn(Optional.of(estado("recibida")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> transferenciaStockService.confirmarRecepcion(1L));
        assertEquals("La transferencia ya fue recibida", ex.getMessage());
    }

    // ---- cancelar (CA-TRA-14..17) ----

    @Test
    void testCancelarDevuelveStockAOrigen() {
        guardaTodo();
        when(transferenciaStockRepository.findById(1L)).thenReturn(Optional.of(estado("en tránsito")));
        Inventario origen = inventario("SANTIAGO", 10, 0);
        Inventario destino = inventario("VALPARAISO", 0, 10);
        when(inventarioRepository.findByProductoIdAndSucursal(1L, "SANTIAGO")).thenReturn(Optional.of(origen));
        when(inventarioRepository.findByProductoIdAndSucursal(1L, "VALPARAISO")).thenReturn(Optional.of(destino));

        CancelacionResponse resp = transferenciaStockService.cancelar(1L);

        assertEquals("cancelada", resp.getTransferencia().getEstado());
        assertEquals("Transferencia cancelada", resp.getMensaje());
        assertEquals(20, origen.getStockDisponible());
        assertEquals(0, destino.getStockTransito());
    }

    @Test
    void testCancelarYaRecibidaImpedido() {
        when(transferenciaStockRepository.findById(1L)).thenReturn(Optional.of(estado("recibida")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> transferenciaStockService.cancelar(1L));
        assertEquals("No se puede cancelar una transferencia ya recibida", ex.getMessage());
    }

    @Test
    void testCancelarYaCanceladaEsIdempotente() {
        when(transferenciaStockRepository.findById(1L)).thenReturn(Optional.of(estado("cancelada")));
        CancelacionResponse resp = transferenciaStockService.cancelar(1L);
        assertEquals("La transferencia ya estaba cancelada", resp.getMensaje());
        verify(inventarioRepository, never()).save(any(Inventario.class));
    }

    private TransferenciaStock estado(String estado) {
        TransferenciaStock t = transferencia(1L, "SANTIAGO", "VALPARAISO", 10);
        t.setId(1L);
        t.setEstado(estado);
        return t;
    }
}
