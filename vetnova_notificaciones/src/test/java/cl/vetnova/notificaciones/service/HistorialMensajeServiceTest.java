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
import cl.vetnova.notificaciones.exception.ResourceNotFoundException;
import cl.vetnova.notificaciones.model.CanalNotificacion;
import cl.vetnova.notificaciones.model.HistorialMensaje;
import cl.vetnova.notificaciones.model.Notificacion;
import cl.vetnova.notificaciones.repository.CanalNotificacionRepository;
import cl.vetnova.notificaciones.repository.HistorialMensajeRepository;
import cl.vetnova.notificaciones.repository.NotificacionRepository;

public class HistorialMensajeServiceTest {

    @Mock private HistorialMensajeRepository historialRepository;
    @Mock private NotificacionRepository notificacionRepository;
    @Mock private CanalNotificacionRepository canalRepository;
    @InjectMocks private HistorialMensajeService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private HistorialMensaje req(Long notifId, Long canalId, String estado) {
        HistorialMensaje h = new HistorialMensaje();
        h.setNotificacionId(notifId);
        h.setCanalId(canalId);
        h.setEstado(estado);
        return h;
    }

    private Notificacion notif(Long usuarioId) {
        Notificacion n = new Notificacion();
        n.setId(1L);
        n.setUsuarioId(usuarioId);
        return n;
    }

    private CanalNotificacion canal(Long usuarioId) {
        CanalNotificacion c = new CanalNotificacion();
        c.setId(1L);
        c.setUsuarioId(usuarioId);
        return c;
    }

    @Test
    void testCrearNotificacionIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(null, 1L, "ENVIADO")));
        assertEquals("El notificacionId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearNotificacionInexistente() {
        when(notificacionRepository.findById(999L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.crear(req(999L, 1L, "ENVIADO")));
        assertEquals("Notificación no encontrada", ex.getMessage());
    }

    @Test
    void testCrearCanalIdNull() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notif(1L)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, null, "ENVIADO")));
        assertEquals("El canalId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearCanalInexistente() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notif(1L)));
        when(canalRepository.findById(999L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.crear(req(1L, 999L, "ENVIADO")));
        assertEquals("Canal de notificación no encontrado", ex.getMessage());
    }

    @Test
    void testCrearCanalNoPertenece() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notif(1L)));
        when(canalRepository.findById(5L)).thenReturn(Optional.of(canal(2L)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, 5L, "ENVIADO")));
        assertEquals("El canal no pertenece al usuario de la notificación", ex.getMessage());
    }

    @Test
    void testCrearEstadoNull() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notif(1L)));
        when(canalRepository.findById(1L)).thenReturn(Optional.of(canal(1L)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, 1L, null)));
        assertEquals("El estado es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearEstadoInvalido() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notif(1L)));
        when(canalRepository.findById(1L)).thenReturn(Optional.of(canal(1L)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, 1L, "PENDIENTE")));
        assertEquals("Estado no válido. Valores permitidos: ENVIADO, ENTREGADO, FALLIDO, LEIDO", ex.getMessage());
    }

    @Test
    void testCrearCasoFeliz() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notif(1L)));
        when(canalRepository.findById(1L)).thenReturn(Optional.of(canal(1L)));
        when(historialRepository.save(any(HistorialMensaje.class))).thenAnswer(inv -> inv.getArgument(0));
        HistorialMensaje h = service.crear(req(1L, 1L, "ENVIADO"));
        assertEquals("ENVIADO", h.getEstado());
        assertNotNull(h.getFechaEnvio());
    }

    @Test
    void testListarPorNotificacion() {
        when(historialRepository.findByNotificacionIdOrderByFechaEnvioAsc(1L)).thenReturn(List.of(new HistorialMensaje()));
        assertEquals(1, service.listar(1L, null, null).size());
    }

    @Test
    void testListarPorCanal() {
        when(historialRepository.findByCanalIdOrderByFechaEnvioDesc(1L)).thenReturn(List.of(new HistorialMensaje()));
        assertEquals(1, service.listar(null, 1L, null).size());
    }

    @Test
    void testListarPorEstado() {
        when(historialRepository.findByEstado("FALLIDO")).thenReturn(List.of(new HistorialMensaje()));
        assertEquals(1, service.listar(null, null, "FALLIDO").size());
    }

    @Test
    void testListarTodos() {
        when(historialRepository.findAll()).thenReturn(List.of(new HistorialMensaje()));
        assertEquals(1, service.listar(null, null, null).size());
    }
}
