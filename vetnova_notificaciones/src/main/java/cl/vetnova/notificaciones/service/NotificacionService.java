package cl.vetnova.notificaciones.service;

import cl.vetnova.notificaciones.exception.BusinessRuleException;
import cl.vetnova.notificaciones.exception.ResourceNotFoundException;
import cl.vetnova.notificaciones.model.CanalNotificacion;
import cl.vetnova.notificaciones.model.Notificacion;
import cl.vetnova.notificaciones.repository.CanalNotificacionRepository;
import cl.vetnova.notificaciones.repository.NotificacionRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;



@Service
public class NotificacionService {

    private final RestTemplate restTemplate;

    static final Set<String> TIPOS = Set.of("EMAIL", "SMS", "PUSH", "SISTEMA");
    static final String TIPO_INVALIDO = "Tipo no válido. Valores permitidos: EMAIL, SMS, PUSH, SISTEMA";

    private final NotificacionRepository notificacionRepository;
    private final CanalNotificacionRepository canalRepository;

    public NotificacionService(NotificacionRepository notificacionRepository, 
                           CanalNotificacionRepository canalRepository,
                           RestTemplate restTemplate) {
    this.notificacionRepository = notificacionRepository;
    this.canalRepository = canalRepository;
    this.restTemplate = restTemplate;
    }

    @Transactional(readOnly = true)
    public Notificacion buscar(Long id) {
        return notificacionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada"));
    }

    @Transactional
    public Notificacion crear(Notificacion request) {
        
        //importantisimo
        if (request.getUsuarioId() == null) {
        throw new BusinessRuleException("El usuarioId es obligatorio");
        }

        try {
            String url = "http://localhost:8081/api/usuarios/" + request.getUsuarioId() + "/existe";
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> resp = restTemplate.getForObject(url, java.util.Map.class);
            if (resp == null || !Boolean.TRUE.equals(resp.get("existe"))) {
                throw new ResourceNotFoundException("Usuario no encontrado en el sistema");
            }
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ResourceNotFoundException("No se pudo verificar el usuario en el sistema");
        }
        // La existencia y el estado activo del usuario viven en MS Auth → verificación diferida.
        if (request.getTipo() == null) {
            throw new BusinessRuleException("El tipo de notificación es obligatorio");
        }
        if (!TIPOS.contains(request.getTipo())) {
            throw new BusinessRuleException(TIPO_INVALIDO);
        }
        if (request.getMensaje() == null) {
            throw new BusinessRuleException("El mensaje es obligatorio");
        }
        if (request.getMensaje().trim().isEmpty()) {
            throw new BusinessRuleException("El mensaje no puede estar vacío");
        }
        CanalNotificacion canal = canalRepository.findByUsuarioIdAndTipo(request.getUsuarioId(), request.getTipo())
                .orElseThrow(() -> new BusinessRuleException("El usuario no tiene configurado el canal " + request.getTipo()));
        if (!Boolean.TRUE.equals(canal.getActivo())) {
            throw new BusinessRuleException("El canal " + request.getTipo() + " del usuario está inactivo");
        }
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuarioId(request.getUsuarioId());
        notificacion.setTipo(request.getTipo());
        notificacion.setMensaje(request.getMensaje());
        notificacion.setLeida(false);
        notificacion.setEstado("ENVIADO");
        notificacion.setFechaEnvio(LocalDateTime.now(ZoneOffset.UTC));
        return notificacionRepository.save(notificacion);
    }

    @Transactional
    public Notificacion marcarLeida(Long id) {
        Notificacion notificacion = buscar(id);
        if (Boolean.TRUE.equals(notificacion.getLeida())) {
            return notificacion;
        }
        notificacion.setLeida(true);
        notificacion.setFechaLectura(LocalDateTime.now(ZoneOffset.UTC));
        return notificacionRepository.save(notificacion);
    }

    @Transactional
    public Notificacion reenviar(Long id) {
        Notificacion notificacion = buscar(id);
        notificacion.setEstado("ENVIADO");
        notificacion.setFechaEnvio(LocalDateTime.now(ZoneOffset.UTC));
        // El registro del nuevo intento en HistorialMensaje se delega al flujo de envío → diferido.
        return notificacionRepository.save(notificacion);
    }

    @Transactional(readOnly = true)
    public List<Notificacion> listar(Long usuarioId, Boolean leida) {
        return leida == null
                ? notificacionRepository.findByUsuarioIdOrderByFechaEnvioDesc(usuarioId)
                : notificacionRepository.findByUsuarioIdAndLeidaOrderByFechaEnvioDesc(usuarioId, leida);
    }

    @Transactional(readOnly = true)
    public long contarNoLeidas(Long usuarioId) {
        return notificacionRepository.countByUsuarioIdAndLeidaFalse(usuarioId);
    }
}
