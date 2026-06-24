package cl.vetnova.reportes.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cl.vetnova.reportes.exception.BusinessRuleException;
import cl.vetnova.reportes.exception.ConflictException;
import cl.vetnova.reportes.exception.ResourceNotFoundException;
import cl.vetnova.reportes.model.Reporte;
import cl.vetnova.reportes.model.ReporteVenta;
import cl.vetnova.reportes.repository.ReporteRepository;
import cl.vetnova.reportes.repository.ReporteVentaRepository;

public class ReporteVentaServiceTest {

    @Mock private ReporteVentaRepository ventaRepository;
    @Mock private ReporteRepository reporteRepository;
    @InjectMocks private ReporteVentaService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private ReporteVenta req(Long reporteId, Double montoTotal, Map<String, Double> porProducto) {
        ReporteVenta r = new ReporteVenta();
        r.setReporteId(reporteId);
        r.setMontoTotal(montoTotal);
        r.setVentaPorProducto(porProducto);
        return r;
    }

    private Reporte reporte(String tipo) {
        Reporte r = new Reporte();
        r.setId(1L);
        r.setTipo(tipo);
        return r;
    }

    private void ventaValida() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporte("VENTA")));
        when(ventaRepository.existsByReporteId(1L)).thenReturn(false);
    }

    @Test
    void testCrearReporteIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(null, 0.0, null)));
        assertEquals("El reporteId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearReporteInexistente() {
        when(reporteRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.crear(req(999L, 0.0, null)));
    }

    @Test
    void testCrearTipoDistinto() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporte("STOCK")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, 0.0, null)));
        assertEquals("El reporte debe ser de tipo VENTA", ex.getMessage());
    }

    @Test
    void testCrearYaTieneDetalle() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporte("VENTA")));
        when(ventaRepository.existsByReporteId(1L)).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> service.crear(req(1L, 0.0, null)));
        assertEquals("El reporte ya tiene un detalle de venta asociado", ex.getMessage());
    }

    @Test
    void testCrearMontoNegativo() {
        ventaValida();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, -500.0, null)));
        assertEquals("El monto total no puede ser negativo", ex.getMessage());
    }

    @Test
    void testCrearMontoInconsistente() {
        ventaValida();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> service.crear(req(1L, 9000.0, Map.of("10", 2000.0, "20", 2500.0))));
        assertEquals("El monto total no coincide con la suma de ventas del período", ex.getMessage());
    }

    @Test
    void testCrearCasoFelizSinMapa() {
        ventaValida();
        when(ventaRepository.save(any(ReporteVenta.class))).thenAnswer(inv -> inv.getArgument(0));
        ReporteVenta r = service.crear(req(1L, null, null));
        assertEquals(0.0, r.getMontoTotal());
        assertEquals(0, r.getTotalOrdenes());
        assertEquals(0, r.getProductosVendidos());
    }

    @Test
    void testCrearCasoFelizMapaVacio() {
        ventaValida();
        when(ventaRepository.save(any(ReporteVenta.class))).thenAnswer(inv -> inv.getArgument(0));
        ReporteVenta r = service.crear(req(1L, 100.0, Map.of()));
        assertEquals(100.0, r.getMontoTotal());
    }

    @Test
    void testCrearCasoFelizConContadoresProvistos() {
        ventaValida();
        when(ventaRepository.save(any(ReporteVenta.class))).thenAnswer(inv -> inv.getArgument(0));
        ReporteVenta r = req(1L, 100.0, null);
        r.setTotalOrdenes(7);
        r.setProductosVendidos(10);
        ReporteVenta guardado = service.crear(r);
        assertEquals(7, guardado.getTotalOrdenes());
        assertEquals(10, guardado.getProductosVendidos());
    }

    @Test
    void testCrearCasoFelizMapaCoincidente() {
        ventaValida();
        when(ventaRepository.save(any(ReporteVenta.class))).thenAnswer(inv -> inv.getArgument(0));
        ReporteVenta r = service.crear(req(1L, 4500.0, Map.of("10", 2000.0, "20", 2500.0)));
        assertEquals(4500.0, r.getMontoTotal());
    }
}
