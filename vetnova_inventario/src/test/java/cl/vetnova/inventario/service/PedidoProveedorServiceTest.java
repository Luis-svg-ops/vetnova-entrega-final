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
import cl.vetnova.inventario.dto.PedidoProveedorRequest;
import cl.vetnova.inventario.exception.BusinessRuleException;
import cl.vetnova.inventario.exception.ResourceNotFoundException;
import cl.vetnova.inventario.model.DetallePedidoProveedor;
import cl.vetnova.inventario.model.Inventario;
import cl.vetnova.inventario.model.MovimientoStock;
import cl.vetnova.inventario.model.PedidoProveedor;
import cl.vetnova.inventario.model.Proveedor;
import cl.vetnova.inventario.model.TipoMovimiento;
import cl.vetnova.inventario.repository.DetallePedidoProveedorRepository;
import cl.vetnova.inventario.repository.InventarioRepository;
import cl.vetnova.inventario.repository.PedidoProveedorRepository;
import cl.vetnova.inventario.repository.ProveedorProductoRepository;
import cl.vetnova.inventario.repository.ProveedorRepository;

public class PedidoProveedorServiceTest {

    @Mock
    private PedidoProveedorRepository pedidoProveedorRepository;
    @Mock
    private ProveedorRepository proveedorRepository;
    @Mock
    private ProveedorProductoRepository proveedorProductoRepository;
    @Mock
    private DetallePedidoProveedorRepository detallePedidoProveedorRepository;
    @Mock
    private InventarioRepository inventarioRepository;
    @Mock
    private OperacionStock operacionStock;
    @InjectMocks
    private PedidoProveedorService pedidoProveedorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private DetallePedidoRequest detalle(Long productoId, Integer cantidad, Double precio) {
        DetallePedidoRequest d = new DetallePedidoRequest();
        d.setProductoId(productoId);
        d.setCantidad(cantidad);
        d.setPrecioUnitario(precio);
        return d;
    }

    private PedidoProveedorRequest request(Long proveedorId, String sucursal, List<DetallePedidoRequest> detalles) {
        PedidoProveedorRequest r = new PedidoProveedorRequest();
        r.setProveedorId(proveedorId);
        r.setSucursal(sucursal);
        r.setResponsable("Ana");
        r.setDetalles(detalles);
        return r;
    }

    private Proveedor proveedor(boolean activo) {
        Proveedor p = new Proveedor();
        p.setActivo(activo);
        return p;
    }

    private PedidoProveedor pedido(String estado) {
        PedidoProveedor p = new PedidoProveedor();
        p.setId(1L);
        p.setProveedorId(1L);
        p.setSucursal("SANTIAGO");
        p.setResponsable("Ana");
        p.setEstado(estado);
        return p;
    }

    // ---- CRUD ----

    @Test
    void testListar() {
        when(pedidoProveedorRepository.findAll()).thenReturn(List.of(new PedidoProveedor()));
        assertEquals(1, pedidoProveedorService.listar().size());
    }

    @Test
    void testObtenerPorIdInexistenteLanzaNotFound() {
        when(pedidoProveedorRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> pedidoProveedorService.obtenerPorId(99L));
    }

    @Test
    void testActualizarExistente() {
        when(pedidoProveedorRepository.findById(1L)).thenReturn(Optional.of(new PedidoProveedor()));
        when(pedidoProveedorRepository.save(any(PedidoProveedor.class))).thenAnswer(inv -> inv.getArgument(0));
        assertNotNull(pedidoProveedorService.actualizar(1L, pedido("pendiente")));
    }

    @Test
    void testEliminarExistente() {
        when(pedidoProveedorRepository.existsById(1L)).thenReturn(true);
        pedidoProveedorService.eliminar(1L);
        verify(pedidoProveedorRepository).deleteById(1L);
    }

    @Test
    void testEliminarInexistenteLanzaNotFound() {
        when(pedidoProveedorRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> pedidoProveedorService.eliminar(99L));
        verify(pedidoProveedorRepository, never()).deleteById(any());
    }

    // ---- crear (CA-PED-01..09) ----

