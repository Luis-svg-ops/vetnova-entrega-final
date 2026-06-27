package cl.vetnova.ventas.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cl.vetnova.ventas.client.CatalogoClient;
import cl.vetnova.ventas.client.InventarioClient;
import cl.vetnova.ventas.dto.CrearPagoRequest;
import cl.vetnova.ventas.dto.OrdenResponse;
import cl.vetnova.ventas.dto.PagoResponse;
import cl.vetnova.ventas.dto.RegistrarPagoRequest;
import cl.vetnova.ventas.exception.BusinessRuleException;
import cl.vetnova.ventas.exception.ConflictException;
import cl.vetnova.ventas.exception.ResourceNotFoundException;
import cl.vetnova.ventas.model.DetalleOrden;
import cl.vetnova.ventas.model.EstadoOrden;
import cl.vetnova.ventas.model.Orden;
import cl.vetnova.ventas.model.Pago;
import cl.vetnova.ventas.repository.OrdenRepository;
import cl.vetnova.ventas.repository.PagoRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PagoServiceTest {

    @Mock
    private OrdenRepository ordenRepository;
    @Mock
    private InventarioClient inventarioClient;
    @Mock
    private PagoRepository pagoRepository;
    @Mock
    private PasarelaPago pasarelaPago;
    @Mock
    private cl.vetnova.ventas.client.AuthClient authClient;
    @Mock
    private CatalogoClient catalogoClient;

    private OrdenService ordenService;
    private PagoService pagoService;
    private Orden orden;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ordenService = new OrdenService(ordenRepository, inventarioClient, authClient, catalogoClient, 0.19);
        pagoService = new PagoService(ordenRepository, ordenService, inventarioClient, pagoRepository, pasarelaPago);

        orden = new Orden();
        orden.setClienteId(2L);
        orden.setSucursal("CHILLAN");
        orden.setEstado(EstadoOrden.PENDIENTE);
        orden.setSubtotal(71980.0);
        orden.setImpuestos(13676.2);
        orden.setTotal(85656.2);
        DetalleOrden detalle = new DetalleOrden();
        detalle.setProductoId(1L);
        detalle.setNombreProducto("Alimento perro adulto 15kg");
        detalle.setCantidad(2);
        detalle.setPrecioUnitario(35990.0);
        detalle.setSubtotal(71980.0);
        orden.addDetalle(detalle);
    }

    private RegistrarPagoRequest pago(double monto) {
        RegistrarPagoRequest request = new RegistrarPagoRequest();
        request.setMetodo("DEBITO");
        request.setMonto(monto);
        request.setReferencia("TRX-0001");
        return request;
    }

    @Test
    void testPagoAprobadoConfirmaLaOrdenYDescuentaStock() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        when(ordenRepository.save(any(Orden.class))).thenAnswer(inv -> inv.getArgument(0));

        OrdenResponse response = pagoService.registrarPago(1L, pago(85656.2));

        assertEquals("CONFIRMADA", response.getEstado());
        assertNotNull(response.getFechaConfirmacion());
        assertEquals(1, response.getPagos().size());
        assertEquals("APROBADO", response.getPagos().get(0).getEstado());
        verify(inventarioClient).registrarSalida(1L, "CHILLAN", 2, "Venta orden " + orden.getId());
    }

    @Test
    void testPagoConMontoDistintoAlTotalLanzaExcepcion() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));

        assertThrows(BusinessRuleException.class, () -> pagoService.registrarPago(1L, pago(1000.0)));
        verify(inventarioClient, never()).registrarSalida(any(), any(), any(), any());
    }

    @Test
    void testNoSePuedePagarUnaOrdenYaConfirmada() {
        orden.setEstado(EstadoOrden.CONFIRMADA);
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));

        assertThrows(BusinessRuleException.class, () -> pagoService.registrarPago(1L, pago(85656.2)));
        verify(ordenRepository, never()).save(any());
    }

    // ---- crearPago (CA-PAG-01..11) ----

    private CrearPagoRequest crearReq(Long ordenId, String metodo, Double monto, String referencia) {
        CrearPagoRequest request = new CrearPagoRequest();
        request.setOrdenId(ordenId);
        request.setMetodo(metodo);
        request.setMonto(monto);
        request.setReferencia(referencia);
        return request;
    }

    private Pago pagoConEstado(String estado) {
        Pago p = new Pago();
        p.setId(1L);
        p.setOrden(orden);
        p.setMonto(85656.2);
        p.setEstado(estado);
        return p;
    }

    @Test
    void testCrearPagoOrdenIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> pagoService.crearPago(crearReq(null, "TARJETA", 85656.2, "R1")));
        assertEquals("El ordenId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearPagoOrdenInexistente() {
        when(ordenRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> pagoService.crearPago(crearReq(999L, "TARJETA", 85656.2, "R1")));
    }

    @Test
    void testCrearPagoOrdenYaTienePagoAprobado() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        when(pagoRepository.existsByOrdenIdAndEstado(1L, "APROBADO")).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class,
                () -> pagoService.crearPago(crearReq(1L, "TARJETA", 85656.2, "R1")));
        assertEquals("La orden ya tiene un pago aprobado", ex.getMessage());
    }

    @Test
    void testCrearPagoMetodoNull() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> pagoService.crearPago(crearReq(1L, null, 85656.2, "R1")));
        assertEquals("El método de pago es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearPagoMetodoInvalido() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> pagoService.crearPago(crearReq(1L, "BITCOIN", 85656.2, "R1")));
        assertEquals("Método de pago no válido. Valores permitidos: TARJETA, EFECTIVO, TRANSFERENCIA, DEBITO",
                ex.getMessage());
    }

    @Test
    void testCrearPagoMontoNull() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> pagoService.crearPago(crearReq(1L, "TARJETA", null, "R1")));
        assertEquals("El monto es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearPagoMontoNoPositivo() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> pagoService.crearPago(crearReq(1L, "TARJETA", 0.0, "R1")));
        assertEquals("El monto debe ser mayor a 0", ex.getMessage());
    }

    @Test
    void testCrearPagoMontoDistintoAlTotal() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> pagoService.crearPago(crearReq(1L, "TARJETA", 1000.0, "R1")));
        assertEquals("El monto debe ser igual al total de la orden (85656.2)", ex.getMessage());
    }

    @Test
    void testCrearPagoReferenciaDuplicada() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        when(pagoRepository.existsByReferencia("TXN-1")).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class,
                () -> pagoService.crearPago(crearReq(1L, "TARJETA", 85656.2, "TXN-1")));
        assertEquals("Ya existe un pago con esa referencia", ex.getMessage());
    }

    @Test
    void testCrearPagoCasoFeliz() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));
        PagoResponse response = pagoService.crearPago(crearReq(1L, "TARJETA", 85656.2, "TXN-1"));
        assertEquals("PENDIENTE", response.getEstado());
    }

    @Test
    void testCrearPagoSinReferencia() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));
        PagoResponse response = pagoService.crearPago(crearReq(1L, "EFECTIVO", 85656.2, null));
        assertEquals("PENDIENTE", response.getEstado());
    }

    // ---- procesar (CA-PAG-12/13/14) ----

    @Test
    void testProcesarPagoYaProcesado() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoConEstado("APROBADO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> pagoService.procesar(1L));
        assertEquals("El pago ya fue procesado", ex.getMessage());
    }

    @Test
    void testProcesarPagoInexistente() {
        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> pagoService.procesar(99L));
    }

    @Test
    void testProcesarPasarelaAprueba() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoConEstado("PENDIENTE")));
        when(pasarelaPago.autorizar(any(Pago.class))).thenReturn(true);
        when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));
        when(ordenRepository.save(any(Orden.class))).thenAnswer(inv -> inv.getArgument(0));

        PagoResponse response = pagoService.procesar(1L);

        assertEquals("APROBADO", response.getEstado());
        assertEquals(EstadoOrden.CONFIRMADA, orden.getEstado());
    }

    @Test
    void testProcesarPasarelaRechaza() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoConEstado("PENDIENTE")));
        when(pasarelaPago.autorizar(any(Pago.class))).thenReturn(false);
        when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));

        PagoResponse response = pagoService.procesar(1L);

        assertEquals("PENDIENTE", response.getEstado());
        verify(ordenRepository, never()).save(any());
    }

    // ---- confirmar (CA-PAG-15/16) ----

    @Test
    void testConfirmarPagoNoAprobado() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoConEstado("PENDIENTE")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> pagoService.confirmar(1L));
        assertEquals("Solo se puede confirmar un pago aprobado por la pasarela", ex.getMessage());
    }

    @Test
    void testConfirmarCasoFeliz() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoConEstado("APROBADO")));
        when(ordenRepository.save(any(Orden.class))).thenAnswer(inv -> inv.getArgument(0));
        pagoService.confirmar(1L);
        assertEquals(EstadoOrden.CONFIRMADA, orden.getEstado());
        assertNotNull(orden.getFechaConfirmacion());
    }

    // ---- rechazar (CA-PAG-17/18) ----

    @Test
    void testRechazarPagoYaAprobado() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoConEstado("APROBADO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> pagoService.rechazar(1L, "fondos insuficientes"));
        assertEquals("No se puede rechazar un pago ya aprobado", ex.getMessage());
    }

    @Test
    void testRechazarCasoFeliz() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoConEstado("PENDIENTE")));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));
        PagoResponse response = pagoService.rechazar(1L, "fondos insuficientes");
        assertEquals("RECHAZADO", response.getEstado());
    }

    // ---- reembolsar (CA-PAG-19/20/21) ----

    @Test
    void testReembolsarPagoNoAprobado() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoConEstado("RECHAZADO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> pagoService.reembolsar(1L));
        assertEquals("Solo se puede reembolsar un pago aprobado", ex.getMessage());
    }

    @Test
    void testReembolsarOrdenEntregada() {
        orden.setEstado(EstadoOrden.ENTREGADA);
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoConEstado("APROBADO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> pagoService.reembolsar(1L));
        assertEquals("No se puede reembolsar el pago de una orden ya entregada", ex.getMessage());
    }

    @Test
    void testReembolsarCasoFeliz() {
        orden.setEstado(EstadoOrden.CANCELADA);
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoConEstado("APROBADO")));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));
        PagoResponse response = pagoService.reembolsar(1L);
        assertEquals("REEMBOLSADO", response.getEstado());
    }
}
