package cl.vetnova.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cl.vetnova.auth.dto.AuditoriaResponse;
import cl.vetnova.auth.dto.CrearAuditoriaRequest;
import cl.vetnova.auth.exception.BusinessRuleException;
import cl.vetnova.auth.exception.ResourceNotFoundException;
import cl.vetnova.auth.model.AuditoriaAcceso;
import cl.vetnova.auth.model.RolPermiso;
import cl.vetnova.auth.model.Usuario;
import cl.vetnova.auth.repository.AuditoriaAccesoRepository;
import cl.vetnova.auth.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AuditoriaServiceTest {

    @Mock
    private AuditoriaAccesoRepository auditoriaAccesoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @InjectMocks
    private AuditoriaService auditoriaService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usuario = new Usuario("Juan", "juan@mail.com", "+569", "hash", new RolPermiso("CLIENTE", "d", Set.of("VER_PERFIL")));
    }

    private CrearAuditoriaRequest request(Long usuarioId, String accion, String ip, Boolean exitoso) {
        return new CrearAuditoriaRequest(usuarioId, accion, ip, exitoso, "detalle");
    }

    private void usuarioExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
    }

    @Test
    void testRegistrarAuditoriaGuardaElAcceso() {
        auditoriaService.registrar(usuario, "LOGIN", "127.0.0.1", true, "Inicio de sesión correcto");
        verify(auditoriaAccesoRepository).save(any());
    }

    @Test
    void testCrearUsuarioIdNullLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> auditoriaService.crear(request(null, "LOGIN", "192.168.1.1", true)));
        assertEquals("El usuarioId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearUsuarioInexistenteLanzaNotFound() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> auditoriaService.crear(request(99L, "LOGIN", "192.168.1.1", true)));
    }

    @Test
    void testCrearAccionNullLanzaBusinessRule() {
        usuarioExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> auditoriaService.crear(request(1L, null, "192.168.1.1", true)));
        assertEquals("La acción es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearAccionInvalidaLanzaBusinessRule() {
        usuarioExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> auditoriaService.crear(request(1L, "HACKEAR", "192.168.1.1", true)));
        assertTrue(ex.getMessage().startsWith("Acción no válida"));
    }

    @Test
    void testCrearExitosoNullLanzaBusinessRule() {
        usuarioExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> auditoriaService.crear(request(1L, "LOGIN", "192.168.1.1", null)));
        assertEquals("El campo exitoso es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearIpNullLanzaBusinessRule() {
        usuarioExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> auditoriaService.crear(request(1L, "LOGIN", null, true)));
        assertEquals("La IP es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearIpInvalidaLanzaBusinessRule() {
        usuarioExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> auditoriaService.crear(request(1L, "LOGIN", "999.999.999.999", true)));
        assertEquals("El formato de IP no es válido", ex.getMessage());
    }

    @Test
    void testCrearCasoFeliz() {
        usuarioExiste();
        when(auditoriaAccesoRepository.save(any(AuditoriaAcceso.class))).thenAnswer(inv -> inv.getArgument(0));

        AuditoriaResponse r = auditoriaService.crear(request(1L, "LOGIN", "192.168.1.1", false));

        assertEquals("LOGIN", r.accion());
        assertFalse(r.exitoso());
    }

    @Test
    void testConsultarPorUsuario() {
        when(auditoriaAccesoRepository.findByUsuarioIdOrderByTimestampDesc(1L)).thenReturn(List.of(new AuditoriaAcceso()));
        assertEquals(1, auditoriaService.consultar(1L, null).size());
    }

    @Test
    void testConsultarPorAccion() {
        when(auditoriaAccesoRepository.findByAccionOrderByTimestampDesc("LOGIN")).thenReturn(List.of(new AuditoriaAcceso()));
        assertEquals(1, auditoriaService.consultar(null, "LOGIN").size());
    }

    @Test
    void testConsultarTodos() {
        when(auditoriaAccesoRepository.findAll()).thenReturn(List.of(new AuditoriaAcceso()));
        assertEquals(1, auditoriaService.consultar(null, null).size());
    }
}
