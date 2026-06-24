package cl.vetnova.fichaclinica.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.fichaclinica.exception.BusinessRuleException;
import cl.vetnova.fichaclinica.exception.ResourceNotFoundException;
import cl.vetnova.fichaclinica.model.Procedimiento;
import cl.vetnova.fichaclinica.repository.FichaClinicaRepository;
import cl.vetnova.fichaclinica.repository.ProcedimientoRepository;

@Service
public class ProcedimientoService {

    @Autowired
    private ProcedimientoRepository procedimientoRepository;

    @Autowired
    private FichaClinicaRepository fichaClinicaRepository;

    // CA-PRO-01..10: registra un procedimiento en una ficha existente.
    public Procedimiento crear(Procedimiento procedimiento) {
        if (procedimiento.getFichaId() == null) {
            throw new BusinessRuleException("El fichaId es obligatorio");
        }
        if (!fichaClinicaRepository.existsById(procedimiento.getFichaId())) {
            throw new ResourceNotFoundException("Ficha clínica no encontrada");
        }
        if (procedimiento.getNombre() == null) {
            throw new BusinessRuleException("El nombre del procedimiento es obligatorio");
        }
        if (procedimiento.getNombre().isBlank()) {
            throw new BusinessRuleException("El nombre del procedimiento no puede estar vacío");
        }
        if (procedimiento.getDescripcion() == null) {
            throw new BusinessRuleException("La descripción es obligatoria");
        }
        if (procedimiento.getDescripcion().isBlank()) {
            throw new BusinessRuleException("La descripción no puede estar vacía");
        }
        if (procedimiento.getVeterinarioId() == null) {
            throw new BusinessRuleException("El veterinarioId es obligatorio");
        }
        procedimiento.setFechaRegistro(LocalDateTime.now(ZoneOffset.UTC));
        return procedimientoRepository.save(procedimiento);
    }

    // CA-PRO-14/15: listado de procedimientos de una ficha por fecha de registro.
    public List<Procedimiento> listarPorFicha(Long fichaId) {
        return procedimientoRepository.findByFichaIdOrderByFechaRegistroAsc(fichaId);
    }

    public List<Procedimiento> listar() {
        return procedimientoRepository.findAll();
    }
}
