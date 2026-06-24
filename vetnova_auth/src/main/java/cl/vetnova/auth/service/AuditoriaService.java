package cl.vetnova.auth.service;

import cl.vetnova.auth.dto.AuditoriaResponse;
import cl.vetnova.auth.dto.CrearAuditoriaRequest;
import cl.vetnova.auth.exception.BusinessRuleException;
import cl.vetnova.auth.exception.ResourceNotFoundException;
import cl.vetnova.auth.model.AuditoriaAcceso;
import cl.vetnova.auth.model.Usuario;
import cl.vetnova.auth.repository.AuditoriaAccesoRepository;
import cl.vetnova.auth.repository.UsuarioRepository;
import cl.vetnova.auth.util.IpValidator;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditoriaService {
    private static final Set<String> ACCIONES_VALIDAS = Set.of(
            "LOGIN", "LOGOUT", "CAMBIO_PASSWORD", "CREACION_USUARIO", "DESACTIVACION_USUARIO", "CAMBIO_ROL");

    private final AuditoriaAccesoRepository repository;
    private final UsuarioRepository usuarioRepository;

    public AuditoriaService(AuditoriaAccesoRepository repository, UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    /** Registro interno automático (login, logout, etc.); no valida porque lo genera el sistema. */
    public void registrar(Usuario usuario, String accion, String ip, Boolean exitoso, String detalle) {
        repository.save(new AuditoriaAcceso(usuario, accion, ip, exitoso, detalle));
    }

    @Transactional
    public AuditoriaResponse crear(CrearAuditoriaRequest request) {
        if (request.usuarioId() == null) {
            throw new BusinessRuleException("El usuarioId es obligatorio");
        }
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        if (request.accion() == null) {
            throw new BusinessRuleException("La acción es obligatoria");
        }
        if (!ACCIONES_VALIDAS.contains(request.accion())) {
            throw new BusinessRuleException(
                    "Acción no válida. Valores permitidos: LOGIN, LOGOUT, CAMBIO_PASSWORD, CREACION_USUARIO, DESACTIVACION_USUARIO, CAMBIO_ROL");
        }
        if (request.exitoso() == null) {
            throw new BusinessRuleException("El campo exitoso es obligatorio");
        }
        if (request.ip() == null) {
            throw new BusinessRuleException("La IP es obligatoria");
        }
        if (!IpValidator.esValida(request.ip())) {
            throw new BusinessRuleException("El formato de IP no es válido");
        }
        AuditoriaAcceso acceso = new AuditoriaAcceso(usuario, request.accion(), request.ip(),
                request.exitoso(), request.detalles());
        return AuditoriaResponse.from(repository.save(acceso));
    }

    public List<AuditoriaResponse> consultar(Long usuarioId, String accion) {
        List<AuditoriaAcceso> registros;
        if (usuarioId != null) {
            registros = repository.findByUsuarioIdOrderByTimestampDesc(usuarioId);
        } else if (accion != null) {
            registros = repository.findByAccionOrderByTimestampDesc(accion);
        } else {
            registros = repository.findAll();
        }
        return registros.stream().map(AuditoriaResponse::from).toList();
    }
}
