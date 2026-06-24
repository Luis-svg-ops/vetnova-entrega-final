package cl.vetnova.reportes.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
import cl.vetnova.reportes.model.ReporteStock;
import cl.vetnova.reportes.repository.ReporteRepository;
import cl.vetnova.reportes.repository.ReporteStockRepository;

public class ReporteStockServiceTest {

    @Mock private ReporteStockRepository stockRepository;
    @Mock private ReporteRepository reporteRepository;
    @InjectMocks private ReporteStockService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private ReporteStock req(Long reporteId, Integer critico, Integer transito) {
        ReporteStock r = new ReporteStock();
        r.setReporteId(reporteId);
        r.setProductosConStockCritico(critico);
        r.setProductosEnTransito(transito);
        return r;
    }

    private Reporte reporte(String tipo) {
        Reporte r = new Reporte();
        r.setId(1L);
        r.setTipo(tipo);
        return r;
    }

    private void stockValido() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporte("STOCK")));
        when(stockRepository.existsByReporteId(1L)).thenReturn(false);
    }

    @Test
    void testCrearReporteIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(null, 3, 2)));
        assertEquals("El reporteId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearReporteInexistente() {
        when(reporteRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.crear(req(999L, 3, 2)));
    }

    @Test
    void testCrearTipoDistinto() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporte("ATENCION")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, 3, 2)));
        assertEquals("El reporte debe ser de tipo STOCK", ex.getMessage());
    }

    @Test
    void testCrearYaTieneDetalle() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporte("STOCK")));
        when(stockRepository.existsByReporteId(1L)).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> service.crear(req(1L, 3, 2)));
        assertEquals("El reporte ya tiene un detalle de stock asociado", ex.getMessage());
    }

    @Test
    void testCrearCriticoNegativo() {
        stockValido();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, -1, 2)));
        assertEquals("Los conteos no pueden ser negativos", ex.getMessage());
    }

    @Test
    void testCrearTransitoNegativo() {
        stockValido();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, 3, -1)));
        assertEquals("Los conteos no pueden ser negativos", ex.getMessage());
    }

    @Test
    void testCrearCasoFeliz() {
        stockValido();
        when(stockRepository.save(any(ReporteStock.class))).thenAnswer(inv -> inv.getArgument(0));
        ReporteStock r = service.crear(req(1L, 3, 2));
        assertEquals(3, r.getProductosConStockCritico());
        assertEquals(2, r.getProductosEnTransito());
    }

    @Test
    void testCrearCasoFelizConteosNull() {
        stockValido();
        when(stockRepository.save(any(ReporteStock.class))).thenAnswer(inv -> inv.getArgument(0));
        ReporteStock r = service.crear(req(1L, null, null));
        assertEquals(0, r.getProductosConStockCritico());
        assertEquals(0, r.getProductosEnTransito());
    }
}
