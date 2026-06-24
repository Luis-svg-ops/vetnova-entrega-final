package cl.vetnova.laboratorio.controller;

import cl.vetnova.laboratorio.dto.CancelarOrdenRequest;
import cl.vetnova.laboratorio.dto.CrearOrdenExamenRequest;
import cl.vetnova.laboratorio.dto.ProgramarOrdenRequest;
import cl.vetnova.laboratorio.model.OrdenExamen;
import cl.vetnova.laboratorio.service.OrdenExamenService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ordenes-examen")
public class OrdenExamenController {

    private final OrdenExamenService service;

    public OrdenExamenController(OrdenExamenService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<OrdenExamen>> listar(@RequestParam(required = false) Long mascotaId) {
        return ResponseEntity.ok(service.listar(mascotaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenExamen> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @PostMapping
    public ResponseEntity<OrdenExamen> crear(@RequestBody CrearOrdenExamenRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/{id}/programar")
    public ResponseEntity<OrdenExamen> programar(@PathVariable Long id, @RequestBody ProgramarOrdenRequest request) {
        return ResponseEntity.ok(service.programar(id, request));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<OrdenExamen> cancelar(@PathVariable Long id, @RequestBody CancelarOrdenRequest request) {
        return ResponseEntity.ok(service.cancelar(id, request));
    }
}
