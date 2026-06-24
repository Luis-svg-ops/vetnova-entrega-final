package cl.vetnova.laboratorio.controller;

import cl.vetnova.laboratorio.dto.ActualizarEstadoMuestraRequest;
import cl.vetnova.laboratorio.dto.RecepcionMuestraRequest;
import cl.vetnova.laboratorio.dto.RegistrarMuestraRequest;
import cl.vetnova.laboratorio.model.Muestra;
import cl.vetnova.laboratorio.service.MuestraService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/muestras")
public class MuestraController {

    private final MuestraService service;

    public MuestraController(MuestraService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Muestra> crear(@RequestBody RegistrarMuestraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/{id}/recepcion")
    public ResponseEntity<Muestra> recepcion(@PathVariable Long id, @RequestBody RecepcionMuestraRequest request) {
        return ResponseEntity.ok(service.registrarRecepcion(id, request));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Muestra> actualizarEstado(@PathVariable Long id, @RequestBody ActualizarEstadoMuestraRequest request) {
        return ResponseEntity.ok(service.actualizarEstado(id, request));
    }
}
