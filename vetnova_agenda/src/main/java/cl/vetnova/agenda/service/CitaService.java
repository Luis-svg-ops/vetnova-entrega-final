package cl.vetnova.agenda.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.agenda.client.AuthClient;
import cl.vetnova.agenda.client.FichaClient;
import cl.vetnova.agenda.dto.CitaResponse;
import cl.vetnova.agenda.exception.BusinessRuleException;
import cl.vetnova.agenda.exception.ConflictException;
import cl.vetnova.agenda.exception.ResourceNotFoundException;
import cl.vetnova.agenda.model.Cita;
import cl.vetnova.agenda.repository.CitaRepository;

@Service
public class CitaService {

    private static final Logger log = LoggerFactory.getLogger(CitaService.class);

    private static final Set<String> SUCURSALES = Set.of("CHILLAN", "LOS_ANGELES", "TALCA", "SANTIAGO");
    private static final int DURACION_POR_DEFECTO = 30;
    private static final String PENDIENTE = "pendiente";
    private static final String CONFIRMADA = "confirmada";
    private static final String EN_CURSO = "en curso";
    private static final String COMPLETADA = "completada";
    private static final String CANCELADA = "cancelada";

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private RecordatorioGenerador recordatorioGenerador;

    @Autowired
    private AuthClient authClient;

    @Autowired
    private FichaClient fichaClient;

    public List<Cita> listar() {
        return citaRepository.findAll();
    }

