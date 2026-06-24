package cl.vetnova.auth.service;

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
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UsuarioRepository usuarioRepository;
    private final SesionTokenRepository tokenRepository;
    private final RolPermisoService rolPermisoService;
    private final AuditoriaService auditoriaService;
    private final PasswordEncoder passwordEncoder;
    private final long expirationMinutes;

    public AuthService(
            UsuarioRepository usuarioRepository,
            SesionTokenRepository tokenRepository,
            RolPermisoService rolPermisoService,
            AuditoriaService auditoriaService,
            PasswordEncoder passwordEncoder,
            @Value("${app.token.expiration-minutes:480}") long expirationMinutes
    ) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.rolPermisoService = rolPermisoService;
        this.auditoriaService = auditoriaService;
        this.passwordEncoder = passwordEncoder;
        this.expirationMinutes = expirationMinutes;
    }

    @Transactional
    public UsuarioResponse registrar(RegisterRequest request) {
        if (usuarioRepository.existsByEmailIgnoreCase(request.email())) {
            throw new BusinessRuleException("El email ya está registrado");
        }
        RolPermiso rol = rolPermisoService.buscarPorNombre(request.rol());
        Usuario usuario = new Usuario(
                request.nombre(),
                request.email().toLowerCase(),
                request.telefono(),
                passwordEncoder.encode(request.password()),
                rol
        );
        Usuario guardado = usuarioRepository.save(usuario);
        auditoriaService.registrar(guardado, "REGISTER", null, true, "Cuenta creada");
        log.info("event=user_registered usuarioId={} email={} rol={}", guardado.getId(), guardado.getEmail(), rol.getNombreRol());
        return UsuarioResponse.from(guardado);
    }

    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletRequest servletRequest) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            auditoriaService.registrar(usuario, "LOGIN", servletRequest.getRemoteAddr(), false, "Cuenta inactiva");
            throw new UnauthorizedException("Credenciales inválidas");
        }
        if (!passwordEncoder.matches(request.password(), usuario.getPasswordHash())) {
            auditoriaService.registrar(usuario, "LOGIN", servletRequest.getRemoteAddr(), false, "Password incorrecta");
            throw new UnauthorizedException("Credenciales inválidas");
        }
        String token = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
        LocalDateTime expiracion = LocalDateTime.now().plusMinutes(expirationMinutes);
        tokenRepository.save(new SesionToken(usuario, token, expiracion));
        auditoriaService.registrar(usuario, "LOGIN", servletRequest.getRemoteAddr(), true, "Inicio de sesión exitoso");
        log.info("event=user_login usuarioId={} email={}", usuario.getId(), usuario.getEmail());
        return new AuthResponse(token, expiracion, UsuarioResponse.from(usuario));
    }

    @Transactional
    public void logout(String token) {
        SesionToken sesion = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token no encontrado"));
        if (sesion.getExpiracion().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Token expirado");
        }
        sesion.revocar();
        tokenRepository.save(sesion);
        auditoriaService.registrar(sesion.getUsuario(), "LOGOUT", null, true, "Cierre de sesión");
        log.info("event=user_logout usuarioId={}", sesion.getUsuario().getId());
    }

    public ValidateTokenResponse validarToken(String token) {
        return tokenRepository.findByToken(token)
                .filter(SesionToken::esValido)
                .map(s -> new ValidateTokenResponse(true, s.getUsuario().getId(), s.getUsuario().getRol().getNombreRol(), s.getUsuario().getRol().getPermisos()))
                .orElse(new ValidateTokenResponse(false, null, null, null));
    }

    @Transactional
    public void cambiarPassword(Long usuarioId, PasswordChangeRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + usuarioId));
        if (!passwordEncoder.matches(request.actual(), usuario.getPasswordHash())) {
            throw new BusinessRuleException("La contraseña actual no coincide");
        }
        usuario.setPasswordHash(passwordEncoder.encode(request.nueva()));
        usuarioRepository.save(usuario);
        auditoriaService.registrar(usuario, "PASSWORD_CHANGE", null, true, "Cambio de contraseña");
        log.info("event=password_changed usuarioId={}", usuarioId);
    }
}
