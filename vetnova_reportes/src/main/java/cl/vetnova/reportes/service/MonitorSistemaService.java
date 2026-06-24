package cl.vetnova.reportes.service;

import cl.vetnova.reportes.exception.BusinessRuleException;
import cl.vetnova.reportes.model.MonitorSistema;
import cl.vetnova.reportes.repository.MonitorSistemaRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MonitorSistemaService {

    static final Set<String> MICROSERVICIOS = Set.of(
            "MS1", "MS2", "MS3", "MS4", "MS5", "MS6", "MS7", "MS8", "MS9", "MS10", "MS11", "MS12");
    private static final Set<String> ESTADOS = Set.of("UP", "DEGRADED", "DOWN");

    private final MonitorSistemaRepository monitorRepository;

    public MonitorSistemaService(MonitorSistemaRepository monitorRepository) {
        this.monitorRepository = monitorRepository;
    }

    @Transactional
    public MonitorSistema registrar(MonitorSistema request) {
        if (request.getMicroservicio() == null) {
            throw new BusinessRuleException("El microservicio es obligatorio");
        }
        if (!MICROSERVICIOS.contains(request.getMicroservicio())) {
            throw new BusinessRuleException("Microservicio no válido. Debe ser uno de los 12 microservicios del sistema");
        }
        if (request.getEstado() == null) {
            throw new BusinessRuleException("El estado es obligatorio");
        }
        if (!ESTADOS.contains(request.getEstado())) {
            throw new BusinessRuleException("Estado no válido. Valores permitidos: UP, DEGRADED, DOWN");
        }
        if (request.getLatenciaMs() != null && request.getLatenciaMs() < 0) {
            throw new BusinessRuleException("La latencia no puede ser negativa");
        }
        if (request.getUsoCpu() != null && (request.getUsoCpu() < 0 || request.getUsoCpu() > 100)) {
            throw new BusinessRuleException("El uso de CPU debe estar entre 0 y 100");
        }
        if (request.getUsoMemoria() != null && (request.getUsoMemoria() < 0 || request.getUsoMemoria() > 100)) {
            throw new BusinessRuleException("El uso de memoria debe estar entre 0 y 100");
        }
        request.setUltimoChequeo(LocalDateTime.now(ZoneOffset.UTC));
        return monitorRepository.save(request);
    }

    @Transactional(readOnly = true)
    public List<MonitorSistema> historial(String microservicio) {
        return monitorRepository.findByMicroservicioOrderByUltimoChequeoDesc(microservicio);
    }
}