    public List<CitaResponse> listarConNombres() {
        return citaRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<Cita> agendaDelDia(LocalDateTime fecha) {
        LocalDateTime inicio = fecha.toLocalDate().atStartOfDay();
        LocalDateTime fin = inicio.plusDays(1).minusSeconds(1);
        return citaRepository.findByFechaHoraBetweenOrderByFechaHoraAsc(inicio, fin);
    }

    public List<CitaResponse> agendaDelDiaConNombres(LocalDateTime fecha) {
        return agendaDelDia(fecha).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Cita obtenerPorId(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id " + id));
    }

    public CitaResponse obtenerConNombres(Long id) {
        return toResponse(obtenerPorId(id));
    }

    public CitaResponse toResponse(Cita cita) {
        String nombreCliente = null;
        String nombreMascota = null;
        String nombreVeterinario = null;
        try {
            nombreCliente = authClient.obtenerNombre(cita.getClienteId());
        } catch (Exception e) {
            log.warn("event=auth_no_disponible clienteId={} — degradación suave en toResponse", cita.getClienteId());
        }
        if (cita.getMascotaId() != null) {
            try {
                nombreMascota = fichaClient.obtenerNombreMascota(cita.getMascotaId());
            } catch (Exception e) {
                log.warn("event=ficha_no_disponible mascotaId={} — degradación suave en toResponse", cita.getMascotaId());
            }
        }
        try {
            nombreVeterinario = authClient.obtenerNombre(cita.getVeterinarioId());
        } catch (Exception e) {
            log.warn("event=auth_no_disponible veterinarioId={} — degradación suave en toResponse", cita.getVeterinarioId());
        }
        return new CitaResponse(cita, nombreCliente, nombreMascota, nombreVeterinario);
    }

    public Cita reprogramar(Long id, LocalDateTime nuevaFecha, Integer nuevaDuracion) {
        Cita cita = obtenerPorId(id);
        if (COMPLETADA.equals(cita.getEstado()) || CANCELADA.equals(cita.getEstado())) {
            throw new BusinessRuleException("No se puede reprogramar una cita " + cita.getEstado());
        }
        if (nuevaFecha == null) {
            throw new BusinessRuleException("La nueva fecha y hora son obligatorias");
        }
        if (!nuevaFecha.isAfter(LocalDateTime.now())) {
            throw new BusinessRuleException("La nueva fecha y hora deben ser futuras");
        }
        Cita temporal = new Cita();
        temporal.setId(id);
        temporal.setVeterinarioId(cita.getVeterinarioId());
        temporal.setFechaHora(nuevaFecha);
        temporal.setDuracionMinutos(nuevaDuracion != null ? nuevaDuracion : cita.getDuracionMinutos());
        if (haySolapamiento(temporal)) {
            throw new ConflictException("El veterinario no está disponible en ese horario");
        }
        cita.setFechaHora(nuevaFecha);
        if (nuevaDuracion != null) {
            cita.setDuracionMinutos(nuevaDuracion);
        }
        return citaRepository.save(cita);
    }

    public Cita crear(Cita cita) {
        // Validaciones de nulos primero (sin llamadas remotas)
        if (cita.getClienteId() == null) {
            throw new BusinessRuleException("El clienteId es obligatorio");
        }
        if (cita.getVeterinarioId() == null) {
            throw new BusinessRuleException("El veterinarioId es obligatorio");
        }
        if (cita.getServicioId() == null) {
            throw new BusinessRuleException("El servicioId es obligatorio");
        }
        if (cita.getFechaHora() == null) {
            throw new BusinessRuleException("La fecha y hora son obligatorias");
        }
        if (!cita.getFechaHora().isAfter(LocalDateTime.now())) {
            throw new BusinessRuleException("La fecha y hora deben ser futuras");
        }
        if (cita.getSucursal() == null) {
            throw new BusinessRuleException("La sucursal es obligatoria");
        }
        if (!SUCURSALES.contains(cita.getSucursal())) {
            throw new ResourceNotFoundException("Sucursal no encontrada");
        }
        // Validaciones remotas (hard) después de todos los null checks
        authClient.verificarCliente(cita.getClienteId());
        if (cita.getMascotaId() != null) {
            fichaClient.verificarMascota(cita.getMascotaId());
        }
        if (haySolapamiento(cita)) {
            throw new ConflictException("El veterinario no está disponible en ese horario");
        }
        if (cita.getBoxId() != null && hayBoxOcupado(cita)) {
            throw new ConflictException("El box ya está ocupado en ese horario");
        }
        cita.setEstado(PENDIENTE);
        cita.setFechaCreacion(LocalDateTime.now());
        Cita guardada = citaRepository.save(cita);
        recordatorioGenerador.generarParaCita(guardada);
        return guardada;
    }

    public Cita confirmar(Long id) {
        Cita cita = obtenerPorId(id);
        if (CANCELADA.equals(cita.getEstado())) {
            throw new BusinessRuleException("No se puede confirmar cita cancelada");
        }
        cita.setEstado(CONFIRMADA);
        return citaRepository.save(cita);
    }

    public Cita iniciar(Long id) {
        Cita cita = obtenerPorId(id);
        if (!CONFIRMADA.equals(cita.getEstado())) {
            throw new BusinessRuleException("Debe estar confirmada antes de iniciarse");
        }
        cita.setEstado(EN_CURSO);
        return citaRepository.save(cita);
    }

    public Cita completar(Long id) {
        Cita cita = obtenerPorId(id);
        if (!EN_CURSO.equals(cita.getEstado())) {
            throw new BusinessRuleException("Debe estar en curso antes de completarse");
        }
        cita.setEstado(COMPLETADA);
        return citaRepository.save(cita);
    }

    public Cita cancelar(Long id, String motivo) {
        Cita cita = obtenerPorId(id);
        if (COMPLETADA.equals(cita.getEstado())) {
            throw new BusinessRuleException("No se puede cancelar cita completada");
        }
        if (motivo == null) {
            throw new BusinessRuleException("El motivo es obligatorio");
        }
        cita.setEstado(CANCELADA);
        cita.setMotivoCancelacion(motivo);
        Cita cancelada = citaRepository.save(cita);
        recordatorioGenerador.cancelarPorCita(cancelada.getId());
        return cancelada;
    }

    private boolean haySolapamiento(Cita nueva) {
        LocalDateTime inicioNueva = nueva.getFechaHora();
        LocalDateTime finNueva = inicioNueva.plusMinutes(duracion(nueva));
        List<Cita> ocupadas = new java.util.ArrayList<>();
        ocupadas.addAll(citaRepository.findByVeterinarioIdAndEstado(nueva.getVeterinarioId(), PENDIENTE));
        ocupadas.addAll(citaRepository.findByVeterinarioIdAndEstado(nueva.getVeterinarioId(), CONFIRMADA));
        for (Cita existente : ocupadas) {
            LocalDateTime inicioExistente = existente.getFechaHora();
            LocalDateTime finExistente = inicioExistente.plusMinutes(duracion(existente));
            if (inicioNueva.isBefore(finExistente) && inicioExistente.isBefore(finNueva)) {
                return true;
            }
        }
        return false;
    }

    private boolean hayBoxOcupado(Cita nueva) {
        LocalDateTime inicioNueva = nueva.getFechaHora();
        LocalDateTime finNueva = inicioNueva.plusMinutes(duracion(nueva));
        List<Cita> ocupadas = new java.util.ArrayList<>();
        ocupadas.addAll(citaRepository.findByBoxIdAndEstado(nueva.getBoxId(), PENDIENTE));
        ocupadas.addAll(citaRepository.findByBoxIdAndEstado(nueva.getBoxId(), CONFIRMADA));
        for (Cita existente : ocupadas) {
            LocalDateTime inicioExistente = existente.getFechaHora();
            LocalDateTime finExistente = inicioExistente.plusMinutes(duracion(existente));
            if (inicioNueva.isBefore(finExistente) && inicioExistente.isBefore(finNueva)) {
                return true;
            }
        }
        return false;
    }

    private int duracion(Cita cita) {
        return cita.getDuracionMinutos() == null ? DURACION_POR_DEFECTO : cita.getDuracionMinutos();
    }
}
