package cl.vetnova.fichaclinica.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.fichaclinica.exception.BusinessRuleException;
import cl.vetnova.fichaclinica.exception.ConflictException;
import cl.vetnova.fichaclinica.exception.ResourceNotFoundException;
import cl.vetnova.fichaclinica.model.Vacuna;
import cl.vetnova.fichaclinica.repository.FichaClinicaRepository;
import cl.vetnova.fichaclinica.repository.VacunaRepository;

@Service
public class VacunaService {

    @Autowired
    private VacunaRepository vacunaRepository;

    @Autowired
    private FichaClinicaRepository fichaClinicaRepository;

    // CA-VAC-01..09, 16: registra una vacuna en una ficha existente.
    public Vacuna crear(Vacuna vacuna) {
        if (vacuna.getFichaId() == null) {
            throw new BusinessRuleException("El fichaId es obligatorio");
        }
        if (!fichaClinicaRepository.existsById(vacuna.getFichaId())) {
            throw new ResourceNotFoundException("Ficha clínica no encontrada");
        }
        if (vacuna.getNombre() == null) {
            throw new BusinessRuleException("El nombre de la vacuna es obligatorio");
        }
        if (vacuna.getNombre().isBlank()) {
            throw new BusinessRuleException("El nombre de la vacuna no puede estar vacío");
        }
        if (vacuna.getFechaAplicacion() == null) {
            throw new BusinessRuleException("La fecha de aplicación es obligatoria");
        }
        if (vacuna.getFechaAplicacion().after(Date.valueOf(LocalDate.now()))) {
            throw new BusinessRuleException("La fecha de aplicación no puede ser futura");
        }
        if (vacuna.getFechaProximaDosis() != null
                && vacuna.getFechaProximaDosis().before(vacuna.getFechaAplicacion())) {
            throw new BusinessRuleException("La próxima dosis debe ser posterior a la fecha de aplicación");
        }
        if (vacunaRepository.existsByFichaIdAndNombreAndFechaAplicacion(
                vacuna.getFichaId(), vacuna.getNombre(), vacuna.getFechaAplicacion())) {
            throw new ConflictException("Ya existe un registro de esa vacuna para esa fecha en esta ficha");
        }
        return vacunaRepository.save(vacuna);
    }

    // CA-VAC-13/14: listado de vacunas de una ficha ordenado por fecha de aplicación.
    public List<Vacuna> listarPorFicha(Long fichaId) {
        return vacunaRepository.findByFichaIdOrderByFechaAplicacionAsc(fichaId);
    }

    public List<Vacuna> listar() {
        return vacunaRepository.findAll();
    }
}
