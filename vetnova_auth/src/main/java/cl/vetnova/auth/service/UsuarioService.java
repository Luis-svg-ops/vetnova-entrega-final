package cl.vetnova.auth.service;

import cl.vetnova.auth.dto.CambiarPasswordRequest;
import cl.vetnova.auth.dto.CrearUsuarioRequest;
import cl.vetnova.auth.dto.UpdatePerfilRequest;
import cl.vetnova.auth.dto.UsuarioResponse;
import cl.vetnova.auth.exception.BusinessRuleException;
import cl.vetnova.auth.exception.ConflictException;
import cl.vetnova.auth.exception.ResourceNotFoundException;
import cl.vetnova.auth.model.RolPermiso;
import cl.vetnova.auth.model.SesionToken;
import cl.vetnova.auth.model.Usuario;
import cl.vetnova.auth.repository.SesionTokenRepository;
import cl.vetnova.auth.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {
    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Set<String> ROLES_VALIDOS = Set.of(
            "ADMIN_SISTEMA", "ADMIN_SUCURSAL", "RECEPCIONISTA", "VETERINARIO", "BODEGA", "CLIENTE");

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final RolPermisoService rolPermisoService;
    private final SesionTokenRepository sesionTokenRepository;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder,
                          RolPermisoService rolPermisoService, SesionTokenRepository sesionTokenRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.rolPermisoService = rolPermisoService;
        this.sesionTokenRepository = sesionTokenRepository;
    }

    @Transactional
    public void cambiarPassword(Long id, CambiarPasswordRequest request) {
        Usuario usuario = buscarEntidad(id);
        if (!passwordEncoder.matches(request.passwordActual(), usuario.getPasswordHash())) {
            throw new BusinessRuleException("El password actual es incorrecto");
        }
        try {
            validarPassword(request.passwordNuevo());
        } catch (BusinessRuleException e) {
            throw new BusinessRuleException("El password nuevo no cumple con la política de seguridad");
        }
        if (passwordEncoder.matches(request.passwordNuevo(), usuario.getPasswordHash())) {
            throw new BusinessRuleException("El password nuevo no puede ser igual al actual");
        }
        usuario.setPasswordHash(passwordEncoder.encode(request.passwordNuevo()));
        invalidarSesiones(usuario);
        repository.save(usuario);
        log.info("event=password_changed usuarioId={}", id);
    }

    private void invalidarSesiones(Usuario usuario) {
        for (SesionToken sesion : sesionTokenRepository.findByUsuarioAndActivoTrue(usuario)) {
            sesion.revocar();
            sesionTokenRepository.save(sesion);
        }
    }

    @Transactional
    public UsuarioResponse crear(CrearUsuarioRequest request) {
        validarEmail(request.email());
        validarPassword(request.password());
        validarRol(request.nombreRol());
        if (repository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("El email ya está registrado");
        }
        RolPermiso rol = rolPermisoService.buscarPorNombre(request.nombreRol());
        Usuario usuario = new Usuario(
                request.nombre(),
                request.email().toLowerCase(),
                request.telefono(),
                passwordEncoder.encode(request.password()),
                rol);
        usuario.setActivo(true);
        usuario.setFechaCreacion(LocalDateTime.now());
        Usuario guardado = repository.save(usuario);
        log.info("event=usuario_created usuarioId={} email={} rol={}", guardado.getId(), guardado.getEmail(), rol.getNombreRol());
        return UsuarioResponse.from(guardado);
    }

    private void validarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BusinessRuleException("El email es obligatorio");
        }
        if (!EMAIL.matcher(email).matches()) {
            throw new BusinessRuleException("El email no tiene un formato válido");
        }
    }

    private void validarPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new BusinessRuleException("El password es obligatorio");
        }
        if (password.length() < 8) {
            throw new BusinessRuleException("El password debe tener al menos 8 caracteres");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new BusinessRuleException("El password debe contener al menos una mayúscula");
        }
        if (!password.matches(".*[0-9].*")) {
            throw new BusinessRuleException("El password debe contener al menos un número");
        }
        if (!password.matches(".*[^A-Za-z0-9].*")) {
            throw new BusinessRuleException("El password debe contener al menos un símbolo");
        }
    }

    private void validarRol(String nombreRol) {
        if (nombreRol == null || nombreRol.isBlank()) {
            throw new BusinessRuleException("El rol es obligatorio");
        }
        if (!ROLES_VALIDOS.contains(nombreRol)) {
            throw new BusinessRuleException(
                    "Rol no válido. Valores permitidos: ADMIN_SISTEMA, ADMIN_SUCURSAL, RECEPCIONISTA, VETERINARIO, BODEGA, CLIENTE");
        }
    }

    public List<UsuarioResponse> listar() {
        return repository.findAll().stream().map(UsuarioResponse::from).toList();
    }

    public Usuario buscarEntidad(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));
    }

    public UsuarioResponse buscar(Long id) {
        return UsuarioResponse.from(buscarEntidad(id));
    }

    public boolean existe(Long id) {
        return repository.existsById(id);
    }

    @Transactional
    public UsuarioResponse actualizarPerfil(Long id, UpdatePerfilRequest request) {
        Usuario usuario = buscarEntidad(id);
        usuario.setNombre(request.nombre());
        usuario.setTelefono(request.telefono());
        log.info("event=usuario_profile_updated usuarioId={}", id);
        return UsuarioResponse.from(repository.save(usuario));
    }

    @Transactional
    public UsuarioResponse activar(Long id) {
        Usuario usuario = buscarEntidad(id);
        usuario.setActivo(true);
        log.info("event=usuario_activated usuarioId={}", id);
        return UsuarioResponse.from(repository.save(usuario));
    }

    @Transactional
    public UsuarioResponse desactivar(Long id) {
        Usuario usuario = buscarEntidad(id);
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            return UsuarioResponse.from(usuario);
        }
        if ("ADMIN_SISTEMA".equals(usuario.getRol().getNombreRol())
                && repository.countByRolNombreRolAndActivoTrue("ADMIN_SISTEMA") <= 1) {
            throw new BusinessRuleException("No se puede desactivar el único administrador del sistema");
        }
        usuario.setActivo(false);
        invalidarSesiones(usuario);
        log.info("event=usuario_deactivated usuarioId={}", id);
        return UsuarioResponse.from(repository.save(usuario));
    }
}
