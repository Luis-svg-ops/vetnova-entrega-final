package cl.vetnova.reportes.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cl.vetnova.reportes.client.AuthClient;
import cl.vetnova.reportes.dto.ReporteRequest;
import cl.vetnova.reportes.exception.BusinessRuleException;
import cl.vetnova.reportes.exception.ResourceNotFoundException;
import cl.vetnova.reportes.model.Reporte;
import cl.vetnova.reportes.repository.ReporteRepository;

public class ReporteServiceTest {

    @Mock private ReporteRepository reporteRepository;
    @InjectMocks private ReporteService service;

    @Mock private AuthClient authClient; 


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private ReporteRequest req(String tipo, Long sucursal, LocalDate desde, LocalDate hasta, Long generadoPor) {
        ReporteRequest r = new ReporteRequest();
        r.setTipo(tipo);
        r.setSucursal(sucursal);
        r.setDesde(desde);
        r.setHasta(hasta);
        r.setGeneradoPor(generadoPor);
        return r;
    }

    private final LocalDate d1 = LocalDate.of(2025, 1, 1);
    private final LocalDate d2 = LocalDate.of(2025, 6, 1);

    @Test
    void testGenerarTipoNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.generar(req(null, 1L, d1, d2, 1L)));
        
        assertEquals("El tipo de reporte es obligatorio", ex.getMessage());
        
    }

    @Test
    void testGenerarTipoInvalido() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.generar(req("FINANCIERO", 1L, d1, d2, 1L)));
        assertEquals("Tipo no válido. Valores permitidos: ATENCION, VENTA, STOCK", ex.getMessage());
    }

    @Test
    void testGenerarSucursalNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.generar(req("VENTA", null, d1, d2, 1L)));
        assertEquals("La sucursal es obligatoria", ex.getMessage());
    }

    @Test
    void testGenerarDesdeNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.generar(req("VENTA", 1L, null, d2, 1L)));
        assertEquals("La fecha de inicio es obligatoria", ex.getMessage());
    }

    @Test
    void testGenerarHastaNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.generar(req("VENTA", 1L, d1, null, 1L)));
        assertEquals("La fecha de fin es obligatoria", ex.getMessage());
    }

    @Test
    void testGenerarDesdePosteriorAHasta() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.generar(req("VENTA", 1L, d2, d1, 1L)));
        assertEquals("La fecha de inicio no puede ser posterior a la fecha de fin", ex.getMessage());
    }

    @Test
    void testGenerarGeneradoPorNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.generar(req("VENTA", 1L, d1, d2, null)));
        assertEquals("El generadoPor es obligatorio", ex.getMessage());
    }

    @Test
    void testGenerarCasoFeliz() {
        when(authClient.usuarioExiste(1L)).thenReturn(true);
        when(reporteRepository.save(any(Reporte.class))).thenAnswer(inv -> inv.getArgument(0));
        Reporte r = service.generar(req("VENTA", 1L, d1, d2, 1L));
        
        assertEquals("GENERADO", r.getEstado());
        assertNotNull(r.getGeneradoEn());
    }

    @Test
    void testGenerarMismoDia() {
        when(authClient.usuarioExiste(1L)).thenReturn(true);
        when(reporteRepository.save(any(Reporte.class))).thenAnswer(inv -> inv.getArgument(0));
        Reporte r = service.generar(req("VENTA", 1L, d1, d1, 1L));
        assertEquals("GENERADO", r.getEstado());
    }

    @Test
    void testExportarInexistente() {
        when(reporteRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.exportar(99L, "PDF"));
    }

    @Test
    void testExportarFormatoInvalido() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(new Reporte()));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.exportar(1L, "CSV"));
        assertEquals("Formato no válido. Valores permitidos: PDF, EXCEL", ex.getMessage());
    }

    @Test
    void testExportarFormatoNull() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(new Reporte()));
        assertThrows(BusinessRuleException.class, () -> service.exportar(1L, null));
    }

    @Test
    void testExportarFormatoPdf() {
        Reporte r = new Reporte();
        r.setId(1L);
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(r));
        assertEquals(1L, service.exportar(1L, "PDF").getId());
    }

    @Test
    void testExportarFormatoExcel() {
        Reporte r = new Reporte();
        r.setId(1L);
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(r));
        assertEquals(1L, service.exportar(1L, "EXCEL").getId());
    }

    @Test
    void testFiltrarPorSucursal() {
        when(reporteRepository.findBySucursal(1L)).thenReturn(List.of(new Reporte()));
        assertEquals(1, service.filtrarPorSucursal(1L).size());
    }

    @Test
    void testListar() {
        when(reporteRepository.findAll()).thenReturn(List.of(new Reporte()));
        assertEquals(1, service.listar().size());
    }
}
