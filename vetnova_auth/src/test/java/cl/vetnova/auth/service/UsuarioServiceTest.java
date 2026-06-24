package cl.vetnova.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cl.vetnova.auth.dto.CambiarPasswordRequest;
import cl.vetnova.auth.dto.CrearUsuarioRequest;
import cl.vetnova.auth.dto.UpdatePerfilRequest;
import cl.vetnova.auth.dto.UsuarioResponse;
import cl.vetnova.auth.exception.BusinessRuleException;
import cl.vetnova.auth.exception.ConflictException;
import cl.vetnova.auth.exception.ResourceNotFoundException;
import cl.vetnova.auth.model.RolPermiso;
import cl.vetnova.auth.model.Usuario;
import cl.vetnova.auth.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RolPermisoService rolPermisoService;

    @Mock
    private cl.vetnova.auth.repository.SesionTokenRepository sesionTokenRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private RolPermiso rolCliente;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rolCliente = new RolPermiso("CLIENTE", "Cliente de la clínica", Set.of("VER_PERFIL"));
        usuario = new Usuario("Camila Rojas", "cliente@vetnova.cl", "+56911111111", "hash", rolCliente);
    }

    private CrearUsuarioRequest request(String email, String password, String rol) {
        return new CrearUsuarioRequest("Juan", email, "+56912345678", password, rol);
    }

    @Test
    void testCrearConEmailNullLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> usuarioService.crear(request(null, "Pass1234!", "CLIENTE")));
        assertEquals("El email es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearConEmailVacioLanzaBusinessRule() {
        assertThrows(BusinessRuleException.class,
                () -> usuarioService.crear(request("   ", "Pass1234!", "CLIENTE")));
    }

    @Test
    void testCrearConEmailFormatoInvalidoLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> usuarioService.crear(request("juanmail.com", "Pass1234!", "CLIENTE")));
        assertEquals("El email no tiene un formato válido", ex.getMessage());
    }

    @Test
    void testCrearConPasswordNullLanzaBusinessRule() {
        assertThrows(BusinessRuleException.class,
                () -> usuarioService.crear(request("juan@mail.com", null, "CLIENTE")));
    }

    @Test
    void testCrearConPasswordVacioLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> usuarioService.crear(request("juan@mail.com", "", "CLIENTE")));
        assertEquals("El password es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearConPasswordCortoLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> usuarioService.crear(request("juan@mail.com", "Ab1!", "CLIENTE")));
        assertEquals("El password debe tener al menos 8 caracteres", ex.getMessage());
    }

    @Test
    void testCrearConPasswordSinMayusculaLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> usuarioService.crear(request("juan@mail.com", "abcd1234!", "CLIENTE")));
        assertEquals("El password debe contener al menos una mayúscula", ex.getMessage());
    }

    @Test
    void testCrearConPasswordSinNumeroLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> usuarioService.crear(request("juan@mail.com", "Abcdefgh!", "CLIENTE")));
        assertEquals("El password debe contener al menos un número", ex.getMessage());
    }

    @Test
    void testCrearConPasswordSinSimboloLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> usuarioService.crear(request("juan@mail.com", "Abcd1234", "CLIENTE")));
        assertEquals("El password debe contener al menos un símbolo", ex.getMessage());
    }

    @Test
    void testCrearConRolNullLanzaBusinessRule() {
        assertThrows(BusinessRuleException.class,
                () -> usuarioService.crear(request("juan@mail.com", "Pass1234!", null)));
    }

    @Test
    void testCrearConRolVacioLanzaBusinessRule() {
        assertThrows(BusinessRuleException.class,
                () -> usuarioService.crear(request("juan@mail.com", "Pass1234!", "  ")));
    }

    @Test
    void testCrearConRolInvalidoLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> usuarioService.crear(request("juan@mail.com", "Pass1234!", "SUPERADMIN")));
        assertTrue(ex.getMessage().startsWith("Rol no válido"));
    }

    @Test
    void testCrearConEmailDuplicadoLanzaConflict() {
        when(repository.existsByEmailIgnoreCase("juan@mail.com")).thenReturn(true);

        ConflictException ex = assertThrows(ConflictException.class,
                () -> usuarioService.crear(request("juan@mail.com", "Pass1234!", "CLIENTE")));
        assertEquals("El email ya está registrado", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void testCrearUsuarioCasoFelizGuardaActivoConHash() {
        when(repository.existsByEmailIgnoreCase("juan@mail.com")).thenReturn(false);
        when(rolPermisoService.buscarPorNombre("CLIENTE")).thenReturn(rolCliente);
        when(passwordEncoder.encode("Pass1234!")).thenReturn("hash-bcrypt");
        when(repository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResponse response = usuarioService.crear(request("juan@mail.com", "Pass1234!", "CLIENTE"));

        assertEquals("CLIENTE", response.rol());
        assertTrue(response.activo());
        assertNotNull(response.fechaCreacion());
        verify(passwordEncoder).encode("Pass1234!");
    }

    @Test
    void testListarDevuelveUsuariosMapeados() {
        when(repository.findAll()).thenReturn(List.of(usuario));

        List<UsuarioResponse> lista = usuarioService.listar();

        assertEquals(1, lista.size());
        assertEquals("cliente@vetnova.cl", lista.get(0).email());
    }

    @Test
    void testBuscarDevuelveElUsuario() {
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));

        UsuarioResponse response = usuarioService.buscar(1L);

        assertEquals("Camila Rojas", response.nombre());
        assertEquals("CLIENTE", response.rol());
    }

    @Test
    void testBuscarUsuarioInexistenteLanzaNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.buscar(99L));
    }

    @Test
    void testExisteDevuelveTrueCuandoElRepositorioLoEncuentra() {
        when(repository.existsById(1L)).thenReturn(true);

        assertTrue(usuarioService.existe(1L));
    }

    @Test
    void testExisteDevuelveFalseCuandoNoEsta() {
        when(repository.existsById(99L)).thenReturn(false);

        assertFalse(usuarioService.existe(99L));
    }

    @Test
    void testActualizarPerfilCambiaNombreYTelefono() {
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResponse response = usuarioService.actualizarPerfil(1L,
                new UpdatePerfilRequest("Camila Rojas Soto", "+56922222222"));

        assertEquals("Camila Rojas Soto", response.nombre());
        assertEquals("+56922222222", response.telefono());
    }

    @Test
    void testActivarYDesactivarCambianElEstado() {
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        assertFalse(usuarioService.desactivar(1L).activo());
        assertTrue(usuarioService.activar(1L).activo());
        verify(repository, times(2)).save(usuario);
    }

    @Test
    void testCambiarPasswordActualIncorrectoLanzaBusinessRule() {
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("malActual", "hash")).thenReturn(false);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> usuarioService.cambiarPassword(1L, new CambiarPasswordRequest("malActual", "NuevaClave9!")));
        assertEquals("El password actual es incorrecto", ex.getMessage());
    }

    @Test
    void testCambiarPasswordNuevoInvalidoLanzaBusinessRule() {
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("Pass1234!", "hash")).thenReturn(true);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> usuarioService.cambiarPassword(1L, new CambiarPasswordRequest("Pass1234!", "corto")));
        assertEquals("El password nuevo no cumple con la política de seguridad", ex.getMessage());
    }

    @Test
    void testCambiarPasswordNuevoIgualAlActualLanzaBusinessRule() {
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("Pass1234!", "hash")).thenReturn(true);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> usuarioService.cambiarPassword(1L, new CambiarPasswordRequest("Pass1234!", "Pass1234!")));
        assertEquals("El password nuevo no puede ser igual al actual", ex.getMessage());
    }

    @Test
    void testCambiarPasswordCasoFelizGuardaNuevoHashEInvalidaSesiones() {
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("Pass1234!", "hash")).thenReturn(true);
        when(passwordEncoder.matches("NuevaClave9!", "hash")).thenReturn(false);
        when(passwordEncoder.encode("NuevaClave9!")).thenReturn("nuevo-hash");

        usuarioService.cambiarPassword(1L, new CambiarPasswordRequest("Pass1234!", "NuevaClave9!"));

        assertEquals("nuevo-hash", usuario.getPasswordHash());
        verify(repository).save(usuario);
    }

    @Test
    void testCambiarPasswordInvalidaLasSesionesActivas() {
        cl.vetnova.auth.model.SesionToken sesion =
                new cl.vetnova.auth.model.SesionToken(usuario, "token-123", java.time.LocalDateTime.now().plusHours(1));
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("Pass1234!", "hash")).thenReturn(true);
        when(passwordEncoder.matches("NuevaClave9!", "hash")).thenReturn(false);
        when(passwordEncoder.encode("NuevaClave9!")).thenReturn("nuevo-hash");
        when(sesionTokenRepository.findByUsuarioAndActivoTrue(usuario)).thenReturn(List.of(sesion));

        usuarioService.cambiarPassword(1L, new CambiarPasswordRequest("Pass1234!", "NuevaClave9!"));

        assertFalse(sesion.getActivo());
        verify(sesionTokenRepository).save(sesion);
    }

    @Test
    void testDesactivarUnicoAdminLanzaBusinessRule() {
        Usuario admin = new Usuario("Admin", "admin@vetnova.cl", "+56900000000", "hash",
                new RolPermiso("ADMIN_SISTEMA", "Admin", Set.of("TODO")));
        admin.setActivo(true);
        when(repository.findById(1L)).thenReturn(Optional.of(admin));
        when(repository.countByRolNombreRolAndActivoTrue("ADMIN_SISTEMA")).thenReturn(1L);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> usuarioService.desactivar(1L));
        assertEquals("No se puede desactivar el único administrador del sistema", ex.getMessage());
    }

    @Test
    void testDesactivarAdminConOtrosAdministradoresFunciona() {
        Usuario admin = new Usuario("Admin", "admin@vetnova.cl", "+56900000000", "hash",
                new RolPermiso("ADMIN_SISTEMA", "Admin", Set.of("TODO")));
        admin.setActivo(true);
        when(repository.findById(1L)).thenReturn(Optional.of(admin));
        when(repository.countByRolNombreRolAndActivoTrue("ADMIN_SISTEMA")).thenReturn(2L);
        when(repository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        assertFalse(usuarioService.desactivar(1L).activo());
    }

    @Test
    void testDesactivarUsuarioYaInactivoEsIdempotente() {
        usuario.setActivo(false);
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));

        assertFalse(usuarioService.desactivar(1L).activo());
        verify(repository, never()).save(any());
    }
}
