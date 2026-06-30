package cl.vetnova.agenda.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.agenda.exception.BusinessRuleException;
import cl.vetnova.agenda.exception.ConflictException;
import cl.vetnova.agenda.exception.ResourceNotFoundException;
import cl.vetnova.agenda.model.BloqueAgenda;
import cl.vetnova.agenda.repository.BloqueAgendaRepository;
import cl.vetnova.agenda.repository.CitaRepository;

// Gestiona bloqueos de agenda (vacaciones, licencias, reuniones) que impiden crear citas en ese período
@Service
public class BloqueAgendaService {

    // Solo se verifica conflicto con citas "confirmadas"; las pendientes pueden cancelarse fácilmente
    private static final String CONFIRMADA = "confirmada";

    @Autowired
    private BloqueAgendaRepository bloqueAgendaRepository;

    @Autowired
    private CitaRepository citaRepository;

    public List<BloqueAgenda> listar() {
        return bloqueAgendaRepository.findAll();
    }

    public BloqueAgenda obtenerPorId(Long id) {
        return bloqueAgendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bloqueo no encontrado con id " + id));
    }

    public BloqueAgenda crear(BloqueAgenda bloque) {
        if (bloque.getVeterinarioId() == null) {
            throw new BusinessRuleException("El veterinarioId es obligatorio");
        }
        if (bloque.getFechaInicio() == null) {
            throw new BusinessRuleException("La fecha de inicio es obligatoria");
        }
        if (bloque.getFechaInicio().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("La fecha de inicio no puede ser en el pasado");
        }
        if (bloque.getFechaFin() == null) {
            throw new BusinessRuleException("La fecha de fin es obligatoria");
        }
        if (bloque.getFechaFin().isBefore(bloque.getFechaInicio())) {
            throw new BusinessRuleException("La fecha de fin debe ser posterior a la de inicio");
        }
        if (bloque.getFechaFin().isEqual(bloque.getFechaInicio())) {
            throw new BusinessRuleException("La fecha de fin debe ser posterior");
        }
        if (bloque.getMotivo() == null) {
            throw new BusinessRuleException("El motivo es obligatorio");
        }
        if (bloque.getMotivo().isBlank()) {
            throw new BusinessRuleException("El motivo no puede estar vacío");
        }
        if (bloque.getCreadoPor() == null) {
            throw new BusinessRuleException("El creador es obligatorio");
        }
        // Validación dura: no se puede bloquear si ya hay citas confirmadas en ese período
        if (citaRepository.existsByVeterinarioIdAndEstadoAndFechaHoraBetween(
                bloque.getVeterinarioId(), CONFIRMADA, bloque.getFechaInicio(), bloque.getFechaFin())) {
            throw new ConflictException("Existen citas en el período. Cancele o reagende primero");
        }
        if (haySolapamientoConBloqueos(bloque)) {
            throw new ConflictException("Ya existe bloqueo en ese período");
        }
        return bloqueAgendaRepository.save(bloque);
    }

    // No permite eliminar un bloqueo que esté actualmente en curso
    public void eliminar(Long id) {
        BloqueAgenda bloque = obtenerPorId(id);
        LocalDateTime ahora = LocalDateTime.now();
        // Un bloque está "en curso" si la hora actual cae entre fechaInicio y fechaFin
        if (bloque.getFechaInicio().isBefore(ahora) && bloque.getFechaFin().isAfter(ahora)) {
            throw new BusinessRuleException("No se puede eliminar bloqueo en curso");
        }
        bloqueAgendaRepository.deleteById(id);
    }

    // Fórmula clásica de solapamiento de intervalos: A.inicio < B.fin && B.inicio < A.fin
    private boolean haySolapamientoConBloqueos(BloqueAgenda nuevo) {
        for (BloqueAgenda existente : bloqueAgendaRepository.findByVeterinarioId(nuevo.getVeterinarioId())) {
            if (nuevo.getFechaInicio().isBefore(existente.getFechaFin())
                    && existente.getFechaInicio().isBefore(nuevo.getFechaFin())) {
                return true;
            }
        }
        return false;
    }
}
