package cl.vetnova.fichaclinica.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import cl.vetnova.fichaclinica.exception.BusinessRuleException;
import cl.vetnova.fichaclinica.exception.ResourceNotFoundException;
import cl.vetnova.fichaclinica.model.Evolucion;
import cl.vetnova.fichaclinica.repository.EvolucionRepository;
import cl.vetnova.fichaclinica.repository.FichaClinicaRepository;

@Service
public class EvolucionService {

    @Autowired
    private EvolucionRepository evolucionRepository;

    @Autowired
    private FichaClinicaRepository fichaClinicaRepository;

    @Autowired
    private RestTemplate restTemplate;

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
        if (evolucion.getCitaId() != null) {
            try {
                String url = "http://localhost:8086/api/v1/citas/" + evolucion.getCitaId();
                restTemplate.getForObject(url, Object.class);
            } catch (Exception e) {
                throw new BusinessRuleException("La cita no existe en el sistema");
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