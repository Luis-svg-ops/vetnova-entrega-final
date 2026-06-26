package cl.vetnova.facturacion.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cl.vetnova.facturacion.dto.ReporteRequest;
import cl.vetnova.facturacion.exception.BusinessRuleException;
import cl.vetnova.facturacion.exception.ConflictException;
import cl.vetnova.facturacion.exception.ResourceNotFoundException;
import cl.vetnova.facturacion.model.DocumentoTributario;
import cl.vetnova.facturacion.model.ReporteTributario;
import cl.vetnova.facturacion.repository.DocumentoTributarioRepository;
import cl.vetnova.facturacion.repository.ReporteTributarioRepository;

public class ReporteTributarioServiceTest {

    @Mock private ReporteTributarioRepository reporteRepository;
    @Mock private DocumentoTributarioRepository documentoRepository;
    @InjectMocks private ReporteTributarioService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private ReporteRequest req(String sucursal, String periodo) {
        ReporteRequest r = new ReporteRequest();
        r.setSucursal(sucursal);
        r.setPeriodo(periodo);
        return r;
    }

    private DocumentoTributario doc(String estado, LocalDateTime fecha, Double neto) {
        DocumentoTributario d = new DocumentoTributario();
        d.setEstadoSII(estado);
        d.setFechaEmision(fecha);
        d.setNeto(neto);
        return d;
    }

    @Test
    void testGenerarSucursalNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.generar(req(null, "2025-06")));
        assertEquals("La sucursal es obligatoria", ex.getMessage());
    }

    @Test
    void testGenerarPeriodoNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.generar(req("CHILLAN", null)));
        assertEquals("El período es obligatorio", ex.getMessage());
    }

    @Test
    void testGenerarPeriodoFormatoInvalido() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.generar(req("CHILLAN", "junio-2025")));
        assertEquals("El período debe tener formato YYYY-MM", ex.getMessage());
    }

    @Test
    void testGenerarPeriodoFuturo() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.generar(req("CHILLAN", "2099-12")));
        assertEquals("No se puede generar reporte para un período futuro", ex.getMessage());
    }

    @Test
    void testGenerarReporteYaExiste() {
        when(reporteRepository.existsBySucursalAndPeriodo("CHILLAN", "2025-06")).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> service.generar(req("CHILLAN", "2025-06")));
        assertEquals("Ya existe un reporte tributario para ese período y sucursal", ex.getMessage());
    }

    @Test
    void testGenerarCasoFelizExcluyeAnuladosYOtrosPeriodos() {
        when(reporteRepository.existsBySucursalAndPeriodo("CHILLAN", "2025-06")).thenReturn(false);
        List<DocumentoTributario> docs = List.of(
                doc("ANULADO", LocalDateTime.of(2025, 6, 15, 10, 0), 1000.0),
                doc("EMITIDO", null, 999.0),
                doc("EMITIDO", LocalDateTime.of(2025, 5, 10, 10, 0), 2000.0),
                doc("EMITIDO", LocalDateTime.of(2025, 6, 12, 10, 0), null),
                doc("EMITIDO", LocalDateTime.of(2025, 6, 20, 10, 0), 3500.0));
        when(documentoRepository.findBySucursal("CHILLAN")).thenReturn(docs);
        when(reporteRepository.save(any(ReporteTributario.class))).thenAnswer(inv -> inv.getArgument(0));
        ReporteTributario reporte = service.generar(req("CHILLAN", "2025-06"));
        assertEquals(2, reporte.getTotalDocumentos());
        assertEquals(3500.0, reporte.getMontoNeto());
        assertEquals(665.0, reporte.getMontoIva());
        assertEquals(4165.0, reporte.getMontoTotal());
    }

    @Test
    void testExportarInexistente() {
        when(reporteRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.exportar(99L, "PDF"));
    }

    @Test
    void testExportarFormatoInvalido() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(new ReporteTributario()));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.exportar(1L, "CSV"));
        assertEquals("Formato no válido. Valores permitidos: PDF, EXCEL", ex.getMessage());
    }

    @Test
    void testExportarFormatoNull() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(new ReporteTributario()));
        assertThrows(BusinessRuleException.class, () -> service.exportar(1L, null));
    }

    @Test
    void testExportarFormatoPdf() {
        ReporteTributario reporte = new ReporteTributario();
        reporte.setId(1L);
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporte));
        assertEquals(1L, service.exportar(1L, "PDF").getId());
    }

    @Test
    void testExportarFormatoExcel() {
        ReporteTributario reporte = new ReporteTributario();
        reporte.setId(1L);
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporte));
        assertEquals(1L, service.exportar(1L, "EXCEL").getId());
    }

    @Test
    void testListar() {
        when(reporteRepository.findAll()).thenReturn(List.of(new ReporteTributario()));
        assertEquals(1, service.listar().size());
    }
}
