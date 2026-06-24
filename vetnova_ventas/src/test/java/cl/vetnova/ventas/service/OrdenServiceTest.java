package cl.vetnova.ventas.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cl.vetnova.ventas.client.InventarioClient;
import cl.vetnova.ventas.dto.ActualizarCantidadRequest;
import cl.vetnova.ventas.dto.CambiarEstadoRequest;
import cl.vetnova.ventas.dto.CrearOrdenRequest;
import cl.vetnova.ventas.dto.DetalleOrdenRequest;
import cl.vetnova.ventas.dto.ItemOrdenRequest;
import cl.vetnova.ventas.dto.OrdenResponse;
import cl.vetnova.ventas.exception.BusinessRuleException;
import cl.vetnova.ventas.exception.ResourceNotFoundException;
import cl.vetnova.ventas.model.DetalleOrden;
import cl.vetnova.ventas.model.EstadoOrden;
import cl.vetnova.ventas.model.Orden;
import cl.vetnova.ventas.model.Pago;
import cl.vetnova.ventas.repository.OrdenRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class OrdenServiceTest {

    @Mock
    private OrdenRepository ordenRepository;
    @Mock
    private InventarioClient inventarioClient;

    private OrdenService ordenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ordenService = new OrdenService(ordenRepository, inventarioClient, 0.19);
    }

    private CrearOrdenRequest requestConUnDetalle(int cantidad, double precio) {
        DetalleOrdenRequest detalle = new DetalleOrdenRequest();
        detalle.setProductoId(1L);
        detalle.setNombreProducto("Alimento perro adulto 15kg");
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(precio);
        CrearOrdenRequest request = new CrearOrdenRequest();
        request.setClienteId(2L);
        request.setIdSucursal(1L);
        request.setDetalles(List.of(detalle));
        return request;
    }

    @Test
    void testCrearOrdenCalculaSubtotalIvaYTotal() {
        when(inventarioClient.consultarStock(1L, 1L)).thenReturn(50);
        when(ordenRepository.save(any(Orden.class))).thenAnswer(inv -> inv.getArgument(0));

        OrdenResponse response = ordenService.crearOrden(requestConUnDetalle(2, 35990.0));

        assertEquals(71980.0, response.getSubtotal());
        assertEquals(13676.2, response.getImpuestos());
        assertEquals(85656.2, response.getTotal());
        assertEquals("PENDIENTE", response.getEstado());
        assertEquals(1, response.getDetalles().size());
    }

    @Test
    void testCrearOrdenConStockInsuficienteLanzaExcepcion() {
        when(inventarioClient.consultarStock(1L, 1L)).thenReturn(1);

        assertThrows(BusinessRuleException.class,
                () -> ordenService.crearOrden(requestConUnDetalle(5, 35990.0)));
        verify(ordenRepository, never()).save(any());
    }

    @Test
    void testCrearOrdenConsultaStockPorCadaDetalle() {
        when(inventarioClient.consultarStock(1L, 1L)).thenReturn(50);
        when(ordenRepository.save(any(Orden.class))).thenAnswer(inv -> inv.getArgument(0));

        ordenService.crearOrden(requestConUnDetalle(2, 1000.0));

        verify(inventarioClient).consultarStock(1L, 1L);
    }

    @Test
    void testObtenerPorIdInexistenteLanzaNotFound() {
        when(ordenRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ordenService.obtenerPorId(99L));
    }

    @Test
    void testExisteDevuelveLoQueDigaElRepositorio() {
        when(ordenRepository.existsById(1L)).thenReturn(true);
        when(ordenRepository.existsById(99L)).thenReturn(false);

        assertTrue(ordenService.existe(1L));
        assertFalse(ordenService.existe(99L));
    }

    @Test
    void testOrdenConfirmadaPuedePasarAEnviada() {
        Orden orden = new Orden();
        orden.setClienteId(2L);
        orden.setIdSucursal(1L);
        orden.setEstado(EstadoOrden.CONFIRMADA);
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        when(ordenRepository.save(any(Orden.class))).thenAnswer(inv -> inv.getArgument(0));

        CambiarEstadoRequest request = new CambiarEstadoRequest();
        request.setEstado("ENVIADA");
        OrdenResponse response = ordenService.cambiarEstado(1L, request);

        assertEquals("ENVIADA", response.getEstado());
    }

    @Test
    void testOrdenPendienteNoPuedePasarAEnviada() {
        Orden orden = new Orden();
        orden.setEstado(EstadoOrden.PENDIENTE);
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));

        CambiarEstadoRequest request = new CambiarEstadoRequest();
        request.setEstado("ENVIADA");

        assertThrows(BusinessRuleException.class, () -> ordenService.cambiarEstado(1L, request));
    }

    @Test
    void testOrdenEntregadaNoSePuedeModificar() {
        Orden orden = new Orden();
        orden.setEstado(EstadoOrden.ENTREGADA);
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));

        CambiarEstadoRequest request = new CambiarEstadoRequest();
        request.setEstado("CANCELADA");

        assertThrows(BusinessRuleException.class, () -> ordenService.cambiarEstado(1L, request));
    }

    @Test
    void testSoloUnaOrdenEnviadaPuedeQuedarEntregada() {
        Orden orden = new Orden();
        orden.setEstado(EstadoOrden.CONFIRMADA);
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));

        CambiarEstadoRequest request = new CambiarEstadoRequest();
        request.setEstado("ENTREGADA");

        assertThrows(BusinessRuleException.class, () -> ordenService.cambiarEstado(1L, request));
    }

    @Test
    void testListarMapeaTodasLasOrdenes() {
        Orden orden = new Orden();
        orden.setClienteId(2L);
        orden.setIdSucursal(1L);
        when(ordenRepository.findAll()).thenReturn(List.of(orden));

        List<OrdenResponse> lista = ordenService.listar();

        assertEquals(1, lista.size());
        assertEquals(2L, lista.get(0).getClienteId());
    }

    private CambiarEstadoRequest cambioA(String estado) {
        CambiarEstadoRequest request = new CambiarEstadoRequest();
        request.setEstado(estado);
        return request;
    }

    @Test
    void testObtenerOrdenPorIdDevuelveLaRespuesta() {
        Orden orden = new Orden();
        orden.setEstado(EstadoOrden.PENDIENTE);
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));

        assertNotNull(ordenService.obtenerPorId(1L));
    }

    @Test
    void testCambiarEstadoDeOrdenFinalLanzaBusinessRule() {
        Orden orden = new Orden();
        orden.setEstado(EstadoOrden.ENTREGADA);
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));

        assertThrows(BusinessRuleException.class, () -> ordenService.cambiarEstado(1L, cambioA("CANCELADA")));
    }

    @Test
    void testEntregarOrdenQueNoFueEnviadaLanzaBusinessRule() {
        Orden orden = new Orden();
        orden.setEstado(EstadoOrden.PENDIENTE);
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));

        assertThrows(BusinessRuleException.class, () -> ordenService.cambiarEstado(1L, cambioA("ENTREGADA")));
    }

    @Test
    void testConfirmarOrdenQueNoEstaPendienteLanzaBusinessRule() {
        Orden orden = new Orden();
        orden.setEstado(EstadoOrden.ENVIADA);
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));

        assertThrows(BusinessRuleException.class, () -> ordenService.cambiarEstado(1L, cambioA("CONFIRMADA")));
    }

    @Test
    void testCambiarEstadoDeOrdenCanceladaLanzaBusinessRule() {
        Orden orden = new Orden();
        orden.setEstado(EstadoOrden.CANCELADA);
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));

        assertThrows(BusinessRuleException.class, () -> ordenService.cambiarEstado(1L, cambioA("CONFIRMADA")));
    }

    @Test
    void testEntregarOrdenEnviadaFunciona() {
        Orden orden = new Orden();
        orden.setEstado(EstadoOrden.ENVIADA);
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        when(ordenRepository.save(any(Orden.class))).thenAnswer(inv -> inv.getArgument(0));

        assertNotNull(ordenService.cambiarEstado(1L, cambioA("ENTREGADA")));
        assertEquals(EstadoOrden.ENTREGADA, orden.getEstado());
    }

    @Test
    void testConfirmarOrdenPendienteFunciona() {
        Orden orden = new Orden();
        orden.setEstado(EstadoOrden.PENDIENTE);
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        when(ordenRepository.save(any(Orden.class))).thenAnswer(inv -> inv.getArgument(0));

        assertNotNull(ordenService.cambiarEstado(1L, cambioA("CONFIRMADA")));
        assertEquals(EstadoOrden.CONFIRMADA, orden.getEstado());
    }

    // ---- confirmar / cancelar (CA-ORD-07..09, 14..20) ----

    private Orden ordenConDetalle(EstadoOrden estado, String estadoPago) {
        Orden orden = new Orden();
        orden.setIdSucursal(1L);
        orden.setEstado(estado);
        DetalleOrden detalle = new DetalleOrden();
        detalle.setProductoId(1L);
        detalle.setCantidad(2);
        orden.addDetalle(detalle);
        if (estadoPago != null) {
            Pago pago = new Pago();
            pago.setEstado(estadoPago);
            orden.addPago(pago);
        }
        return orden;
    }

    @Test
    void testConfirmarOrdenCanceladaLanzaBusinessRule() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(ordenConDetalle(EstadoOrden.CANCELADA, "APROBADO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> ordenService.confirmar(1L));
        assertEquals("No se puede confirmar una orden cancelada", ex.getMessage());
    }

    @Test
    void testConfirmarOrdenNoPendienteLanzaBusinessRule() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(ordenConDetalle(EstadoOrden.CONFIRMADA, "APROBADO")));
        assertThrows(BusinessRuleException.class, () -> ordenService.confirmar(1L));
    }

    @Test
    void testConfirmarOrdenSinItemsLanzaBusinessRule() {
        Orden orden = new Orden();
        orden.setEstado(EstadoOrden.PENDIENTE);
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> ordenService.confirmar(1L));
        assertEquals("La orden no tiene ítems", ex.getMessage());
    }

    @Test
    void testConfirmarSinPagoAprobadoLanzaBusinessRule() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(ordenConDetalle(EstadoOrden.PENDIENTE, "PENDIENTE")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> ordenService.confirmar(1L));
        assertEquals("La orden no puede confirmarse sin un pago aprobado", ex.getMessage());
    }

    @Test
    void testConfirmarSinStockLanzaBusinessRule() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(ordenConDetalle(EstadoOrden.PENDIENTE, "APROBADO")));
        when(inventarioClient.consultarStock(1L, 1L)).thenReturn(1);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> ordenService.confirmar(1L));
        assertEquals("Stock insuficiente para confirmar la orden", ex.getMessage());
    }

    @Test
    void testConfirmarCasoFelizDescuentaStock() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(ordenConDetalle(EstadoOrden.PENDIENTE, "APROBADO")));
        when(inventarioClient.consultarStock(1L, 1L)).thenReturn(50);
        when(ordenRepository.save(any(Orden.class))).thenAnswer(inv -> inv.getArgument(0));

        OrdenResponse response = ordenService.confirmar(1L);

        assertEquals("CONFIRMADA", response.getEstado());
        assertNotNull(response.getFechaConfirmacion());
        verify(inventarioClient).registrarSalida(1L, 1L, 2, "Confirmación orden 1");
    }

    @Test
    void testCancelarOrdenEntregadaLanzaBusinessRule() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(ordenConDetalle(EstadoOrden.ENTREGADA, null)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> ordenService.cancelar(1L));
        assertEquals("No se puede cancelar una orden ya entregada", ex.getMessage());
    }

    @Test
    void testCancelarOrdenEnviadaLanzaBusinessRule() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(ordenConDetalle(EstadoOrden.ENVIADA, null)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> ordenService.cancelar(1L));
        assertEquals("No se puede cancelar una orden que ya fue enviada", ex.getMessage());
    }

    @Test
    void testCancelarOrdenConfirmadaReponeStock() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(ordenConDetalle(EstadoOrden.CONFIRMADA, "APROBADO")));
        when(ordenRepository.save(any(Orden.class))).thenAnswer(inv -> inv.getArgument(0));

        OrdenResponse response = ordenService.cancelar(1L);

        assertEquals("CANCELADA", response.getEstado());
        verify(inventarioClient).registrarEntrada(1L, 1L, 2, "Cancelación orden 1");
    }

    @Test
    void testCancelarOrdenPendienteNoTocaStock() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(ordenConDetalle(EstadoOrden.PENDIENTE, null)));
        when(ordenRepository.save(any(Orden.class))).thenAnswer(inv -> inv.getArgument(0));

        OrdenResponse response = ordenService.cancelar(1L);

        assertEquals("CANCELADA", response.getEstado());
        verify(inventarioClient, never()).registrarEntrada(any(), any(), any(), any());
    }

    // ---- agregar / actualizar / eliminar detalle (CA-DOR) ----

    private ItemOrdenRequest item(Long itemId, String tipo, Integer cantidad, Double precio) {
        ItemOrdenRequest request = new ItemOrdenRequest();
        request.setItemId(itemId);
        request.setTipoItem(tipo);
        request.setCantidad(cantidad);
        request.setPrecioUnitario(precio);
        return request;
    }

    private DetalleOrden detalleConId(Long id, double precio, int cantidad) {
        DetalleOrden detalle = new DetalleOrden();
        detalle.setId(id);
        detalle.setProductoId(10L);
        detalle.setPrecioUnitario(precio);
        detalle.setCantidad(cantidad);
        detalle.setSubtotal(precio * cantidad);
        return detalle;
    }

    private Orden ordenPendienteVacia() {
        Orden orden = new Orden();
        orden.setEstado(EstadoOrden.PENDIENTE);
        return orden;
    }

    @Test
    void testAgregarDetalleOrdenNoPendiente() {
        Orden orden = new Orden();
        orden.setEstado(EstadoOrden.CONFIRMADA);
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> ordenService.agregarDetalle(1L, item(10L, "PRODUCTO", 2, 500.0)));
        assertEquals("No se pueden agregar ítems a una orden que no está en estado PENDIENTE", ex.getMessage());
    }

    @Test
    void testAgregarDetalleItemIdNull() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(ordenPendienteVacia()));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> ordenService.agregarDetalle(1L, item(null, "PRODUCTO", 2, 500.0)));
        assertEquals("El itemId es obligatorio", ex.getMessage());
    }

    @Test
    void testAgregarDetalleTipoNull() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(ordenPendienteVacia()));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> ordenService.agregarDetalle(1L, item(10L, null, 2, 500.0)));
        assertEquals("El tipoItem es obligatorio", ex.getMessage());
    }

    @Test
    void testAgregarDetalleTipoInvalido() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(ordenPendienteVacia()));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> ordenService.agregarDetalle(1L, item(10L, "COMBO", 2, 500.0)));
        assertEquals("Tipo de ítem no válido. Valores permitidos: PRODUCTO, SERVICIO", ex.getMessage());
    }

    @Test
    void testAgregarDetalleCantidadNull() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(ordenPendienteVacia()));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> ordenService.agregarDetalle(1L, item(10L, "PRODUCTO", null, 500.0)));
        assertEquals("La cantidad es obligatoria", ex.getMessage());
    }

    @Test
    void testAgregarDetalleCantidadNoPositiva() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(ordenPendienteVacia()));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> ordenService.agregarDetalle(1L, item(10L, "PRODUCTO", 0, 500.0)));
        assertEquals("La cantidad debe ser mayor a 0", ex.getMessage());
    }

    @Test
    void testAgregarDetallePrecioNull() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(ordenPendienteVacia()));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> ordenService.agregarDetalle(1L, item(10L, "PRODUCTO", 2, null)));
        assertEquals("El precio unitario es obligatorio", ex.getMessage());
    }

    @Test
    void testAgregarDetallePrecioNegativo() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(ordenPendienteVacia()));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> ordenService.agregarDetalle(1L, item(10L, "PRODUCTO", 2, -100.0)));
        assertEquals("El precio unitario no puede ser negativo", ex.getMessage());
    }

    @Test
    void testAgregarDetalleSubtotalInconsistente() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(ordenPendienteVacia()));
        ItemOrdenRequest request = item(10L, "PRODUCTO", 2, 500.0);
        request.setSubtotal(800.0);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> ordenService.agregarDetalle(1L, request));
        assertEquals("El subtotal debe ser igual a cantidad × precioUnitario", ex.getMessage());
    }

    @Test
    void testAgregarDetalleSubtotalConsistenteEsValido() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(ordenPendienteVacia()));
        when(ordenRepository.save(any(Orden.class))).thenAnswer(inv -> inv.getArgument(0));
        ItemOrdenRequest request = item(10L, "PRODUCTO", 2, 500.0);
        request.setSubtotal(1000.0);
        OrdenResponse response = ordenService.agregarDetalle(1L, request);
        assertEquals(1000.0, response.getSubtotal());
    }

    @Test
    void testAgregarDetallePrecioCeroEsValido() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(ordenPendienteVacia()));
        when(ordenRepository.save(any(Orden.class))).thenAnswer(inv -> inv.getArgument(0));
        OrdenResponse response = ordenService.agregarDetalle(1L, item(10L, "SERVICIO", 1, 0.0));
        assertEquals(0.0, response.getDetalles().get(0).getSubtotal());
    }

    @Test
    void testAgregarDetalleCasoFelizRecalculaTotal() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(ordenPendienteVacia()));
        when(ordenRepository.save(any(Orden.class))).thenAnswer(inv -> inv.getArgument(0));
        OrdenResponse response = ordenService.agregarDetalle(1L, item(10L, "PRODUCTO", 2, 500.0));
        assertEquals(1000.0, response.getSubtotal());
        assertEquals(1190.0, response.getTotal());
    }

    @Test
    void testActualizarDetalleNoEncontrado() {
        Orden orden = ordenPendienteVacia();
        orden.addDetalle(detalleConId(5L, 500.0, 2));
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        ActualizarCantidadRequest request = new ActualizarCantidadRequest();
        request.setCantidad(4);
        assertThrows(ResourceNotFoundException.class,
                () -> ordenService.actualizarDetalle(1L, 99L, request));
    }

    @Test
    void testActualizarDetalleCantidadNoPositiva() {
        Orden orden = ordenPendienteVacia();
        orden.addDetalle(detalleConId(5L, 500.0, 2));
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        ActualizarCantidadRequest request = new ActualizarCantidadRequest();
        request.setCantidad(0);
        assertThrows(BusinessRuleException.class, () -> ordenService.actualizarDetalle(1L, 5L, request));
    }

    @Test
    void testActualizarDetalleCantidadNull() {
        Orden orden = ordenPendienteVacia();
        orden.addDetalle(detalleConId(5L, 500.0, 2));
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        ActualizarCantidadRequest request = new ActualizarCantidadRequest();
        assertThrows(BusinessRuleException.class, () -> ordenService.actualizarDetalle(1L, 5L, request));
    }

    @Test
    void testActualizarDetalleCasoFelizRecalcula() {
        Orden orden = ordenPendienteVacia();
        orden.addDetalle(detalleConId(5L, 500.0, 2));
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        when(ordenRepository.save(any(Orden.class))).thenAnswer(inv -> inv.getArgument(0));
        ActualizarCantidadRequest request = new ActualizarCantidadRequest();
        request.setCantidad(4);
        OrdenResponse response = ordenService.actualizarDetalle(1L, 5L, request);
        assertEquals(2000.0, response.getDetalles().get(0).getSubtotal());
        assertEquals(2000.0, response.getSubtotal());
    }

    @Test
    void testEliminarDetalleUnicoImpedido() {
        Orden orden = ordenPendienteVacia();
        orden.addDetalle(detalleConId(5L, 500.0, 2));
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> ordenService.eliminarDetalle(1L, 5L));
        assertEquals("La orden debe tener al menos un ítem", ex.getMessage());
    }

    @Test
    void testEliminarDetalleCasoFeliz() {
        Orden orden = ordenPendienteVacia();
        orden.addDetalle(detalleConId(5L, 500.0, 2));
        orden.addDetalle(detalleConId(6L, 200.0, 1));
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        when(ordenRepository.save(any(Orden.class))).thenAnswer(inv -> inv.getArgument(0));
        OrdenResponse response = ordenService.eliminarDetalle(1L, 5L);
        assertEquals(1, response.getDetalles().size());
        assertEquals(200.0, response.getSubtotal());
    }
}
