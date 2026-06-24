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

import cl.vetnova.inventario.dto.DetallePedidoRequest;
import cl.vetnova.inventario.exception.BusinessRuleException;
import cl.vetnova.inventario.exception.ConflictException;
import cl.vetnova.inventario.exception.ResourceNotFoundException;
import cl.vetnova.inventario.model.DetallePedidoProveedor;
import cl.vetnova.inventario.model.PedidoProveedor;
import cl.vetnova.inventario.repository.DetallePedidoProveedorRepository;
import cl.vetnova.inventario.repository.PedidoProveedorRepository;
import cl.vetnova.inventario.repository.ProductoRepository;
import cl.vetnova.inventario.repository.ProveedorProductoRepository;

public class DetallePedidoProveedorServiceTest {

    @Mock
    private DetallePedidoProveedorRepository detallePedidoProveedorRepository;
    @Mock
    private PedidoProveedorRepository pedidoProveedorRepository;
    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private ProveedorProductoRepository proveedorProductoRepository;
    @InjectMocks
    private DetallePedidoProveedorService detallePedidoProveedorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private DetallePedidoRequest request(Long pedidoId, Long productoId, Integer cantidad, Double precio) {
        DetallePedidoRequest r = new DetallePedidoRequest();
        r.setPedidoId(pedidoId);
        r.setProductoId(productoId);
        r.setCantidad(cantidad);
        r.setPrecioUnitario(precio);
        return r;
    }

    private PedidoProveedor pedido(String estado) {
        PedidoProveedor p = new PedidoProveedor();
        p.setId(1L);
        p.setProveedorId(1L);
        p.setEstado(estado);
        return p;
    }

    private void pedidoEditable() {
        when(pedidoProveedorRepository.findById(1L)).thenReturn(Optional.of(pedido("pendiente")));
    }

    // ---- CRUD ----

    @Test
    void testListar() {
        when(detallePedidoProveedorRepository.findAll()).thenReturn(List.of(new DetallePedidoProveedor()));
        assertEquals(1, detallePedidoProveedorService.listar().size());
    }

    @Test
    void testObtenerPorIdInexistenteLanzaNotFound() {
        when(detallePedidoProveedorRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> detallePedidoProveedorService.obtenerPorId(99L));
    }

    private DetallePedidoProveedor detalleConPedido(Long pedidoId) {
        DetallePedidoProveedor d = new DetallePedidoProveedor();
        d.setId(1L);
        d.setPedidoId(pedidoId);
        d.setCantidad(10);
        d.setPrecioUnitario(500.0);
        return d;
    }

    @Test
    void testEliminarDetalleInexistenteLanzaNotFound() {
        when(detallePedidoProveedorRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> detallePedidoProveedorService.eliminar(99L));
        verify(detallePedidoProveedorRepository, never()).deleteById(any());
    }

    @Test
    void testEliminarPedidoRecibidoImpedido() {
        when(detallePedidoProveedorRepository.findById(1L)).thenReturn(Optional.of(detalleConPedido(1L)));
        when(pedidoProveedorRepository.findById(1L)).thenReturn(Optional.of(pedido("recibido")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> detallePedidoProveedorService.eliminar(1L));
        assertEquals("No se puede eliminar detalle de pedido recibido", ex.getMessage());
    }

    @Test
    void testEliminarUltimoDetalleImpedido() {
        when(detallePedidoProveedorRepository.findById(1L)).thenReturn(Optional.of(detalleConPedido(1L)));
        when(pedidoProveedorRepository.findById(1L)).thenReturn(Optional.of(pedido("pendiente")));
        when(detallePedidoProveedorRepository.countByPedidoId(1L)).thenReturn(1L);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> detallePedidoProveedorService.eliminar(1L));
        assertEquals("Mínimo un detalle", ex.getMessage());
    }

