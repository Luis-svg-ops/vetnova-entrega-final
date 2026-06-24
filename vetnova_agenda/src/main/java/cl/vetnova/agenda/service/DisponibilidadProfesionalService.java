package cl.vetnova.agenda.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.agenda.exception.BusinessRuleException;
import cl.vetnova.agenda.exception.ConflictException;
import cl.vetnova.agenda.exception.ResourceNotFoundException;
import cl.vetnova.agenda.model.DisponibilidadProfesional;
import cl.vetnova.agenda.repository.CitaRepository;
import cl.vetnova.agenda.repository.DisponibilidadProfesionalRepository;

@Service
public class DisponibilidadProfesionalService {

    private static final Set<String> DIAS = Set.of("LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO");
    private static final Set<String> SUCURSALES = Set.of("SANTIAGO", "CHILLAN", "TALCA", "LOS_ANGELES");
    private static final Pattern HORA = Pattern.compile("^([01][0-9]|2[0-3]):[0-5][0-9]$");

    @Autowired
    private DisponibilidadProfesionalRepository disponibilidadRepository;

    @Autowired
    private CitaRepository citaRepository;

    public List<DisponibilidadProfesional> listar() {
        return disponibilidadRepository.findAll();
    }

    public DisponibilidadProfesional obtenerPorId(Long id) {
        return disponibilidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidad no encontrada con id " + id));
    }

    public DisponibilidadProfesional crear(DisponibilidadProfesional disponibilidad) {
        if (disponibilidad.getVeterinarioId() == null) {
            throw new BusinessRuleException("El veterinarioId es obligatorio");
        }
        if (disponibilidad.getDiaSemana() == null) {
            throw new BusinessRuleException("El día de la semana es obligatorio");
        }
        if (!DIAS.contains(disponibilidad.getDiaSemana())) {
            throw new BusinessRuleException("Día no válido. Valores: LUNES-DOMINGO");
        }
        if (disponibilidad.getHoraInicio() == null) {
            throw new BusinessRuleException("La hora de inicio es obligatoria");
        }
        if (!HORA.matcher(disponibilidad.getHoraInicio()).matches()) {
            throw new BusinessRuleException("Formato inválido. Use HH:mm");
        }
        if (disponibilidad.getHoraFin() == null) {
            throw new BusinessRuleException("La hora de fin es obligatoria");
        }
        if (!HORA.matcher(disponibilidad.getHoraFin()).matches()) {
            throw new BusinessRuleException("Formato inválido. Use HH:mm");
        }
        LocalTime inicio = LocalTime.parse(disponibilidad.getHoraInicio());
        LocalTime fin = LocalTime.parse(disponibilidad.getHoraFin());
        if (fin.isBefore(inicio)) {
            throw new BusinessRuleException("La hora de fin debe ser posterior a la de inicio");
        }
        if (fin.equals(inicio)) {
            throw new BusinessRuleException("La hora de fin debe ser posterior");
        }
        if (disponibilidad.getSucursal() == null) {
            throw new BusinessRuleException("La sucursal es obligatoria");
        }
        if (!SUCURSALES.contains(disponibilidad.getSucursal())) {
            throw new ResourceNotFoundException("Sucursal no encontrada");
        }
        if (disponibilidadRepository.existsByVeterinarioIdAndDiaSemanaAndSucursal(
                disponibilidad.getVeterinarioId(), disponibilidad.getDiaSemana(), disponibilidad.getSucursal())) {
            throw new ConflictException("Ya existe horario para ese día y sucursal");
        }
        disponibilidad.setActiva(true);
        return disponibilidadRepository.save(disponibilidad);
    }

    public DisponibilidadProfesional actualizar(Long id, DisponibilidadProfesional datos) {
        DisponibilidadProfesional existente = obtenerPorId(id);
        LocalDateTime ahora = LocalDateTime.now();
        if (citaRepository.existsByVeterinarioIdAndFechaHoraBetween(
                existente.getVeterinarioId(), ahora, ahora.plusDays(7))) {
            throw new BusinessRuleException("No se puede modificar con citas en los próximos 7 días");
        }
        existente.setDiaSemana(datos.getDiaSemana());
        existente.setHoraInicio(datos.getHoraInicio());
        existente.setHoraFin(datos.getHoraFin());
        existente.setSucursal(datos.getSucursal());
        return disponibilidadRepository.save(existente);
    }

    public DisponibilidadProfesional activar(Long id) {
        DisponibilidadProfesional disponibilidad = obtenerPorId(id);
        disponibilidad.setActiva(true);
        return disponibilidadRepository.save(disponibilidad);
    }

    public DisponibilidadProfesional desactivar(Long id) {
        DisponibilidadProfesional disponibilidad = obtenerPorId(id);
        disponibilidad.setActiva(false);
        return disponibilidadRepository.save(disponibilidad);
    }

    public void eliminar(Long id) {
        DisponibilidadProfesional existente = obtenerPorId(id);
        if (citaRepository.existsByVeterinarioIdAndFechaHoraAfter(existente.getVeterinarioId(), LocalDateTime.now())) {
            throw new BusinessRuleException("No se puede eliminar con citas futuras");
        }
        disponibilidadRepository.deleteById(id);
    }
}
