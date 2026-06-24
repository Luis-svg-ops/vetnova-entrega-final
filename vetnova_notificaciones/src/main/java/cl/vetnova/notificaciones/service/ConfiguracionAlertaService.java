package cl.vetnova.notificaciones.service;

import cl.vetnova.notificaciones.exception.BusinessRuleException;
import cl.vetnova.notificaciones.exception.ConflictException;
import cl.vetnova.notificaciones.exception.ResourceNotFoundException;
import cl.vetnova.notificaciones.model.ConfiguracionAlerta;
import cl.vetnova.notificaciones.repository.CanalNotificacionRepository;
import cl.vetnova.notificaciones.repository.ConfiguracionAlertaRepository;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfiguracionAlertaService {

    private static final Set<String> TIPOS_EVENTO = Set.of(
            "STOCK_CRITICO", "CITA_CONFIRMADA", "CITA_CANCELADA", "PAGO_APROBADO", "PAGO_RECHAZADO",
            "TICKET_RESPONDIDO", "EXAMEN_LISTO", "INCIDENTE_SISTEMA", "FOLIO_BAJO");
    private static final String EVENTO_INVALIDO = "Tipo de evento no válido. Valores permitidos: STOCK_CRITICO, "
            + "CITA_CONFIRMADA, CITA_CANCELADA, PAGO_APROBADO, PAGO_RECHAZADO, TICKET_RESPONDIDO, EXAMEN_LISTO, "
            + "INCIDENTE_SISTEMA, FOLIO_BAJO";

    private final ConfiguracionAlertaRepository configuracionRepository;
    private final CanalNotificacionRepository canalRepository;

    public ConfiguracionAlertaService(ConfiguracionAlertaRepository configuracionRepository, CanalNotificacionRepository canalRepository) {
        this.configuracionRepository = configuracionRepository;
        this.canalRepository = canalRepository;
    }

    @Transactional(readOnly = true)
    public ConfiguracionAlerta buscar(Long id) {
        return configuracionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Configuración de alerta no encontrada"));
    }

    @Transactional
    public ConfiguracionAlerta crear(ConfiguracionAlerta request) {
        if (request.getUsuarioId() == null) {
            throw new BusinessRuleException("El usuarioId es obligatorio");
        }
        // La existencia del usuario vive en MS Auth → verificación diferida.
        if (request.getTipoEvento() == null) {
            throw new BusinessRuleException("El tipo de evento es obligatorio");
        }
        if (!TIPOS_EVENTO.contains(request.getTipoEvento())) {
            throw new BusinessRuleException(EVENTO_INVALIDO);
        }
        if (configuracionRepository.existsByUsuarioIdAndTipoEventoAndCanal(
                request.getUsuarioId(), request.getTipoEvento(), request.getCanal())) {
            throw new ConflictException("Ya existe una configuración de alerta para ese evento y canal");
        }
        if (request.getCanal() == null) {
            throw new BusinessRuleException("El canal es obligatorio");
        }
        if (!NotificacionService.TIPOS.contains(request.getCanal())) {
            throw new BusinessRuleException("Canal no válido. Valores permitidos: EMAIL, SMS, PUSH, SISTEMA");
        }
        if (!canalRepository.existsByUsuarioIdAndTipo(request.getUsuarioId(), request.getCanal())) {
            throw new BusinessRuleException("El usuario no tiene configurado el canal " + request.getCanal());
        }
        ConfiguracionAlerta configuracion = new ConfiguracionAlerta();
        configuracion.setUsuarioId(request.getUsuarioId());
        configuracion.setTipoEvento(request.getTipoEvento());
        configuracion.setCanal(request.getCanal());
        configuracion.setActiva(true);
        return configuracionRepository.save(configuracion);
    }

    @Transactional
    public ConfiguracionAlerta desactivar(Long id) {
        ConfiguracionAlerta configuracion = buscar(id);
        if (!Boolean.TRUE.equals(configuracion.getActiva())) {
            return configuracion;
        }
        configuracion.setActiva(false);
        return configuracionRepository.save(configuracion);
    }

    @Transactional
    public ConfiguracionAlerta activar(Long id) {
        ConfiguracionAlerta configuracion = buscar(id);
        configuracion.setActiva(true);
        return configuracionRepository.save(configuracion);
    }

    @Transactional
    public void eliminar(Long id) {
        ConfiguracionAlerta configuracion = buscar(id);
        configuracionRepository.delete(configuracion);
    }

    @Transactional(readOnly = true)
    public List<ConfiguracionAlerta> listar(Long usuarioId) {
        return configuracionRepository.findByUsuarioId(usuarioId);
    }
}
