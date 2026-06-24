package cl.vetnova.notificaciones.service;

import cl.vetnova.notificaciones.exception.BusinessRuleException;
import cl.vetnova.notificaciones.exception.ResourceNotFoundException;
import cl.vetnova.notificaciones.model.CanalNotificacion;
import cl.vetnova.notificaciones.model.HistorialMensaje;
import cl.vetnova.notificaciones.model.Notificacion;
import cl.vetnova.notificaciones.repository.CanalNotificacionRepository;
import cl.vetnova.notificaciones.repository.HistorialMensajeRepository;
import cl.vetnova.notificaciones.repository.NotificacionRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HistorialMensajeService {

    private static final Set<String> ESTADOS = Set.of("ENVIADO", "ENTREGADO", "FALLIDO", "LEIDO");

    private final HistorialMensajeRepository historialRepository;
    private final NotificacionRepository notificacionRepository;
    private final CanalNotificacionRepository canalRepository;

    public HistorialMensajeService(HistorialMensajeRepository historialRepository, NotificacionRepository notificacionRepository,
                                   CanalNotificacionRepository canalRepository) {
        this.historialRepository = historialRepository;
        this.notificacionRepository = notificacionRepository;
        this.canalRepository = canalRepository;
    }

    @Transactional
    public HistorialMensaje crear(HistorialMensaje request) {
        if (request.getNotificacionId() == null) {
            throw new BusinessRuleException("El notificacionId es obligatorio");
        }
        Notificacion notificacion = notificacionRepository.findById(request.getNotificacionId())
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada"));
        if (request.getCanalId() == null) {
            throw new BusinessRuleException("El canalId es obligatorio");
        }
        CanalNotificacion canal = canalRepository.findById(request.getCanalId())
                .orElseThrow(() -> new ResourceNotFoundException("Canal de notificación no encontrado"));
        if (!Objects.equals(canal.getUsuarioId(), notificacion.getUsuarioId())) {
            throw new BusinessRuleException("El canal no pertenece al usuario de la notificación");
        }
        if (request.getEstado() == null) {
            throw new BusinessRuleException("El estado es obligatorio");
        }
        if (!ESTADOS.contains(request.getEstado())) {
            throw new BusinessRuleException("Estado no válido. Valores permitidos: ENVIADO, ENTREGADO, FALLIDO, LEIDO");
        }
        HistorialMensaje historial = new HistorialMensaje();
        historial.setNotificacionId(notificacion.getId());
        historial.setCanalId(canal.getId());
        historial.setEstado(request.getEstado());
        historial.setDescripcion(request.getDescripcion());
        historial.setFechaEnvio(LocalDateTime.now(ZoneOffset.UTC));
        return historialRepository.save(historial);
    }

    @Transactional(readOnly = true)
    public List<HistorialMensaje> listar(Long notificacionId, Long canalId, String estado) {
        if (notificacionId != null) {
            return historialRepository.findByNotificacionIdOrderByFechaEnvioAsc(notificacionId);
        }
        if (canalId != null) {
            return historialRepository.findByCanalIdOrderByFechaEnvioDesc(canalId);
        }
        if (estado != null) {
            return historialRepository.findByEstado(estado);
        }
        return historialRepository.findAll();
    }
}