    @Test
    void testEliminarCasoFeliz() {
        when(detallePedidoProveedorRepository.findById(1L)).thenReturn(Optional.of(detalleConPedido(1L)));
        when(pedidoProveedorRepository.findById(1L)).thenReturn(Optional.of(pedido("pendiente")));
        when(detallePedidoProveedorRepository.countByPedidoId(1L)).thenReturn(2L);
        detallePedidoProveedorService.eliminar(1L);
        verify(detallePedidoProveedorRepository).deleteById(1L);
    }

    @Test
    void testEliminarPedidoInexistenteLanzaNotFound() {
        when(detallePedidoProveedorRepository.findById(1L)).thenReturn(Optional.of(detalleConPedido(7L)));
        when(pedidoProveedorRepository.findById(7L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> detallePedidoProveedorService.eliminar(1L));
        assertEquals("Pedido no encontrado", ex.getMessage());
    }

    // ---- crear (CA-DET-01..14, 16) ----

    @Test
    void testCrearPedidoIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> detallePedidoProveedorService.crear(request(null, 1L, 10, 800.0)));
        assertEquals("El pedidoId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearPedidoInexistente() {
        when(pedidoProveedorRepository.findById(999L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> detallePedidoProveedorService.crear(request(999L, 1L, 10, 800.0)));
        assertEquals("Pedido no encontrado", ex.getMessage());
    }

    @Test
    void testCrearPedidoRecibidoNoEditable() {
        when(pedidoProveedorRepository.findById(1L)).thenReturn(Optional.of(pedido("recibido")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> detallePedidoProveedorService.crear(request(1L, 1L, 10, 800.0)));
        assertEquals("No se pueden agregar detalles a pedido recibido o cancelado", ex.getMessage());
    }

    @Test
    void testCrearPedidoCanceladoNoEditable() {
        when(pedidoProveedorRepository.findById(1L)).thenReturn(Optional.of(pedido("cancelado")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> detallePedidoProveedorService.crear(request(1L, 1L, 10, 800.0)));
        assertEquals("No se pueden agregar detalles a pedido recibido o cancelado", ex.getMessage());
    }

    @Test
    void testCrearProductoIdNull() {
        pedidoEditable();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> detallePedidoProveedorService.crear(request(1L, null, 10, 800.0)));
        assertEquals("El productoId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearProductoInexistente() {
        pedidoEditable();
        when(productoRepository.existsById(999L)).thenReturn(false);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> detallePedidoProveedorService.crear(request(1L, 999L, 10, 800.0)));
        assertEquals("Producto no encontrado", ex.getMessage());
    }

    @Test
    void testCrearProductoNoSuministrado() {
        pedidoEditable();
        when(productoRepository.existsById(5L)).thenReturn(true);
        when(proveedorProductoRepository.existsByProveedorIdAndProductoId(1L, 5L)).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> detallePedidoProveedorService.crear(request(1L, 5L, 10, 800.0)));
        assertEquals("Producto no suministrado por el proveedor", ex.getMessage());
    }

    @Test
    void testCrearProductoDuplicado() {
        pedidoEditable();
        when(productoRepository.existsById(1L)).thenReturn(true);
        when(proveedorProductoRepository.existsByProveedorIdAndProductoId(1L, 1L)).thenReturn(true);
        when(detallePedidoProveedorRepository.existsByPedidoIdAndProductoId(1L, 1L)).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class,
                () -> detallePedidoProveedorService.crear(request(1L, 1L, 10, 800.0)));
        assertEquals("Producto ya en el pedido. Modifique la cantidad", ex.getMessage());
    }

