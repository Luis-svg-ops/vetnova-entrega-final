package cl.vetnova.notificaciones.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Map;
import org.springframework.web.client.RestTemplate;

import cl.vetnova.notificaciones.exception.BusinessRuleException;
import cl.vetnova.notificaciones.exception.ResourceNotFoundException;
import cl.vetnova.notificaciones.model.CanalNotificacion;
import cl.vetnova.notificaciones.model.Notificacion;
import cl.vetnova.notificaciones.repository.CanalNotificacionRepository;
import cl.vetnova.notificaciones.repository.NotificacionRepository;

public class NotificacionServiceTest {

    @Mock private NotificacionRepository notificacionRepository;
    @Mock private CanalNotificacionRepository canalRepository;
    @InjectMocks private NotificacionService service;
    @Mock private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Notificacion req(Long usuarioId, String tipo, String mensaje) {
        Notificacion n = new Notificacion();
        n.setUsuarioId(usuarioId);
        n.setTipo(tipo);
        n.setMensaje(mensaje);
        return n;
    }

    private CanalNotificacion canal(boolean activo) {
        CanalNotificacion c = new CanalNotificacion();
        c.setActivo(activo);
        return c;
    }

    private Notificacion notif(Long id, Boolean leida) {
        Notificacion n = new Notificacion();
        n.setId(id);
        n.setLeida(leida);
        return n;
    }

    @Test
    void testCrearUsuarioIdNull() {
       
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(null, "EMAIL", "m")));
        assertEquals("El usuarioId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearTipoNull() {
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("existe", true));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, null, "m")));
        assertEquals("El tipo de notificación es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearTipoInvalido() {
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("existe", true));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, "FAX", "m")));
        assertEquals("Tipo no válido. Valores permitidos: EMAIL, SMS, PUSH, SISTEMA", ex.getMessage());
    }

    @Test
    void testCrearMensajeNull() {
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("existe", true));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, "EMAIL", null)));
        assertEquals("El mensaje es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearMensajeVacio() {
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("existe", true));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, "EMAIL", "  ")));
        assertEquals("El mensaje no puede estar vacío", ex.getMessage());
    }

    @Test
    void testCrearCanalNoConfigurado() {
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("existe", true));
        when(canalRepository.findByUsuarioIdAndTipo(1L, "SMS")).thenReturn(Optional.empty());
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, "SMS", "m")));
        assertEquals("El usuario no tiene configurado el canal SMS", ex.getMessage());
    }

    @Test
    void testCrearCanalInactivo() {
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("existe", true));
        when(canalRepository.findByUsuarioIdAndTipo(1L, "EMAIL")).thenReturn(Optional.of(canal(false)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, "EMAIL", "m")));
        assertEquals("El canal EMAIL del usuario está inactivo", ex.getMessage());
    }

    @Test
    void testCrearCasoFeliz() {
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("existe", true)); 
        when(canalRepository.findByUsuarioIdAndTipo(1L, "EMAIL")).thenReturn(Optional.of(canal(true)));
        when(notificacionRepository.save(any(Notificacion.class))).thenAnswer(inv -> inv.getArgument(0));
        Notificacion n = service.crear(req(1L, "EMAIL", "Su cita fue confirmada"));
        assertFalse(n.getLeida());
        assertEquals("ENVIADO", n.getEstado());
        assertNotNull(n.getFechaEnvio());
    }

    @Test
    void testMarcarLeidaInexistente() {
        when(notificacionRepository.findById(99L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.marcarLeida(99L));
        assertEquals("Notificación no encontrada", ex.getMessage());
    }

    @Test
    void testMarcarLeidaYaLeida() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notif(1L, true)));
        Notificacion n = service.marcarLeida(1L);
        assertTrue(n.getLeida());
        verify(notificacionRepository, never()).save(any());
    }

    @Test
    void testMarcarLeidaCasoFeliz() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notif(1L, false)));
        when(notificacionRepository.save(any(Notificacion.class))).thenAnswer(inv -> inv.getArgument(0));
        Notificacion n = service.marcarLeida(1L);
        assertTrue(n.getLeida());
        assertNotNull(n.getFechaLectura());
    }

    @Test
    void testReenviar() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notif(1L, false)));
        when(notificacionRepository.save(any(Notificacion.class))).thenAnswer(inv -> inv.getArgument(0));
        Notificacion n = service.reenviar(1L);
        assertEquals("ENVIADO", n.getEstado());
        assertNotNull(n.getFechaEnvio());
    }

    @Test
    void testListarSinFiltroLeida() {
        when(notificacionRepository.findByUsuarioIdOrderByFechaEnvioDesc(1L)).thenReturn(List.of(new Notificacion()));
        assertEquals(1, service.listar(1L, null).size());
    }

    @Test
    void testListarPorLeida() {
        when(notificacionRepository.findByUsuarioIdAndLeidaOrderByFechaEnvioDesc(1L, false)).thenReturn(List.of(new Notificacion()));
        assertEquals(1, service.listar(1L, false).size());
    }

    @Test
    void testContarNoLeidas() {
        when(notificacionRepository.countByUsuarioIdAndLeidaFalse(1L)).thenReturn(4L);
        assertEquals(4L, service.contarNoLeidas(1L));
    }
}
