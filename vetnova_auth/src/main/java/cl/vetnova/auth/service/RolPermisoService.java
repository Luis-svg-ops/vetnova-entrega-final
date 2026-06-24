package cl.vetnova.auth.service;

import cl.vetnova.auth.dto.RolRequest;
import cl.vetnova.auth.dto.RolResponse;
import cl.vetnova.auth.exception.BusinessRuleException;
import cl.vetnova.auth.exception.ConflictException;
import cl.vetnova.auth.exception.ResourceNotFoundException;
import cl.vetnova.auth.model.RolPermiso;
import cl.vetnova.auth.repository.RolPermisoRepository;
import cl.vetnova.auth.repository.UsuarioRepository;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RolPermisoService {
    private static final Logger log = LoggerFactory.getLogger(RolPermisoService.class);

    /** Catálogo de permisos válidos del sistema. */
    private static final Set<String> PERMISOS_VALIDOS = Set.of(
            "USUARIOS_GESTIONAR", "ROLES_GESTIONAR", "SISTEMA_MONITOREAR", "CUENTA_PROPIA",
            "TICKETS_CREAR", "TICKETS_GESTIONAR", "ORDENES_EXAMEN_CREAR", "ORDENES_EXAMEN_SOLICITAR",
            "RESULTADOS_CONSULTAR", "VER_CATALOGO", "GESTIONAR_CATALOGO", "VER_AGENDA", "AGENDAR_CITA",
            "GESTIONAR_AGENDA", "VER_FICHA", "GESTIONAR_FICHA", "VER_INVENTARIO", "GESTIONAR_INVENTARIO",
            "VER_STOCK", "VER_REPORTES", "VER_PERFIL", "VER_FACTURACION", "GESTIONAR_FACTURACION",
            "VER_VENTAS", "GESTIONAR_VENTAS");

    private final RolPermisoRepository repository;
    private final UsuarioRepository usuarioRepository;

    public RolPermisoService(RolPermisoRepository repository, UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<RolResponse> listar() {
        return repository.findAll().stream().map(RolResponse::from).toList();
    }

    public RolPermiso buscarEntidad(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + id));
    }

    public RolPermiso buscarPorNombre(String nombre) {
        return repository.findByNombreRolIgnoreCase(nombre).orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + nombre));
    }

    @Transactional
    public RolResponse crear(RolRequest request) {
        validarNombre(request.nombreRol());
        validarPermisos(request.permisos());
        if (repository.existsByNombreRolIgnoreCase(request.nombreRol())) {
            throw new ConflictException("Ya existe un rol con ese nombre");
        }
        RolPermiso rol = new RolPermiso(request.nombreRol().toUpperCase(), request.descripcion(), request.permisos());
        RolPermiso guardado = repository.save(rol);
        log.info("event=rol_created rolId={} nombre={}", guardado.getId(), guardado.getNombreRol());
        return RolResponse.from(guardado);
    }

    @Transactional
    public RolResponse actualizar(Long id, RolRequest request) {
        RolPermiso rol = buscarEntidad(id);
        rol.setNombreRol(request.nombreRol().toUpperCase());
        rol.setDescripcion(request.descripcion());
        rol.setPermisos(request.permisos());
        log.info("event=rol_updated rolId={}", id);
        return RolResponse.from(repository.save(rol));
    }

    @Transactional
    public RolResponse asignarPermiso(Long id, String permiso) {
        RolPermiso rol = buscarEntidad(id);
        if (!PERMISOS_VALIDOS.contains(permiso)) {
            throw new BusinessRuleException("Permiso no válido");
        }
        if (rol.tienePermiso(permiso)) {
            throw new ConflictException("El permiso ya está asignado a este rol");
        }
        rol.asignarPermiso(permiso);
        log.info("event=permiso_asignado rolId={} permiso={}", id, permiso);
        return RolResponse.from(repository.save(rol));
    }

    @Transactional
    public RolResponse revocarPermiso(Long id, String permiso) {
        RolPermiso rol = buscarEntidad(id);
        if (!rol.tienePermiso(permiso)) {
            throw new BusinessRuleException("El permiso no está asignado a este rol");
        }
        if (rol.getPermisos().size() <= 1) {
            throw new BusinessRuleException("No se puede revocar el único permiso del rol");
        }
        rol.revocarPermiso(permiso);
        log.info("event=permiso_revocado rolId={} permiso={}", id, permiso);
        return RolResponse.from(repository.save(rol));
    }

    public boolean tienePermiso(Long id, String permiso) {
        return buscarEntidad(id).tienePermiso(permiso);
    }

    @Transactional
    public void eliminar(Long id) {
        buscarEntidad(id);
        if (usuarioRepository.existsByRolId(id)) {
            throw new BusinessRuleException("No se puede eliminar un rol con usuarios asignados");
        }
        repository.deleteById(id);
        log.info("event=rol_eliminado rolId={}", id);
    }

    private void validarNombre(String nombreRol) {
        if (nombreRol == null) {
            throw new BusinessRuleException("El nombre del rol es obligatorio");
        }
        if (nombreRol.isBlank()) {
            throw new BusinessRuleException("El nombre del rol no puede estar vacío");
        }
    }

    private void validarPermisos(Set<String> permisos) {
        if (permisos == null) {
            throw new BusinessRuleException("La lista de permisos es obligatoria");
        }
        if (permisos.isEmpty()) {
            throw new BusinessRuleException("El rol debe tener al menos un permiso");
        }
        for (String permiso : permisos) {
            if (!PERMISOS_VALIDOS.contains(permiso)) {
                throw new BusinessRuleException("Permiso no válido: " + permiso);
            }
        }
    }
}
