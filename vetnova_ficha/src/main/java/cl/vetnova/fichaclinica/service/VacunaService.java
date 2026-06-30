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

/**
 * Servicio de negocio para vacunas: valida fechas, evita duplicados y asegura que la ficha exista.
 * Las vacunas son registros inmutables una vez persistidas en la base de datos.
 */
@Service
public class VacunaService {

    @Autowired
    private VacunaRepository vacunaRepository;

    @Autowired
    private FichaClinicaRepository fichaClinicaRepository;

    // CA-VAC-01..09, 16: registra una vacuna en una ficha existente.
    /**
     * Registra una vacuna validando fechas y previniendo duplicados (misma ficha, nombre y fecha).
     * @param vacuna datos de la vacuna (fichaId, nombre y fechaAplicacion son obligatorios)
     */
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
        // No se puede registrar una vacuna con fecha futura (debe haberse aplicado ya)
        if (vacuna.getFechaAplicacion().after(Date.valueOf(LocalDate.now()))) {
            throw new BusinessRuleException("La fecha de aplicación no puede ser futura");
        }
        // La próxima dosis siempre debe ser posterior a la aplicación actual
        if (vacuna.getFechaProximaDosis() != null
                && vacuna.getFechaProximaDosis().before(vacuna.getFechaAplicacion())) {
            throw new BusinessRuleException("La próxima dosis debe ser posterior a la fecha de aplicación");
        }
        // Previene duplicados: misma vacuna, misma fecha, en la misma ficha
        if (vacunaRepository.existsByFichaIdAndNombreAndFechaAplicacion(
                vacuna.getFichaId(), vacuna.getNombre(), vacuna.getFechaAplicacion())) {
            throw new ConflictException("Ya existe un registro de esa vacuna para esa fecha en esta ficha");
        }
        return vacunaRepository.save(vacuna);
    }

    // CA-VAC-13/14: listado de vacunas de una ficha ordenado por fecha de aplicación.
    /**
     * Retorna las vacunas de una ficha clínica ordenadas de más antigua a más reciente.
     * @param fichaId ID de la ficha cuyas vacunas se quieren consultar
     */
    public List<Vacuna> listarPorFicha(Long fichaId) {
        return vacunaRepository.findByFichaIdOrderByFechaAplicacionAsc(fichaId);
    }

    /**
     * Retorna todas las vacunas registradas en el sistema.
     */
    public List<Vacuna> listar() {
        return vacunaRepository.findAll();
    }
}
