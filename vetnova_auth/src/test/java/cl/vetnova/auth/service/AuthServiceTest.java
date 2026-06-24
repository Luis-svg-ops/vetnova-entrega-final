package cl.vetnova.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cl.vetnova.auth.dto.AuthResponse;
import cl.vetnova.auth.dto.LoginRequest;
import cl.vetnova.auth.dto.PasswordChangeRequest;
import cl.vetnova.auth.dto.RegisterRequest;
import cl.vetnova.auth.dto.UsuarioResponse;
import cl.vetnova.auth.dto.ValidateTokenResponse;
import cl.vetnova.auth.exception.BusinessRuleException;
import cl.vetnova.auth.exception.ResourceNotFoundException;
import cl.vetnova.auth.exception.UnauthorizedException;
import cl.vetnova.auth.model.RolPermiso;
import cl.vetnova.auth.model.SesionToken;
import cl.vetnova.auth.model.Usuario;
import cl.vetnova.auth.repository.SesionTokenRepository;
import cl.vetnova.auth.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.ArgumentMatchers.eq;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private SesionTokenRepository tokenRepository;
    @Mock
    private RolPermisoService rolPermisoService;
    @Mock
    private AuditoriaService auditoriaService;
    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthService authService;
    private Usuario usuario;
    private final MockHttpServletRequest servletRequest = new MockHttpServletRequest();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(usuarioRepository, tokenRepository, rolPermisoService,
                auditoriaService, passwordEncoder, 120);
        RolPermiso rol = new RolPermiso("CLIENTE", "Cliente", Set.of("VER_PERFIL"));
        usuario = new Usuario("Camila Rojas", "cliente@vetnova.cl", "+56911111111", "hash", rol);
    }

    @Test
    void testLoginCorrectoDevuelveTokenYUsuario() {
        when(usuarioRepository.findByEmailIgnoreCase("cliente@vetnova.cl")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("Cliente1234", "hash")).thenReturn(true);

        AuthResponse response = authService.login(new LoginRequest("cliente@vetnova.cl", "Cliente1234"), servletRequest);

        assertNotNull(response.token());
        assertEquals("cliente@vetnova.cl", response.usuario().email());
        verify(tokenRepository).save(any(SesionToken.class));
    }

    @Test
    void testLoginConPasswordIncorrectaLanzaExcepcion() {
        when(usuarioRepository.findByEmailIgnoreCase("cliente@vetnova.cl")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("mala", "hash")).thenReturn(false);

        assertThrows(UnauthorizedException.class,
                () -> authService.login(new LoginRequest("cliente@vetnova.cl", "mala"), servletRequest));
        verify(tokenRepository, never()).save(any());
    }

    @Test
    void testLoginConEmailInexistenteLanzaExcepcion() {
        when(usuarioRepository.findByEmailIgnoreCase("nadie@vetnova.cl")).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class,
                () -> authService.login(new LoginRequest("nadie@vetnova.cl", "x"), servletRequest));
    }

    @Test
    void testLoginConCuentaDesactivadaLanzaExcepcion() {
        usuario.setActivo(false);
        when(usuarioRepository.findByEmailIgnoreCase("cliente@vetnova.cl")).thenReturn(Optional.of(usuario));

        assertThrows(UnauthorizedException.class,
                () -> authService.login(new LoginRequest("cliente@vetnova.cl", "Cliente1234"), servletRequest));
    }

    @Test
    void testRegistrarCreaElUsuarioConPasswordEncriptada() {
        RegisterRequest request = new RegisterRequest("Pedro Soto", "pedro@vetnova.cl",
                "+56933333333", "Pedro12345", "CLIENTE");
        when(usuarioRepository.existsByEmailIgnoreCase("pedro@vetnova.cl")).thenReturn(false);
        when(rolPermisoService.buscarPorNombre("CLIENTE"))
                .thenReturn(new RolPermiso("CLIENTE", "Cliente", Set.of()));
        when(passwordEncoder.encode("Pedro12345")).thenReturn("hash-nuevo");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResponse response = authService.registrar(request);

        assertEquals("pedro@vetnova.cl", response.email());
        verify(passwordEncoder).encode("Pedro12345");
    }

    @Test
    void testRegistrarConEmailRepetidoLanzaExcepcion() {
        when(usuarioRepository.existsByEmailIgnoreCase("cliente@vetnova.cl")).thenReturn(true);
        RegisterRequest request = new RegisterRequest("Otra Persona", "cliente@vetnova.cl",
                "+56933333333", "Password123", "CLIENTE");

        assertThrows(BusinessRuleException.class, () -> authService.registrar(request));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void testValidarTokenVigenteDevuelveDatosDelUsuario() {
        SesionToken sesion = new SesionToken(usuario, "tok123", LocalDateTime.now().plusHours(1));
        when(tokenRepository.findByToken("tok123")).thenReturn(Optional.of(sesion));

        ValidateTokenResponse response = authService.validarToken("tok123");

        assertTrue(response.valido());
        assertEquals("CLIENTE", response.rol());
    }

    @Test
    void testValidarTokenInexistenteRespondeInvalido() {
        when(tokenRepository.findByToken("nada")).thenReturn(Optional.empty());

        assertFalse(authService.validarToken("nada").valido());
    }

    @Test
    void testCambiarPasswordConActualIncorrectaLanzaExcepcion() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("mala", "hash")).thenReturn(false);

        assertThrows(BusinessRuleException.class,
                () -> authService.cambiarPassword(1L, new PasswordChangeRequest("mala", "Nueva12345")));
    }

    @Test
    void testCambiarPasswordDeUsuarioInexistenteLanzaNotFound() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> authService.cambiarPassword(99L, new PasswordChangeRequest("a", "Nueva12345")));
    }

    @Test
    void testLogoutRevocaElToken() {
        SesionToken sesion = new SesionToken(usuario, "tok123", LocalDateTime.now().plusHours(1));
        when(tokenRepository.findByToken("tok123")).thenReturn(Optional.of(sesion));
        when(tokenRepository.save(any(SesionToken.class))).thenAnswer(inv -> inv.getArgument(0));

        authService.logout("tok123");

        assertFalse(sesion.esValido());
        verify(tokenRepository).save(sesion);
    }

    @Test
    void testCambiarPasswordConActualCorrectaGuardaLaNueva() {
        Usuario usuario = new Usuario();
        usuario.setPasswordHash("hash-anterior");
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("Clave12345", "hash-anterior")).thenReturn(true);
        when(passwordEncoder.encode("NuevaClave99")).thenReturn("hash-nuevo");

        authService.cambiarPassword(2L, new PasswordChangeRequest("Clave12345", "NuevaClave99"));

        assertEquals("hash-nuevo", usuario.getPasswordHash());
        verify(usuarioRepository).save(usuario);
        verify(auditoriaService).registrar(eq(usuario), eq("PASSWORD_CHANGE"), any(), eq(true), any());
    }

    @Test
    void testCambiarPasswordConActualIncorrectaLanzaBusinessRule() {
        Usuario usuario = new Usuario();
        usuario.setPasswordHash("hash-anterior");
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("equivocada", "hash-anterior")).thenReturn(false);

        assertThrows(BusinessRuleException.class,
                () -> authService.cambiarPassword(2L, new PasswordChangeRequest("equivocada", "NuevaClave99")));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void testRegistrarConEmailDuplicadoLanzaBusinessRule() {
        when(usuarioRepository.existsByEmailIgnoreCase("fernanda@vetnova.cl")).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> authService.registrar(new RegisterRequest(
                "Fernanda Soto", "fernanda@vetnova.cl", "+56911111111", "Clave12345", "CLIENTE")));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void testLoginDeUsuarioInactivoLanzaBusinessRule() {
        Usuario usuario = new Usuario();
        usuario.setActivo(false);
        when(usuarioRepository.findByEmailIgnoreCase("fernanda@vetnova.cl")).thenReturn(Optional.of(usuario));

        assertThrows(UnauthorizedException.class, () -> authService.login(
                new LoginRequest("fernanda@vetnova.cl", "Clave12345"), new MockHttpServletRequest()));
    }

    @Test
    void testValidarTokenValidoDevuelveDatosDelUsuario() {
        RolPermiso rol = new RolPermiso("VETERINARIO", "Veterinario", new java.util.HashSet<>());
        Usuario usuario = new Usuario();
        usuario.setRol(rol);
        SesionToken sesion = new SesionToken(usuario, "token-ok", java.time.LocalDateTime.now().plusMinutes(30));
        when(tokenRepository.findByToken("token-ok")).thenReturn(Optional.of(sesion));

        assertTrue(authService.validarToken("token-ok").valido());
    }

    @Test
    void testValidarTokenRevocadoDevuelveNoValido() {
        Usuario usuario = new Usuario();
        SesionToken sesion = new SesionToken(usuario, "token-rev", java.time.LocalDateTime.now().plusMinutes(30));
        sesion.revocar();
        when(tokenRepository.findByToken("token-rev")).thenReturn(Optional.of(sesion));

        assertFalse(authService.validarToken("token-rev").valido());
    }

    @Test
    void testLogoutConTokenInexistenteLanzaNotFound() {
        when(tokenRepository.findByToken("inexistente")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.logout("inexistente"));
        verify(tokenRepository, never()).save(any());
    }

    @Test
    void testLogoutConTokenExpiradoLanza401() {
        SesionToken sesion = new SesionToken(usuario, "tok-exp", LocalDateTime.now().minusMinutes(1));
        when(tokenRepository.findByToken("tok-exp")).thenReturn(Optional.of(sesion));

        assertThrows(UnauthorizedException.class, () -> authService.logout("tok-exp"));
        verify(tokenRepository, never()).save(any());
    }
}
