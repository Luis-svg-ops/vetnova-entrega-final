package cl.vetnova.inventario.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.vetnova.inventario.dto.ResolucionRequest;
import cl.vetnova.inventario.dto.SolicitudReposicionRequest;
import cl.vetnova.inventario.exception.RegistroInmutableException;
import cl.vetnova.inventario.model.SolicitudReposicion;
import cl.vetnova.inventario.service.SolicitudReposicionService;

@RestController
@RequestMapping("/api/v1/solicitudreposicions")
public class SolicitudReposicionController {

    @Autowired
    private SolicitudReposicionService solicitudReposicionService;

    @GetMapping
    public ResponseEntity<List<SolicitudReposicion>> listar() {
        return ResponseEntity.ok(solicitudReposicionService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudReposicion> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(solicitudReposicionService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<SolicitudReposicion> crear(@RequestBody SolicitudReposicionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(solicitudReposicionService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SolicitudReposicion> actualizar(@PathVariable Long id, @RequestBody SolicitudReposicion solicitud) {
        return ResponseEntity.ok(solicitudReposicionService.actualizar(id, solicitud));
    }

    @PutMapping("/{id}/aprobar")
    public ResponseEntity<SolicitudReposicion> aprobar(@PathVariable Long id, @RequestBody ResolucionRequest request) {
        return ResponseEntity.ok(solicitudReposicionService.aprobar(id, request));
    }

    @PutMapping("/{id}/rechazar")
    public ResponseEntity<SolicitudReposicion> rechazar(@PathVariable Long id, @RequestBody ResolucionRequest request) {
        return ResponseEntity.ok(solicitudReposicionService.rechazar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        throw new RegistroInmutableException("Las solicitudes no pueden ser eliminadas");
    }
}
