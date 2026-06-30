package cl.vetnova.agenda.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.agenda.dto.RecordatorioRequest;
import cl.vetnova.agenda.exception.BusinessRuleException;
import cl.vetnova.agenda.exception.ConflictException;
import cl.vetnova.agenda.exception.ResourceNotFoundException;
import cl.vetnova.agenda.model.Cita;
import cl.vetnova.agenda.model.Recordatorio;
import cl.vetnova.agenda.repository.CitaRepository;
import cl.vetnova.agenda.repository.RecordatorioRepository;

// Gestiona recordatorios de citas; implementa RecordatorioGenerador para ser invocado desde CitaService
@Service
public class RecordatorioService implements RecordatorioGenerador {

    // Tipos de canal soportados
    private static final Set<String> TIPOS = Set.of("EMAIL", "SMS", "PUSH");
    private static final String EMAIL = "EMAIL";

    @Autowired
    private RecordatorioRepository recordatorioRepository;

    @Autowired
    private CitaRepository citaRepository;

    public List<Recordatorio> listar() {
        return recordatorioRepository.findAll();
    }

    public Recordatorio obtenerPorId(Long id) {
        return recordatorioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recordatorio no encontrado con id " + id));
    }

    // Crea recordatorio manual; no permite duplicar el mismo tipo para la misma cita
    public Recordatorio crear(RecordatorioRequest request) {
        if (request.citaId() == null) {
            throw new BusinessRuleException("El citaId es obligatorio");
        }
        Cita cita = citaRepository.findById(request.citaId())
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));
        if (request.tipo() == null) {
            throw new BusinessRuleException("El tipo es obligatorio");
        }
        if (!TIPOS.contains(request.tipo())) {
            throw new BusinessRuleException("Tipo no válido. Valores: EMAIL, SMS, PUSH");
        }
        // Evita duplicados: una cita no puede tener dos recordatorios del mismo canal
        if (recordatorioRepository.existsByCitaIdAndTipo(request.citaId(), request.tipo())) {
            throw new ConflictException("Ya existe recordatorio de ese tipo para esta cita");
        }
        return recordatorioRepository.save(construir(cita, request.tipo()));
    }

    // Simula que la notificación fue despachada
    public Recordatorio reenviar(Long id) {
        Recordatorio recordatorio = obtenerPorId(id);
        recordatorio.setEnviado(true);
        return recordatorioRepository.save(recordatorio);
    }

    // Llamado automáticamente desde CitaService.crear(); idempotente — no duplica si ya existe EMAIL
    @Override
    public void generarParaCita(Cita cita) {
        if (recordatorioRepository.existsByCitaIdAndTipo(cita.getId(), EMAIL)) {
            return;
        }
        recordatorioRepository.save(construir(cita, EMAIL));
    }

    // Llamado automáticamente desde CitaService.cancelar(); solo cancela los no enviados
    @Override
    public void cancelarPorCita(Long citaId) {
        for (Recordatorio recordatorio : recordatorioRepository.findByCitaId(citaId)) {
            // Los ya enviados no se modifican — la notificación ya fue despachada
            if (!Boolean.TRUE.equals(recordatorio.getEnviado())) {
                recordatorio.setCancelado(true);
                recordatorioRepository.save(recordatorio);
            }
        }
    }

    // Construye el recordatorio programado 24h antes de la cita; si la cita es en menos de 24h, envío inmediato
    private Recordatorio construir(Cita cita, String tipo) {
        LocalDateTime envio = cita.getFechaHora().minusHours(24);
        if (envio.isBefore(LocalDateTime.now())) {
            envio = LocalDateTime.now();
        }
        Recordatorio recordatorio = new Recordatorio();
        recordatorio.setCitaId(cita.getId());
        recordatorio.setTipo(tipo);
        recordatorio.setFechaEnvio(envio);
        recordatorio.setMensaje("Recordatorio de cita veterinaria para el " + cita.getFechaHora()
                + " con veterinario " + cita.getVeterinarioId() + " en " + cita.getSucursal());
        recordatorio.setEnviado(false);
        recordatorio.setCancelado(false);
        return recordatorio;
    }
}
