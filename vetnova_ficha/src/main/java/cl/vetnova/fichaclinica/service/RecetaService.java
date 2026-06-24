package cl.vetnova.fichaclinica.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.fichaclinica.dto.MedicamentoRequest;
import cl.vetnova.fichaclinica.dto.RecetaRequest;
import cl.vetnova.fichaclinica.exception.BusinessRuleException;
import cl.vetnova.fichaclinica.exception.ResourceNotFoundException;
import cl.vetnova.fichaclinica.model.Receta;
import cl.vetnova.fichaclinica.repository.FichaClinicaRepository;
import cl.vetnova.fichaclinica.repository.RecetaRepository;

@Service
public class RecetaService {

    @Autowired
    private RecetaRepository recetaRepository;

    @Autowired
    private FichaClinicaRepository fichaClinicaRepository;

    // CA-REC-01..13: emite una receta con una lista de medicamentos válidos.
    public Receta crear(RecetaRequest request) {
        if (request.getFichaId() == null) {
            throw new BusinessRuleException("El fichaId es obligatorio");
        }
        if (!fichaClinicaRepository.existsById(request.getFichaId())) {
            throw new ResourceNotFoundException("Ficha clínica no encontrada");
        }
        if (request.getVeterinarioId() == null) {
            throw new BusinessRuleException("El veterinarioId es obligatorio");
        }
        if (request.getMedicamentos() == null) {
            throw new BusinessRuleException("La lista de medicamentos es obligatoria");
        }
        if (request.getMedicamentos().isEmpty()) {
            throw new BusinessRuleException("La receta debe tener al menos un medicamento");
        }
        for (MedicamentoRequest medicamento : request.getMedicamentos()) {
            if (medicamento.getNombre() == null) {
                throw new BusinessRuleException("El nombre del medicamento es obligatorio");
            }
            if (medicamento.getDosis() == null) {
                throw new BusinessRuleException("La dosis es obligatoria para cada medicamento");
            }
            if (medicamento.getFrecuencia() == null) {
                throw new BusinessRuleException("La frecuencia es obligatoria para cada medicamento");
            }
        }
        Date emision = Date.valueOf(LocalDate.now());
        if (request.getFechaVencimiento() != null && request.getFechaVencimiento().before(emision)) {
            throw new BusinessRuleException("La fecha de vencimiento debe ser posterior a la fecha de emisión");
        }
        MedicamentoRequest primero = request.getMedicamentos().get(0);
        Receta receta = new Receta();
        receta.setFichaId(request.getFichaId());
        receta.setVeterinarioId(request.getVeterinarioId());
        receta.setMedicamentos(request.getMedicamentos().stream()
                .map(MedicamentoRequest::getNombre).collect(Collectors.joining(", ")));
        receta.setDosis(primero.getDosis());
        receta.setFrecuencia(primero.getFrecuencia());
        receta.setFechaEmision(emision);
        receta.setFechaVencimiento(request.getFechaVencimiento());
        return recetaRepository.save(receta);
    }

    // CA-REC-17: listado de recetas de una ficha por fecha de emisión descendente.
    public List<Receta> listarPorFicha(Long fichaId) {
        return recetaRepository.findByFichaIdOrderByFechaEmisionDesc(fichaId);
    }

    public List<Receta> listar() {
        return recetaRepository.findAll();
    }
}
