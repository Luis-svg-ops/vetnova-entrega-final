package cl.vetnova.reportes.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cl.vetnova.reportes.exception.BusinessRuleException;
import cl.vetnova.reportes.model.MonitorSistema;
import cl.vetnova.reportes.repository.MonitorSistemaRepository;

public class MonitorSistemaServiceTest {

    @Mock private MonitorSistemaRepository monitorRepository;
    @InjectMocks private MonitorSistemaService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private MonitorSistema req(String micro, String estado) {
        MonitorSistema m = new MonitorSistema();
        m.setMicroservicio(micro);
        m.setEstado(estado);
        return m;
    }

    @Test
    void testRegistrarMicroservicioNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req(null, "UP")));
        assertEquals("El microservicio es obligatorio", ex.getMessage());
    }

    @Test
    void testRegistrarMicroservicioInvalido() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req("MS99", "UP")));
        assertEquals("Microservicio no válido. Debe ser uno de los 12 microservicios del sistema", ex.getMessage());
    }

    @Test
    void testRegistrarEstadoNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req("MS1", null)));
        assertEquals("El estado es obligatorio", ex.getMessage());
    }

    @Test
    void testRegistrarEstadoInvalido() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(req("MS1", "OFFLINE")));
        assertEquals("Estado no válido. Valores permitidos: UP, DEGRADED, DOWN", ex.getMessage());
    }

    @Test
    void testRegistrarLatenciaNegativa() {
        MonitorSistema m = req("MS1", "UP");
        m.setLatenciaMs(-10);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(m));
        assertEquals("La latencia no puede ser negativa", ex.getMessage());
    }

    @Test
    void testRegistrarCpuFueraDeRango() {
        MonitorSistema m = req("MS1", "UP");
        m.setUsoCpu(101.0);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(m));
        assertEquals("El uso de CPU debe estar entre 0 y 100", ex.getMessage());
    }

    @Test
    void testRegistrarCpuNegativo() {
        MonitorSistema m = req("MS1", "UP");
        m.setUsoCpu(-1.0);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(m));
        assertEquals("El uso de CPU debe estar entre 0 y 100", ex.getMessage());
    }

    @Test
    void testRegistrarMemoriaNegativa() {
        MonitorSistema m = req("MS1", "UP");
        m.setUsoMemoria(-5.0);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(m));
        assertEquals("El uso de memoria debe estar entre 0 y 100", ex.getMessage());
    }

    @Test
    void testRegistrarMemoriaFueraDeRango() {
        MonitorSistema m = req("MS1", "UP");
        m.setUsoMemoria(101.0);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.registrar(m));
        assertEquals("El uso de memoria debe estar entre 0 y 100", ex.getMessage());
    }

    @Test
    void testRegistrarCasoFeliz() {
        when(monitorRepository.save(any(MonitorSistema.class))).thenAnswer(inv -> inv.getArgument(0));
        MonitorSistema m = req("MS1", "UP");
        m.setLatenciaMs(0);
        m.setUsoCpu(45.0);
        m.setUsoMemoria(60.0);
        MonitorSistema guardado = service.registrar(m);
        assertNotNull(guardado.getUltimoChequeo());
    }

    @Test
    void testRegistrarCasoFelizSinMetricas() {
        when(monitorRepository.save(any(MonitorSistema.class))).thenAnswer(inv -> inv.getArgument(0));
        MonitorSistema guardado = service.registrar(req("MS1", "UP"));
        assertNotNull(guardado.getUltimoChequeo());
    }

    @Test
    void testHistorial() {
        when(monitorRepository.findByMicroservicioOrderByUltimoChequeoDesc("MS1")).thenReturn(List.of(new MonitorSistema()));
        assertEquals(1, service.historial("MS1").size());
    }
}
