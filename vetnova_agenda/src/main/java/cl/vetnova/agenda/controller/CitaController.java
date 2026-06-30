package cl.vetnova.agenda.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.vetnova.agenda.dto.CancelarCitaRequest;
import cl.vetnova.agenda.dto.CitaRequest;
import cl.vetnova.agenda.dto.CitaResponse;
import cl.vetnova.agenda.dto.ReprogramarCitaRequest;
import cl.vetnova.agenda.model.Cita;
import cl.vetnova.agenda.service.CitaService;

/**
 * Controlador REST principal del microservicio agenda: gestiona el ciclo de vida completo de una cita.
 * Expone endpoints para crear, consultar y avanzar la máquina de estados PENDIENTE→CONFIRMADA→EN_CURSO→COMPLETADA.
 */
@RestController
@RequestMapping("/api/v1/citas")
public class CitaController {

    @Autowired
    private CitaService citaService;

    /**
     * Crea una nueva cita verificando cliente (auth), mascota (ficha), solapamiento y fecha futura.
     * Construye el objeto Cita desde el DTO de entrada y lo delega al servicio.
     * @param request DTO con clienteId, mascotaId, veterinarioId, servicioId, boxId, sucursal, fechaHora, etc.
     * @return la cita creada en estado PENDIENTE con estado HTTP 201
     */
    @PostMapping
    public ResponseEntity<Cita> crear(@RequestBody CitaRequest request) {
        Cita cita = new Cita();
        cita.setClienteId(request.clienteId());
        cita.setMascotaId(request.mascotaId());
        cita.setVeterinarioId(request.veterinarioId());
        cita.setServicioId(request.servicioId());
        cita.setBoxId(request.boxId());
        cita.setSucursal(request.sucursal());
        cita.setFechaHora(request.fechaHora());
        cita.setDuracionMinutos(request.duracionMinutos());
        cita.setCanal(request.canal());
        return ResponseEntity.status(HttpStatus.CREATED).body(citaService.crear(cita));
    }

    /**
     * Lista todas las citas enriquecidas con nombres de cliente, mascota y veterinario.
     * @return lista de CitaResponse con los datos desnormalizados
     */
    @GetMapping
    public ResponseEntity<List<CitaResponse>> listar() {
        return ResponseEntity.ok(citaService.listarConNombres());
    }

    /**
     * Retorna la agenda del día actual ordenada por hora, enriquecida con nombres.
     * @return lista de citas con fechaHora entre 00:00 y 23:59:59 del día de hoy
     */
    @GetMapping("/agenda")
    public ResponseEntity<List<CitaResponse>> agendaHoy() {
        return ResponseEntity.ok(citaService.agendaDelDiaConNombres(java.time.LocalDateTime.now()));
    }

    /**
     * Busca una cita por su id y retorna la respuesta enriquecida con nombres.
     * @param id identificador de la cita
     * @return CitaResponse con nombres resueltos; excepción 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<CitaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.obtenerConNombres(id));
    }

    /**
     * Reprograma la fecha/hora de una cita que no esté COMPLETADA ni CANCELADA.
     * @param id identificador de la cita a reprogramar
     * @param request nueva fechaHora (obligatoria, debe ser futura) y duracionMinutos opcional
     * @return la cita actualizada con la nueva fecha
     */
    @PutMapping("/{id}")
    public ResponseEntity<Cita> reprogramar(@PathVariable Long id, @RequestBody ReprogramarCitaRequest request) {
        return ResponseEntity.ok(citaService.reprogramar(id, request.fechaHora(), request.duracionMinutos()));
    }

    /**
     * Avanza la cita de PENDIENTE a CONFIRMADA (primer paso de la máquina de estados).
     * @param id identificador de la cita a confirmar
     * @return la cita con estado "confirmada"
     */
    @PutMapping("/{id}/confirmar")
    public ResponseEntity<Cita> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.confirmar(id));
    }

    /**
     * Avanza la cita de CONFIRMADA a EN_CURSO; solo válido si el estado actual es "confirmada".
     * @param id identificador de la cita a iniciar
     * @return la cita con estado "en curso"
     */
    @PutMapping("/{id}/iniciar")
    public ResponseEntity<Cita> iniciar(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.iniciar(id));
    }

    /**
     * Avanza la cita de EN_CURSO a COMPLETADA; solo válido si el estado actual es "en curso".
     * @param id identificador de la cita a completar
     * @return la cita con estado "completada"
     */
    @PutMapping("/{id}/completar")
    public ResponseEntity<Cita> completar(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.completar(id));
    }

    /**
     * Cancela una cita desde cualquier estado anterior a COMPLETADA y cancela sus recordatorios pendientes.
     * @param id identificador de la cita a cancelar
     * @param request motivo de cancelación (obligatorio)
     * @return la cita con estado "cancelada" y motivoCancelacion registrado
     */
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Cita> cancelar(@PathVariable Long id, @RequestBody CancelarCitaRequest request) {
        return ResponseEntity.ok(citaService.cancelar(id, request.motivoCancelacion()));
    }
}
