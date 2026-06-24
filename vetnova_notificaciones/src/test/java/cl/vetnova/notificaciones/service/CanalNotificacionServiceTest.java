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
import cl.vetnova.notificaciones.model.CanalNotificacion;
import cl.vetnova.notificaciones.repository.CanalNotificacionRepository;
import cl.vetnova.notificaciones.repository.HistorialMensajeRepository;

public class CanalNotificacionServiceTest {

    @Mock private CanalNotificacionRepository canalRepository;
    @Mock private HistorialMensajeRepository historialRepository;
    @InjectMocks private CanalNotificacionService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private CanalNotificacion req(Long usuarioId, String tipo, String destino) {
        CanalNotificacion c = new CanalNotificacion();
        c.setUsuarioId(usuarioId);
        c.setTipo(tipo);
        c.setDestino(destino);
        return c;
    }

    private CanalNotificacion canal(Long id, Boolean activo) {
        CanalNotificacion c = new CanalNotificacion();
        c.setId(id);
        c.setActivo(activo);
        return c;
    }

    @Test
    void testCrearUsuarioIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(null, "EMAIL", "a@b.com")));
        assertEquals("El usuarioId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearTipoNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, null, "a@b.com")));
        assertEquals("El tipo de canal es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearTipoInvalido() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, "FAX", "a@b.com")));
        assertEquals("Tipo no válido. Valores permitidos: EMAIL, SMS, PUSH, SISTEMA", ex.getMessage());
    }

    @Test
    void testCrearDuplicado() {
        when(canalRepository.existsByUsuarioIdAndTipo(1L, "EMAIL")).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> service.crear(req(1L, "EMAIL", "a@b.com")));
        assertEquals("El usuario ya tiene configurado un canal de tipo EMAIL", ex.getMessage());
    }

    @Test
    void testCrearDestinoNull() {
        when(canalRepository.existsByUsuarioIdAndTipo(1L, "EMAIL")).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, "EMAIL", null)));
        assertEquals("El destino es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearEmailFormatoInvalido() {
        when(canalRepository.existsByUsuarioIdAndTipo(1L, "EMAIL")).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, "EMAIL", "juanmail.com")));
        assertEquals("El destino no tiene formato de email válido", ex.getMessage());
    }

    @Test
    void testCrearSmsFormatoInvalido() {
        when(canalRepository.existsByUsuarioIdAndTipo(1L, "SMS")).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.crear(req(1L, "SMS", "noestelefono")));
        assertEquals("El destino no tiene formato de teléfono válido", ex.getMessage());
    }

    @Test
    void testCrearCasoFelizEmail() {
        when(canalRepository.existsByUsuarioIdAndTipo(1L, "EMAIL")).thenReturn(false);
        when(canalRepository.save(any(CanalNotificacion.class))).thenAnswer(inv -> inv.getArgument(0));
        CanalNotificacion c = service.crear(req(1L, "EMAIL", "juan@mail.com"));
        assertTrue(c.getActivo());
    }

    @Test
    void testCrearCasoFelizSms() {
        when(canalRepository.existsByUsuarioIdAndTipo(1L, "SMS")).thenReturn(false);
        when(canalRepository.save(any(CanalNotificacion.class))).thenAnswer(inv -> inv.getArgument(0));
        CanalNotificacion c = service.crear(req(1L, "SMS", "+56912345678"));
        assertEquals("+56912345678", c.getDestino());
    }

    @Test
    void testActualizarInexistente() {
        when(canalRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.actualizar(99L, req(1L, "EMAIL", "x@y.com")));
    }

    @Test
    void testActualizarCasoFeliz() {
        when(canalRepository.findById(1L)).thenReturn(Optional.of(canal(1L, true)));
        when(canalRepository.save(any(CanalNotificacion.class))).thenAnswer(inv -> inv.getArgument(0));
        CanalNotificacion req = new CanalNotificacion();
        req.setDestino("nuevo@mail.com");
        CanalNotificacion c = service.actualizar(1L, req);
        assertEquals("nuevo@mail.com", c.getDestino());
    }

    @Test
    void testActualizarSinDestino() {
        when(canalRepository.findById(1L)).thenReturn(Optional.of(canal(1L, true)));
        when(canalRepository.save(any(CanalNotificacion.class))).thenAnswer(inv -> inv.getArgument(0));
        CanalNotificacion c = service.actualizar(1L, new CanalNotificacion());
        assertEquals(1L, c.getId());
    }

    @Test
    void testDesactivarYaInactivo() {
        when(canalRepository.findById(1L)).thenReturn(Optional.of(canal(1L, false)));
        CanalNotificacion c = service.desactivar(1L);
        assertFalse(c.getActivo());
        verify(canalRepository, never()).save(any());
    }

    @Test
    void testDesactivarCasoFeliz() {
        when(canalRepository.findById(1L)).thenReturn(Optional.of(canal(1L, true)));
        when(canalRepository.save(any(CanalNotificacion.class))).thenAnswer(inv -> inv.getArgument(0));
        CanalNotificacion c = service.desactivar(1L);
        assertFalse(c.getActivo());
    }

    @Test
    void testEliminarConHistorial() {
        when(canalRepository.findById(1L)).thenReturn(Optional.of(canal(1L, true)));
        when(historialRepository.existsByCanalId(1L)).thenReturn(true);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.eliminar(1L));
        assertEquals("No se puede eliminar un canal con historial de mensajes", ex.getMessage());
    }

    @Test
    void testEliminarSinHistorial() {
        CanalNotificacion c = canal(1L, true);
        when(canalRepository.findById(1L)).thenReturn(Optional.of(c));
        when(historialRepository.existsByCanalId(1L)).thenReturn(false);
        service.eliminar(1L);
        verify(canalRepository).delete(c);
    }

    @Test
    void testListar() {
        when(canalRepository.findByUsuarioId(1L)).thenReturn(List.of(new CanalNotificacion()));
        assertEquals(1, service.listar(1L).size());
    }
}
