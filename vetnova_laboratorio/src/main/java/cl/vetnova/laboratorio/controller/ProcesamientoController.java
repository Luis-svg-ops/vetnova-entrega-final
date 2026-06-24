package cl.vetnova.laboratorio.controller;

import cl.vetnova.laboratorio.dto.CompletarProcesamientoRequest;
import cl.vetnova.laboratorio.dto.CrearProcesamientoRequest;
import cl.vetnova.laboratorio.model.Procesamiento;
import cl.vetnova.laboratorio.service.ProcesamientoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/procesamientos")
public class ProcesamientoController {

    private final ProcesamientoService service;

    public ProcesamientoController(ProcesamientoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Procesamiento> crear(@RequestBody CrearProcesamientoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/{id}/iniciar")
    public ResponseEntity<Procesamiento> iniciar(@PathVariable Long id) {
        return ResponseEntity.ok(service.iniciar(id));
    }

    @PutMapping("/{id}/completar")
    public ResponseEntity<Procesamiento> completar(@PathVariable Long id, @RequestBody CompletarProcesamientoRequest request) {
        return ResponseEntity.ok(service.completar(id, request));
    }
}
