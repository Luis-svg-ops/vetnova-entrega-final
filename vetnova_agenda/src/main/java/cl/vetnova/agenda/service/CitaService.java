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

/**
 * Servicio central de la agenda: implementa la máquina de estados de citas y coordina
 * las llamadas remotas a vetnova_auth (8081) y vetnova_ficha (8087).
 * Máquina de estados: PENDIENTE → CONFIRMADA → EN_CURSO → COMPLETADA (CANCELADA desde cualquier estado previo).
 */
@Service
public class CitaService {

    private static final Logger log = LoggerFactory.getLogger(CitaService.class);

    // Sucursales válidas en el sistema; centralizado para evitar errores de tipeo
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

    // Cliente HTTP hacia vetnova_auth (puerto 8081) para verificar y obtener datos de usuarios
    @Autowired
    private AuthClient authClient;

    // Cliente HTTP hacia vetnova_ficha (puerto 8087) para verificar y obtener datos de mascotas
    @Autowired
    private FichaClient fichaClient;

    /**
     * Retorna todas las citas sin enriquecer con nombres (solo ids).
     * @return lista de todas las citas
     */
    public List<Cita> listar() {
        return citaRepository.findAll();
    }

    /**
     * Retorna todas las citas enriquecidas con nombres de cliente, mascota y veterinario.
     * La obtención de nombres usa degradación suave: si auth o ficha fallan, el nombre queda null.
     * @return lista de CitaResponse con datos desnormalizados
     */
    public List<CitaResponse> listarConNombres() {
        return citaRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retorna las citas del día indicado ordenadas cronológicamente.
     * @param fecha cualquier instante del día que se quiere consultar
     * @return citas entre 00:00:00 y 23:59:59 del día de esa fecha
     */
    public List<Cita> agendaDelDia(LocalDateTime fecha) {
        LocalDateTime inicio = fecha.toLocalDate().atStartOfDay();
        LocalDateTime fin = inicio.plusDays(1).minusSeconds(1);
        return citaRepository.findByFechaHoraBetweenOrderByFechaHoraAsc(inicio, fin);
    }

    /**
     * Igual que agendaDelDia pero retorna CitaResponse con nombres resueltos.
     * @param fecha día a consultar
     * @return lista de CitaResponse ordenada por hora
     */
    public List<CitaResponse> agendaDelDiaConNombres(LocalDateTime fecha) {
        return agendaDelDia(fecha).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca una cita por id; lanza excepción 404 si no existe.
     * @param id identificador de la cita
     * @return la cita encontrada
     */
    public Cita obtenerPorId(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id " + id));
    }

    /**
     * Obtiene una cita por id y la convierte a CitaResponse con nombres resueltos.
     * @param id identificador de la cita
     * @return CitaResponse enriquecido
     */
    public CitaResponse obtenerConNombres(Long id) {
        return toResponse(obtenerPorId(id));
    }

    /**
     * Construye un CitaResponse enriqueciendo la cita con nombres desde auth y ficha.
     * Degradación suave: si cualquier servicio externo falla, el nombre queda null pero la respuesta se entrega igual.
     * @param cita cita base a enriquecer
     * @return CitaResponse con nombreCliente, nombreMascota y nombreVeterinario (pueden ser null)
     */
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

    /**
     * Reprograma una cita cambiando su fecha/hora y opcionalmente su duración.
     * Solo se permite si la cita no está COMPLETADA ni CANCELADA, y la nueva fecha debe ser futura.
     * @param id identificador de la cita a reprogramar
     * @param nuevaFecha nueva fecha y hora (obligatoria, debe ser posterior al momento actual)
     * @param nuevaDuracion nueva duración en minutos; si es null, conserva la duración anterior
     * @return la cita actualizada
     */
    public Cita reprogramar(Long id, LocalDateTime nuevaFecha, Integer nuevaDuracion) {
        Cita cita = obtenerPorId(id);
        // Una cita terminada o cancelada no puede moverse: su historia ya está escrita
        if (COMPLETADA.equals(cita.getEstado()) || CANCELADA.equals(cita.getEstado())) {
            throw new BusinessRuleException("No se puede reprogramar una cita " + cita.getEstado());
        }
        if (nuevaFecha == null) {
            throw new BusinessRuleException("La nueva fecha y hora son obligatorias");
        }
        if (!nuevaFecha.isAfter(LocalDateTime.now())) {
            throw new BusinessRuleException("La nueva fecha y hora deben ser futuras");
        }
        // Creamos un objeto temporal para reutilizar haySolapamiento sin modificar la cita aún
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

    /**
     * Crea una nueva cita verificando cliente (auth), mascota (ficha), solapamiento de horario y box.
     * Las validaciones de nulos se hacen primero; las llamadas remotas van al final para minimizar latencia.
     * @param cita objeto con todos los datos de la cita a agendar
     * @return la cita persistida en estado PENDIENTE, con recordatorio EMAIL generado automáticamente
     */
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
        // Validación de fecha futura: no se pueden agendar citas en el pasado
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
        // Validación dura con auth (8081): si el cliente no existe, la cita NO se crea
        authClient.verificarCliente(cita.getClienteId());
        // Validación dura con ficha (8087): si la mascota no existe, la cita NO se crea
        if (cita.getMascotaId() != null) {
            fichaClient.verificarMascota(cita.getMascotaId());
        }
        // Validación de solapamiento: el veterinario no puede tener dos citas al mismo tiempo
        if (haySolapamiento(cita)) {
            throw new ConflictException("El veterinario no está disponible en ese horario");
        }
        if (cita.getBoxId() != null && hayBoxOcupado(cita)) {
            throw new ConflictException("El box ya está ocupado en ese horario");
        }
        cita.setEstado(PENDIENTE);
        cita.setFechaCreacion(LocalDateTime.now());
        Cita guardada = citaRepository.save(cita);
        // Al crear la cita se genera automáticamente un recordatorio EMAIL para el cliente
        recordatorioGenerador.generarParaCita(guardada);
        return guardada;
    }

    /**
     * Avanza la cita al estado CONFIRMADA; no se puede confirmar una cita ya CANCELADA.
     * @param id identificador de la cita
     * @return la cita con estado "confirmada"
     */
    public Cita confirmar(Long id) {
        Cita cita = obtenerPorId(id);
        if (CANCELADA.equals(cita.getEstado())) {
            throw new BusinessRuleException("No se puede confirmar cita cancelada");
        }
        cita.setEstado(CONFIRMADA);
        return citaRepository.save(cita);
    }

    /**
     * Avanza la cita al estado EN_CURSO; solo válido si el estado actual es exactamente CONFIRMADA.
     * @param id identificador de la cita
     * @return la cita con estado "en curso"
     */
    public Cita iniciar(Long id) {
        Cita cita = obtenerPorId(id);
        // La máquina de estados es estricta: solo CONFIRMADA puede pasar a EN_CURSO
        if (!CONFIRMADA.equals(cita.getEstado())) {
            throw new BusinessRuleException("Debe estar confirmada antes de iniciarse");
        }
        cita.setEstado(EN_CURSO);
        return citaRepository.save(cita);
    }

    /**
     * Avanza la cita al estado COMPLETADA; solo válido si el estado actual es exactamente EN_CURSO.
     * @param id identificador de la cita
     * @return la cita con estado "completada"
     */
    public Cita completar(Long id) {
        Cita cita = obtenerPorId(id);
        // La máquina de estados es estricta: solo EN_CURSO puede pasar a COMPLETADA
        if (!EN_CURSO.equals(cita.getEstado())) {
            throw new BusinessRuleException("Debe estar en curso antes de completarse");
        }
        cita.setEstado(COMPLETADA);
        return citaRepository.save(cita);
    }

    /**
     * Cancela una cita registrando el motivo y marcando como cancelados sus recordatorios pendientes.
     * No se puede cancelar una cita que ya está COMPLETADA.
     * @param id identificador de la cita a cancelar
     * @param motivo descripción obligatoria del motivo de cancelación
     * @return la cita con estado "cancelada" y motivoCancelacion guardado
     */
    public Cita cancelar(Long id, String motivo) {
        Cita cita = obtenerPorId(id);
        // Una cita completada no puede retrotraerse a cancelada: la atención ya ocurrió
        if (COMPLETADA.equals(cita.getEstado())) {
            throw new BusinessRuleException("No se puede cancelar cita completada");
        }
        if (motivo == null) {
            throw new BusinessRuleException("El motivo es obligatorio");
        }
        cita.setEstado(CANCELADA);
        cita.setMotivoCancelacion(motivo);
        Cita cancelada = citaRepository.save(cita);
        // Al cancelar la cita se marcan como cancelados todos los recordatorios no enviados aún
        recordatorioGenerador.cancelarPorCita(cancelada.getId());
        return cancelada;
    }

    /**
     * Verifica si el veterinario ya tiene una cita (PENDIENTE o CONFIRMADA) que se solape con la nueva.
     * Usa lógica estándar de solapamiento de intervalos: A.inicio < B.fin && B.inicio < A.fin.
     * @param nueva cita cuyo horario se quiere verificar
     * @return true si hay solapamiento con alguna cita existente del veterinario
     */
    private boolean haySolapamiento(Cita nueva) {
        LocalDateTime inicioNueva = nueva.getFechaHora();
        LocalDateTime finNueva = inicioNueva.plusMinutes(duracion(nueva));
        List<Cita> ocupadas = new java.util.ArrayList<>();
        // Solo se verifica contra citas activas (pendiente o confirmada); las canceladas o completadas no bloquean
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

    /**
     * Verifica si el box ya está ocupado (PENDIENTE o CONFIRMADA) en el mismo horario que la nueva cita.
     * @param nueva cita con boxId y horario a verificar
     * @return true si el box tiene otra cita activa que se solapa
     */
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

    /**
     * Retorna la duración de la cita en minutos; si no tiene duración definida, usa 30 minutos por defecto.
     * @param cita cita de la que se quiere obtener la duración
     * @return duración en minutos
     */
    private int duracion(Cita cita) {
        return cita.getDuracionMinutos() == null ? DURACION_POR_DEFECTO : cita.getDuracionMinutos();
    }
}