    @Test
    void testCrearProveedorIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> pedidoProveedorService.crear(request(null, "SANTIAGO", List.of(detalle(1L, 5, 100.0)))));
        assertEquals("El proveedorId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearProveedorInexistente() {
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> pedidoProveedorService.crear(request(99L, "SANTIAGO", List.of(detalle(1L, 5, 100.0)))));
        assertEquals("Proveedor no encontrado", ex.getMessage());
    }

    @Test
    void testCrearProveedorInactivo() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor(false)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> pedidoProveedorService.crear(request(1L, "SANTIAGO", List.of(detalle(1L, 5, 100.0)))));
        assertEquals("Proveedor inactivo", ex.getMessage());
    }

    @Test
    void testCrearSucursalNull() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor(true)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> pedidoProveedorService.crear(request(1L, null, List.of(detalle(1L, 5, 100.0)))));
        assertEquals("La sucursal es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearSucursalInexistente() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor(true)));
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> pedidoProveedorService.crear(request(1L, "FANTASMA", List.of(detalle(1L, 5, 100.0)))));
        assertEquals("Sucursal no encontrada", ex.getMessage());
    }

    @Test
    void testCrearDetallesNull() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor(true)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> pedidoProveedorService.crear(request(1L, "SANTIAGO", null)));
        assertEquals("El pedido debe tener al menos un detalle", ex.getMessage());
    }

    @Test
    void testCrearDetallesVacios() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor(true)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> pedidoProveedorService.crear(request(1L, "SANTIAGO", List.of())));
        assertEquals("El pedido debe tener al menos un detalle", ex.getMessage());
    }

    @Test
    void testCrearProductoNoSuministrado() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor(true)));
        when(proveedorProductoRepository.existsByProveedorIdAndProductoId(1L, 5L)).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> pedidoProveedorService.crear(request(1L, "SANTIAGO", List.of(detalle(5L, 5, 100.0)))));
        assertEquals("Producto no suministrado por este proveedor", ex.getMessage());
    }

    @Test
    void testCrearCasoFeliz() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor(true)));
        when(proveedorProductoRepository.existsByProveedorIdAndProductoId(1L, 1L)).thenReturn(true);
        when(pedidoProveedorRepository.save(any(PedidoProveedor.class))).thenAnswer(inv -> {
            PedidoProveedor p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });
        when(detallePedidoProveedorRepository.save(any(DetallePedidoProveedor.class))).thenAnswer(inv -> inv.getArgument(0));

        PedidoProveedor creado = pedidoProveedorService.crear(request(1L, "SANTIAGO", List.of(detalle(1L, 10, 800.0))));

        assertEquals("pendiente", creado.getEstado());
        assertNotNull(creado.getFechaPedido());
        verify(detallePedidoProveedorRepository).save(any(DetallePedidoProveedor.class));
    }

    // ---- enviar (CA-PED-10..12) ----

    @Test
    void testEnviarDesdePendiente() {
        when(pedidoProveedorRepository.findById(1L)).thenReturn(Optional.of(pedido("pendiente")));
        when(pedidoProveedorRepository.save(any(PedidoProveedor.class))).thenAnswer(inv -> inv.getArgument(0));
        assertEquals("enviado", pedidoProveedorService.enviar(1L).getEstado());
    }

    @Test
    void testEnviarYaEnviado() {
        when(pedidoProveedorRepository.findById(1L)).thenReturn(Optional.of(pedido("enviado")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> pedidoProveedorService.enviar(1L));
        assertEquals("El pedido ya fue enviado", ex.getMessage());
    }

    @Test
    void testEnviarYaRecibido() {
        when(pedidoProveedorRepository.findById(1L)).thenReturn(Optional.of(pedido("recibido")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> pedidoProveedorService.enviar(1L));
        assertEquals("No se puede enviar pedido ya recibido", ex.getMessage());
    }

    // ---- recibir (CA-PED-13..15) ----

    @Test
    void testRecibirDesdePendienteEsInvalido() {
        when(pedidoProveedorRepository.findById(1L)).thenReturn(Optional.of(pedido("pendiente")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> pedidoProveedorService.recibir(1L));
        assertEquals("Debe estar enviado antes de recibir", ex.getMessage());
    }

    @Test
    void testRecibirCasoFelizConInventarioExistente() {
        when(pedidoProveedorRepository.findById(1L)).thenReturn(Optional.of(pedido("enviado")));
        DetallePedidoProveedor det = new DetallePedidoProveedor();
        det.setProductoId(1L);
        det.setCantidad(10);
        when(detallePedidoProveedorRepository.findByPedidoId(1L)).thenReturn(List.of(det));
        when(inventarioRepository.findByProductoIdAndSucursal(1L, "SANTIAGO"))
                .thenReturn(Optional.of(new Inventario()));
        when(operacionStock.aplicarMovimiento(any(Inventario.class), any(TipoMovimiento.class), any(), any(), any()))
                .thenReturn(new MovimientoStock());
        when(pedidoProveedorRepository.save(any(PedidoProveedor.class))).thenAnswer(inv -> inv.getArgument(0));

        PedidoProveedor recibido = pedidoProveedorService.recibir(1L);

        assertEquals("recibido", recibido.getEstado());
        assertNotNull(recibido.getFechaRecepcion());
        verify(operacionStock).aplicarMovimiento(any(Inventario.class), any(TipoMovimiento.class), any(), any(), any());
    }

    @Test
    void testRecibirCreaInventarioSiNoExiste() {
        when(pedidoProveedorRepository.findById(1L)).thenReturn(Optional.of(pedido("enviado")));
        DetallePedidoProveedor det = new DetallePedidoProveedor();
        det.setProductoId(1L);
        det.setCantidad(10);
        when(detallePedidoProveedorRepository.findByPedidoId(1L)).thenReturn(List.of(det));
        when(inventarioRepository.findByProductoIdAndSucursal(1L, "SANTIAGO")).thenReturn(Optional.empty());
        when(operacionStock.aplicarMovimiento(any(Inventario.class), any(TipoMovimiento.class), any(), any(), any()))
                .thenReturn(new MovimientoStock());
        when(pedidoProveedorRepository.save(any(PedidoProveedor.class))).thenAnswer(inv -> inv.getArgument(0));

        assertEquals("recibido", pedidoProveedorService.recibir(1L).getEstado());
    }

    // ---- cancelar (CA-PED-16..18) ----

    @Test
    void testCancelarDesdePendiente() {
        when(pedidoProveedorRepository.findById(1L)).thenReturn(Optional.of(pedido("pendiente")));
        when(pedidoProveedorRepository.save(any(PedidoProveedor.class))).thenAnswer(inv -> inv.getArgument(0));
        assertEquals("cancelado", pedidoProveedorService.cancelar(1L).getEstado());
    }

    @Test
    void testCancelarYaRecibidoImpedido() {
        when(pedidoProveedorRepository.findById(1L)).thenReturn(Optional.of(pedido("recibido")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> pedidoProveedorService.cancelar(1L));
        assertEquals("No se puede cancelar pedido ya recibido", ex.getMessage());
    }
}
