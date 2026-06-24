package cl.vetnova.notificaciones.service;

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

import cl.vetnova.notificaciones.exception.BusinessRuleException;
import cl.vetnova.notificaciones.exception.ConflictException;
import cl.vetnova.notificaciones.exception.ResourceNotFoundException;
import cl.vetnova.notificaciones.model.ConfiguracionAlerta;
import cl.vetnova.notificaciones.repository.CanalNotificacionRepository;
import cl.vetnova.notificaciones.repository.ConfiguracionAlertaRepository;

public class ConfiguracionAlertaServiceTest {

    @Mock private ConfiguracionAlertaRepository configuracionRepository;
    @Mock private CanalNotificacionRepository canalRepository;
    @InjectMocks private ConfiguracionAlertaService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private ConfiguracionAlerta req(Long usuarioId, String tipoEvento, String canal) {
        ConfiguracionAlerta c = new ConfiguracionAlerta();
        c.setUsuarioId(usuarioId);
        c.setTipoEvento(tipoEvento);
        c.setCanal(canal);
        return c;
    }

    private ConfiguracionAlerta config(Long id, Boolean activa) {
        ConfiguracionAlerta c = new ConfiguracionAlerta();
        c.setId(id);
        c.setActiva(activa);
        return c;
    }

    @Test
    void testCrearUsuarioIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(null, "STOCK_CRITICO", "EMAIL")));
        assertEquals("El usuarioId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearTipoEventoNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, null, "EMAIL")));
        assertEquals("El tipo de evento es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearTipoEventoInvalido() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, "HACKEO", "EMAIL")));
        assertTrue(ex.getMessage().startsWith("Tipo de evento no válido. Valores permitidos: STOCK_CRITICO"));
    }

    @Test
    void testCrearConfiguracionDuplicada() {
        when(configuracionRepository.existsByUsuarioIdAndTipoEventoAndCanal(1L, "STOCK_CRITICO", "EMAIL")).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> service.crear(req(1L, "STOCK_CRITICO", "EMAIL")));
        assertEquals("Ya existe una configuración de alerta para ese evento y canal", ex.getMessage());
    }

    @Test
    void testCrearCanalNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, "STOCK_CRITICO", null)));
        assertEquals("El canal es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearCanalInvalido() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, "STOCK_CRITICO", "FAX")));
        assertEquals("Canal no válido. Valores permitidos: EMAIL, SMS, PUSH, SISTEMA", ex.getMessage());
    }

    @Test
    void testCrearCanalNoConfigurado() {
        when(canalRepository.existsByUsuarioIdAndTipo(1L, "SMS")).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, "STOCK_CRITICO", "SMS")));
        assertEquals("El usuario no tiene configurado el canal SMS", ex.getMessage());
    }

    @Test
    void testCrearCasoFeliz() {
        when(canalRepository.existsByUsuarioIdAndTipo(1L, "EMAIL")).thenReturn(true);
        when(configuracionRepository.save(any(ConfiguracionAlerta.class))).thenAnswer(inv -> inv.getArgument(0));
        ConfiguracionAlerta c = service.crear(req(1L, "CITA_CONFIRMADA", "EMAIL"));
        assertTrue(c.getActiva());
    }

    @Test
    void testDesactivarInexistente() {
        when(configuracionRepository.findById(99L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.desactivar(99L));
        assertEquals("Configuración de alerta no encontrada", ex.getMessage());
    }

    @Test
    void testDesactivarYaInactiva() {
        when(configuracionRepository.findById(1L)).thenReturn(Optional.of(config(1L, false)));
        ConfiguracionAlerta c = service.desactivar(1L);
        assertFalse(c.getActiva());
        verify(configuracionRepository, never()).save(any());
    }

    @Test
    void testDesactivarCasoFeliz() {
        when(configuracionRepository.findById(1L)).thenReturn(Optional.of(config(1L, true)));
        when(configuracionRepository.save(any(ConfiguracionAlerta.class))).thenAnswer(inv -> inv.getArgument(0));
        ConfiguracionAlerta c = service.desactivar(1L);
        assertFalse(c.getActiva());
    }

    @Test
    void testActivar() {
        when(configuracionRepository.findById(1L)).thenReturn(Optional.of(config(1L, false)));
        when(configuracionRepository.save(any(ConfiguracionAlerta.class))).thenAnswer(inv -> inv.getArgument(0));
        ConfiguracionAlerta c = service.activar(1L);
        assertTrue(c.getActiva());
    }

    @Test
    void testEliminar() {
        ConfiguracionAlerta c = config(1L, true);
        when(configuracionRepository.findById(1L)).thenReturn(Optional.of(c));
        service.eliminar(1L);
        verify(configuracionRepository).delete(c);
    }

    @Test
    void testListar() {
        when(configuracionRepository.findByUsuarioId(1L)).thenReturn(List.of(new ConfiguracionAlerta()));
        assertEquals(1, service.listar(1L).size());
    }
}
