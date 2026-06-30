package cl.vetnova.fichaclinica.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.fichaclinica.client.AgendaClient;
import cl.vetnova.fichaclinica.exception.BusinessRuleException;
import cl.vetnova.fichaclinica.exception.ResourceNotFoundException;
import cl.vetnova.fichaclinica.model.Evolucion;
import cl.vetnova.fichaclinica.repository.EvolucionRepository;
import cl.vetnova.fichaclinica.repository.FichaClinicaRepository;

/**
 * Servicio de negocio para evoluciones clínicas: valida que la cita corresponda a la mascota correcta.
 * Consulta vetnova_agenda (AgendaClient) para validar la cita; si el servicio no responde, continúa igual.
 */
@Service
public class EvolucionService {

    private static final Logger log = LoggerFactory.getLogger(EvolucionService.class);

    @Autowired
    private EvolucionRepository evolucionRepository;

    @Autowired
    private FichaClinicaRepository fichaClinicaRepository;

    // AgendaClient llama al microservicio vetnova_agenda (puerto 8086) para validar la cita
    @Autowired
    private AgendaClient agendaClient;

    /**
     * Registra una evolución clínica validando que la cita pertenezca a la mascota de la ficha.
     * Si vetnova_agenda no está disponible, la evolución se guarda igual (tolerancia a fallos).
     */
    public Evolucion crear(Evolucion evolucion) {
        if (evolucion.getFichaId() == null) {
            throw new BusinessRuleException("El fichaId es obligatorio");
        }
        if (!fichaClinicaRepository.existsById(evolucion.getFichaId())) {
            throw new ResourceNotFoundException("Ficha clínica no encontrada");
        }
        if (evolucion.getVeterinarioId() == null) {
            throw new BusinessRuleException("El veterinarioId es obligatorio");
        }
        if (evolucion.getCitaId() == null) {
            throw new BusinessRuleException("El citaId es obligatorio para registrar una evolución");
        }
        if (evolucion.getCitaId() != null) {
            try {
                java.util.Map<Object, Object> citaData = agendaClient.obtenerCita(evolucion.getCitaId());
                if (citaData != null && citaData.get("mascotaId") != null) {
                    Long citaMascotaId = ((Number) citaData.get("mascotaId")).longValue();
                    cl.vetnova.fichaclinica.model.FichaClinica ficha =
                            fichaClinicaRepository.findById(evolucion.getFichaId()).orElseThrow();
                    // Validación cruzada: la cita debe ser de la misma mascota que la ficha clínica
                    if (!citaMascotaId.equals(ficha.getMascotaId())) {
                        throw new BusinessRuleException(
                                "La cita pertenece a una mascota distinta a la de la ficha clínica");
                    }
                }
            } catch (BusinessRuleException ex) {
                // Re-lanzar errores de regla de negocio; no swallowear validaciones críticas
                throw ex;
            } catch (Exception e) {
                // Si vetnova_agenda no responde, se registra warning pero la evolución se guarda igual
                log.warn("event=agenda_no_disponible citaId={} — evolución creada sin validar cita: {}",
                        evolucion.getCitaId(), e.getMessage());
            }
        }
        if (evolucion.getDescripcion() == null) {
            throw new BusinessRuleException("La descripción es obligatoria");
        }
        if (evolucion.getDescripcion().isBlank()) {
            throw new BusinessRuleException("La descripción no puede estar vacía");
        }
        // Se usa UTC para consistencia entre todos los microservicios del sistema
        evolucion.setFechaRegistro(LocalDateTime.now(ZoneOffset.UTC));
        return evolucionRepository.save(evolucion);
    }

    /**
     * Retorna las evoluciones de una ficha clínica ordenadas de más antigua a más reciente.
     * @param fichaId ID de la ficha cuyas evoluciones se quieren consultar
     */
    public List<Evolucion> listarPorFicha(Long fichaId) {
        return evolucionRepository.findByFichaIdOrderByFechaRegistroAsc(fichaId);
    }

    /**
     * Retorna todas las evoluciones registradas en el sistema.
     */
    public List<Evolucion> listar() {
        return evolucionRepository.findAll();
    }
}