package cl.vetnova.agenda.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.vetnova.agenda.model.HistorialAgenda;
import cl.vetnova.agenda.service.HistorialAgendaService;

import jakarta.validation.Valid;

/**
 * Controlador REST para registrar y consultar el historial de cambios en la agenda.
 * Permite llevar trazabilidad de las modificaciones realizadas sobre las citas.
 */
@RestController
@RequestMapping("/api/v1/historial")

public class HistorialAgendaController {

    @Autowired
    private HistorialAgendaService historialAgendaService;

    /**
     * Registra un nuevo evento en el historial de agenda (p. ej., reprogramación, cancelación).
     * @param historial objeto con citaId y detalle del evento
     * @return el registro creado con estado HTTP 201
     */
    @PostMapping
    public ResponseEntity<HistorialAgenda> crear(@Valid @RequestBody HistorialAgenda historial){
        return ResponseEntity.status(HttpStatus.CREATED).body(historialAgendaService.crear(historial));
    }

    /**
     * Retorna todos los registros del historial de agenda.
     * @return lista completa de eventos del historial con estado HTTP 200
     */
    @GetMapping
    public ResponseEntity<List<HistorialAgenda>> listar(){
        return ResponseEntity.ok(historialAgendaService.listar());
    }

    /**
     * Elimina un registro del historial por su id.
     * @param id identificador del registro a eliminar
     * @return respuesta vacía con estado HTTP 204
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        historialAgendaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}