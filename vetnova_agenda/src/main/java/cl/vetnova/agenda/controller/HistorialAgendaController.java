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

@RestController
@RequestMapping("/api/v1/historial")

public class HistorialAgendaController {

    @Autowired
    private HistorialAgendaService historialAgendaService;

    @PostMapping
    public ResponseEntity<HistorialAgenda> crear(@Valid @RequestBody HistorialAgenda historial){
        return ResponseEntity.status(HttpStatus.CREATED).body(historialAgendaService.crear(historial));
    }

    @GetMapping
    public ResponseEntity<List<HistorialAgenda>> listar(){
        return ResponseEntity.ok(historialAgendaService.listar());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        historialAgendaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}