    @Test
    void testCrearCantidadNull() {
        pedidoEditable();
        when(productoRepository.existsById(1L)).thenReturn(true);
        when(proveedorProductoRepository.existsByProveedorIdAndProductoId(1L, 1L)).thenReturn(true);
        when(detallePedidoProveedorRepository.existsByPedidoIdAndProductoId(1L, 1L)).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> detallePedidoProveedorService.crear(request(1L, 1L, null, 800.0)));
        assertEquals("La cantidad es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearCantidadNoPositiva() {
        pedidoEditable();
        when(productoRepository.existsById(1L)).thenReturn(true);
        when(proveedorProductoRepository.existsByProveedorIdAndProductoId(1L, 1L)).thenReturn(true);
        when(detallePedidoProveedorRepository.existsByPedidoIdAndProductoId(1L, 1L)).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> detallePedidoProveedorService.crear(request(1L, 1L, 0, 800.0)));
        assertEquals("La cantidad debe ser mayor a 0", ex.getMessage());
    }

    @Test
    void testCrearPrecioNull() {
        pedidoEditable();
        when(productoRepository.existsById(1L)).thenReturn(true);
        when(proveedorProductoRepository.existsByProveedorIdAndProductoId(1L, 1L)).thenReturn(true);
        when(detallePedidoProveedorRepository.existsByPedidoIdAndProductoId(1L, 1L)).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> detallePedidoProveedorService.crear(request(1L, 1L, 10, null)));
        assertEquals("El precio unitario es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearPrecioNoPositivo() {
        pedidoEditable();
        when(productoRepository.existsById(1L)).thenReturn(true);
        when(proveedorProductoRepository.existsByProveedorIdAndProductoId(1L, 1L)).thenReturn(true);
        when(detallePedidoProveedorRepository.existsByPedidoIdAndProductoId(1L, 1L)).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> detallePedidoProveedorService.crear(request(1L, 1L, 10, 0.0)));
        assertEquals("El precio unitario debe ser mayor a 0", ex.getMessage());
    }

    @Test
    void testCrearCasoFelizCalculaSubtotal() {
        pedidoEditable();
        when(productoRepository.existsById(1L)).thenReturn(true);
        when(proveedorProductoRepository.existsByProveedorIdAndProductoId(1L, 1L)).thenReturn(true);
        when(detallePedidoProveedorRepository.existsByPedidoIdAndProductoId(1L, 1L)).thenReturn(false);
        when(detallePedidoProveedorRepository.save(any(DetallePedidoProveedor.class))).thenAnswer(inv -> inv.getArgument(0));

        DetallePedidoProveedor creado = detallePedidoProveedorService.crear(request(1L, 1L, 50, 800.0));

        assertEquals(40000.0, creado.getSubtotal());
    }

    // ---- actualizar (CA-DET-15) ----

    @Test
    void testActualizarRecalculaSubtotalConCantidad() {
        when(detallePedidoProveedorRepository.findById(1L)).thenReturn(Optional.of(detalleConPedido(1L)));
        when(pedidoProveedorRepository.findById(1L)).thenReturn(Optional.of(pedido("pendiente")));
        when(detallePedidoProveedorRepository.save(any(DetallePedidoProveedor.class))).thenAnswer(inv -> inv.getArgument(0));

        DetallePedidoProveedor actualizado = detallePedidoProveedorService.actualizar(1L, request(null, null, 20, null));

        assertEquals(10000.0, actualizado.getSubtotal());
    }

    @Test
    void testActualizarRecalculaSubtotalConPrecio() {
        when(detallePedidoProveedorRepository.findById(1L)).thenReturn(Optional.of(detalleConPedido(1L)));
        when(pedidoProveedorRepository.findById(1L)).thenReturn(Optional.of(pedido("pendiente")));
        when(detallePedidoProveedorRepository.save(any(DetallePedidoProveedor.class))).thenAnswer(inv -> inv.getArgument(0));

        DetallePedidoProveedor actualizado = detallePedidoProveedorService.actualizar(1L, request(null, null, null, 100.0));

        assertEquals(1000.0, actualizado.getSubtotal());
    }

    @Test
    void testActualizarPedidoCancelado() {
        when(detallePedidoProveedorRepository.findById(1L)).thenReturn(Optional.of(detalleConPedido(1L)));
        when(pedidoProveedorRepository.findById(1L)).thenReturn(Optional.of(pedido("cancelado")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> detallePedidoProveedorService.actualizar(1L, request(null, null, 20, null)));
        assertEquals("No se puede modificar detalle de pedido cancelado", ex.getMessage());
    }
}
