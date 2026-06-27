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

@RestController
@RequestMapping("/api/v1/citas")
public class CitaController {

    @Autowired
    private CitaService citaService;

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

    @GetMapping
    public ResponseEntity<List<CitaResponse>> listar() {
        return ResponseEntity.ok(citaService.listarConNombres());
    }

    @GetMapping("/agenda")
    public ResponseEntity<List<CitaResponse>> agendaHoy() {
        return ResponseEntity.ok(citaService.agendaDelDiaConNombres(java.time.LocalDateTime.now()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.obtenerConNombres(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cita> reprogramar(@PathVariable Long id, @RequestBody ReprogramarCitaRequest request) {
        return ResponseEntity.ok(citaService.reprogramar(id, request.fechaHora(), request.duracionMinutos()));
    }

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<Cita> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.confirmar(id));
    }

    @PutMapping("/{id}/iniciar")
    public ResponseEntity<Cita> iniciar(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.iniciar(id));
    }

    @PutMapping("/{id}/completar")
    public ResponseEntity<Cita> completar(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.completar(id));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Cita> cancelar(@PathVariable Long id, @RequestBody CancelarCitaRequest request) {
        return ResponseEntity.ok(citaService.cancelar(id, request.motivoCancelacion()));
    }
}
