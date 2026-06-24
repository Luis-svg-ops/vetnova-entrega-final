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
import cl.vetnova.reportes.model.ReporteAtencion;
import cl.vetnova.reportes.repository.ReporteAtencionRepository;
import cl.vetnova.reportes.repository.ReporteRepository;

public class ReporteAtencionServiceTest {

    @Mock private ReporteAtencionRepository atencionRepository;
    @Mock private ReporteRepository reporteRepository;
    @InjectMocks private ReporteAtencionService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private ReporteAtencion req(Long reporteId, Integer realizadas, Integer canceladas, Integer ausentes, Integer total) {
        ReporteAtencion r = new ReporteAtencion();
        r.setReporteId(reporteId);
        r.setCitasRealizadas(realizadas);
        r.setCitasCanceladas(canceladas);
        r.setCitasAusentes(ausentes);
        r.setTotalCitas(total);
        return r;
    }

    private Reporte reporte(String tipo) {
        Reporte r = new Reporte();
        r.setId(1L);
        r.setTipo(tipo);
        return r;
    }

    @Test
    void testCrearReporteIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(null, 1, 1, 1, null)));
        assertEquals("El reporteId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearReporteInexistente() {
        when(reporteRepository.findById(999L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.crear(req(999L, 1, 1, 1, null)));
        assertEquals("Reporte no encontrado", ex.getMessage());
    }

    @Test
    void testCrearTipoDistinto() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporte("VENTA")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, 1, 1, 1, null)));
        assertEquals("El reporte debe ser de tipo ATENCION", ex.getMessage());
    }

    @Test
    void testCrearYaTieneDetalle() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporte("ATENCION")));
        when(atencionRepository.existsByReporteId(1L)).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> service.crear(req(1L, 1, 1, 1, null)));
        assertEquals("El reporte ya tiene un detalle de atención asociado", ex.getMessage());
    }

    @Test
    void testCrearConteoNegativo() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporte("ATENCION")));
        when(atencionRepository.existsByReporteId(1L)).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, -1, 1, 1, null)));
        assertEquals("Los conteos no pueden ser negativos", ex.getMessage());
    }

    @Test
    void testCrearCanceladasNegativo() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporte("ATENCION")));
        when(atencionRepository.existsByReporteId(1L)).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, 1, -1, 1, null)));
        assertEquals("Los conteos no pueden ser negativos", ex.getMessage());
    }

    @Test
    void testCrearAusentesNegativo() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporte("ATENCION")));
        when(atencionRepository.existsByReporteId(1L)).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, 1, 1, -1, null)));
        assertEquals("Los conteos no pueden ser negativos", ex.getMessage());
    }

    @Test
    void testCrearCasoFelizConteosNull() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporte("ATENCION")));
        when(atencionRepository.existsByReporteId(1L)).thenReturn(false);
        when(atencionRepository.save(any(ReporteAtencion.class))).thenAnswer(inv -> inv.getArgument(0));
        ReporteAtencion r = service.crear(req(1L, null, null, null, null));
        assertEquals(0, r.getTotalCitas());
    }

    @Test
    void testCrearSubtotalesInconsistentes() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporte("ATENCION")));
        when(atencionRepository.existsByReporteId(1L)).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, 10, 5, 3, 20)));
        assertEquals("La suma de subtotales no coincide con el total de citas", ex.getMessage());
    }

    @Test
    void testCrearCasoFelizSinTotal() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporte("ATENCION")));
        when(atencionRepository.existsByReporteId(1L)).thenReturn(false);
        when(atencionRepository.save(any(ReporteAtencion.class))).thenAnswer(inv -> inv.getArgument(0));
        ReporteAtencion r = service.crear(req(1L, 12, 5, 3, null));
        assertEquals(20, r.getTotalCitas());
    }

    @Test
    void testCrearCasoFelizConTotalCoincidente() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporte("ATENCION")));
        when(atencionRepository.existsByReporteId(1L)).thenReturn(false);
        when(atencionRepository.save(any(ReporteAtencion.class))).thenAnswer(inv -> inv.getArgument(0));
        ReporteAtencion r = service.crear(req(1L, 12, 5, 3, 20));
        assertEquals(20, r.getTotalCitas());
    }
}
