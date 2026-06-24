package cl.vetnova.reportes.service;

import cl.vetnova.reportes.exception.BusinessRuleException;
import cl.vetnova.reportes.exception.ConflictException;
import cl.vetnova.reportes.exception.ResourceNotFoundException;
import cl.vetnova.reportes.model.IncidenteSistema;
import cl.vetnova.reportes.repository.IncidenteSistemaRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IncidenteSistemaService {

    private static final Set<String> SEVERIDADES = Set.of("BAJA", "MEDIA", "ALTA", "CRITICA");

    private final IncidenteSistemaRepository incidenteRepository;

    public IncidenteSistemaService(IncidenteSistemaRepository incidenteRepository) {
        this.incidenteRepository = incidenteRepository;
    }

    @Transactional(readOnly = true)
    public IncidenteSistema buscar(Long id) {
        return incidenteRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Incidente no encontrado"));
    }

    @Transactional
    public IncidenteSistema registrar(IncidenteSistema request) {
        if (request.getMicroservicio() == null) {
            throw new BusinessRuleException("El microservicio es obligatorio");
        }
        if (!MonitorSistemaService.MICROSERVICIOS.contains(request.getMicroservicio())) {
            throw new BusinessRuleException("Microservicio no válido. Debe ser uno de los 12 microservicios del sistema");
        }
        if (request.getTipo() == null) {
            throw new BusinessRuleException("El tipo de incidente es obligatorio");
        }
        if (request.getTipo().trim().isEmpty()) {
            throw new BusinessRuleException("El tipo de incidente no puede estar vacío");
        }
        if (request.getSeveridad() == null) {
            throw new BusinessRuleException("La severidad es obligatoria");
        }
        if (!SEVERIDADES.contains(request.getSeveridad())) {
            throw new BusinessRuleException("Severidad no válida. Valores permitidos: BAJA, MEDIA, ALTA, CRITICA");
        }
        if (request.getDescripcion() == null) {
            throw new BusinessRuleException("La descripción es obligatoria");
        }
        if (incidenteRepository.existsByMicroservicioAndTipoAndEstado(request.getMicroservicio(), request.getTipo(), "ABIERTO")) {
            throw new ConflictException("Ya existe un incidente abierto para ese microservicio y tipo");
        }
        request.setId(null);
        request.setEstado("ABIERTO");
        request.setNotificado(false);
        request.setFechaResolucion(null);
        request.setFechaDeteccion(LocalDateTime.now(ZoneOffset.UTC));
        return incidenteRepository.save(request);
    }

    @Transactional
    public IncidenteSistema notificarAdministrador(Long id) {
        IncidenteSistema incidente = buscar(id);
        if (Boolean.TRUE.equals(incidente.getNotificado())) {
            return incidente;
        }
        incidente.setNotificado(true);
        // Notificación a ADMIN_SISTEMA vía MS Notificaciones → diferida.
        return incidenteRepository.save(incidente);
    }

    @Transactional
    public IncidenteSistema resolver(Long id) {
        IncidenteSistema incidente = buscar(id);
        if ("RESUELTO".equals(incidente.getEstado())) {
            throw new BusinessRuleException("El incidente ya está resuelto");
        }
        incidente.setEstado("RESUELTO");
        incidente.setFechaResolucion(LocalDateTime.now(ZoneOffset.UTC));
        return incidenteRepository.save(incidente);
    }
}
