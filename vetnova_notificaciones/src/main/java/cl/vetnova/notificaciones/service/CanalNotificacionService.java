package cl.vetnova.notificaciones.service;

import cl.vetnova.notificaciones.exception.BusinessRuleException;
import cl.vetnova.notificaciones.exception.ConflictException;
import cl.vetnova.notificaciones.exception.ResourceNotFoundException;
import cl.vetnova.notificaciones.model.CanalNotificacion;
import cl.vetnova.notificaciones.repository.CanalNotificacionRepository;
import cl.vetnova.notificaciones.repository.HistorialMensajeRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CanalNotificacionService {

    private static final String EMAIL_REGEX = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
    private static final String TELEFONO_REGEX = "^\\+?\\d{7,15}$";

    private final CanalNotificacionRepository canalRepository;
    private final HistorialMensajeRepository historialRepository;

    public CanalNotificacionService(CanalNotificacionRepository canalRepository, HistorialMensajeRepository historialRepository) {
        this.canalRepository = canalRepository;
        this.historialRepository = historialRepository;
    }

    @Transactional(readOnly = true)
    public CanalNotificacion buscar(Long id) {
        return canalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Canal de notificación no encontrado"));
    }

    @Transactional
    public CanalNotificacion crear(CanalNotificacion request) {
        if (request.getUsuarioId() == null) {
            throw new BusinessRuleException("El usuarioId es obligatorio");
        }
        // La existencia del usuario vive en MS Auth → verificación diferida.
        if (request.getTipo() == null) {
            throw new BusinessRuleException("El tipo de canal es obligatorio");
        }
        if (!NotificacionService.TIPOS.contains(request.getTipo())) {
            throw new BusinessRuleException(NotificacionService.TIPO_INVALIDO);
        }
        if (canalRepository.existsByUsuarioIdAndTipo(request.getUsuarioId(), request.getTipo())) {
            throw new ConflictException("El usuario ya tiene configurado un canal de tipo " + request.getTipo());
        }
        if (request.getDestino() == null) {
            throw new BusinessRuleException("El destino es obligatorio");
        }
        if ("EMAIL".equals(request.getTipo()) && !request.getDestino().matches(EMAIL_REGEX)) {
            throw new BusinessRuleException("El destino no tiene formato de email válido");
        }
        if ("SMS".equals(request.getTipo()) && !request.getDestino().matches(TELEFONO_REGEX)) {
            throw new BusinessRuleException("El destino no tiene formato de teléfono válido");
        }
        CanalNotificacion canal = new CanalNotificacion();
        canal.setUsuarioId(request.getUsuarioId());
        canal.setTipo(request.getTipo());
        canal.setDestino(request.getDestino());
        canal.setActivo(true);
        return canalRepository.save(canal);
    }

    @Transactional
    public CanalNotificacion actualizar(Long id, CanalNotificacion request) {
        CanalNotificacion canal = buscar(id);
        if (request.getDestino() != null) {
            canal.setDestino(request.getDestino());
        }
        return canalRepository.save(canal);
    }

    @Transactional
    public CanalNotificacion desactivar(Long id) {
        CanalNotificacion canal = buscar(id);
        if (!Boolean.TRUE.equals(canal.getActivo())) {
            return canal;
        }
        canal.setActivo(false);
        return canalRepository.save(canal);
    }

    @Transactional
    public void eliminar(Long id) {
        CanalNotificacion canal = buscar(id);
        if (historialRepository.existsByCanalId(canal.getId())) {
            throw new BusinessRuleException("No se puede eliminar un canal con historial de mensajes");
        }
        canalRepository.delete(canal);
    }

    @Transactional(readOnly = true)
    public List<CanalNotificacion> listar(Long usuarioId) {
        return canalRepository.findByUsuarioId(usuarioId);
    }
}
