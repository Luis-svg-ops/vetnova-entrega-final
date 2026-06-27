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

@Service
public class EvolucionService {

    private static final Logger log = LoggerFactory.getLogger(EvolucionService.class);

    @Autowired
    private EvolucionRepository evolucionRepository;

    @Autowired
    private FichaClinicaRepository fichaClinicaRepository;

    @Autowired
    private AgendaClient agendaClient;

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
                    if (!citaMascotaId.equals(ficha.getMascotaId())) {
                        throw new BusinessRuleException(
                                "La cita pertenece a una mascota distinta a la de la ficha clínica");
                    }
                }
            } catch (BusinessRuleException ex) {
                throw ex;
            } catch (Exception e) {
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
        evolucion.setFechaRegistro(LocalDateTime.now(ZoneOffset.UTC));
        return evolucionRepository.save(evolucion);
    }

    public List<Evolucion> listarPorFicha(Long fichaId) {
        return evolucionRepository.findByFichaIdOrderByFechaRegistroAsc(fichaId);
    }

    public List<Evolucion> listar() {
        return evolucionRepository.findAll();
    }
}