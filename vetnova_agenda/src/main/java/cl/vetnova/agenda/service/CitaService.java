package cl.vetnova.agenda.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import cl.vetnova.agenda.exception.BusinessRuleException;
import cl.vetnova.agenda.exception.ConflictException;
import cl.vetnova.agenda.exception.ResourceNotFoundException;
import cl.vetnova.agenda.model.Cita;
import cl.vetnova.agenda.repository.CitaRepository;

@Service
public class CitaService {

    private static final Set<String> SUCURSALES = Set.of("SANTIAGO", "CHILLAN", "TALCA", "LOS_ANGELES");
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
private RestTemplate restTemplate;

    public List<Cita> listar() {
        return citaRepository.findAll();
    }

    public Cita obtenerPorId(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id " + id));
    }

    public Cita crear(Cita cita) {
        if (cita.getClienteId() == null) {
            throw new BusinessRuleException("El clienteId es obligatorio");
        }
        try {
            String urlCliente = "http://localhost:8081/api/usuarios/" + cita.getClienteId() + "/existe";
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> resp = restTemplate.getForObject(urlCliente, java.util.Map.class);
            if (resp == null || !Boolean.TRUE.equals(resp.get("existe"))) {
                throw new ResourceNotFoundException("Cliente no encontrado en el sistema");
            }
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ResourceNotFoundException("No se pudo verificar el cliente en el sistema");
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
        if (cita.getMascotaId() != null) {
            try {
                String url = "http://localhost:8087/api/v1/mascotas/" + cita.getMascotaId();
                restTemplate.getForObject(url, Object.class);
            } catch (Exception e) {
                throw new ResourceNotFoundException("Mascota no encontrada en el sistema");
            }
        }
        if (cita.getSucursal() == null) {
            throw new BusinessRuleException("La sucursal es obligatoria");
        }
        if (!SUCURSALES.contains(cita.getSucursal())) {
            throw new ResourceNotFoundException("Sucursal no encontrada");
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
