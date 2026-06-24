package cl.vetnova.envio.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import cl.vetnova.envio.model.RutaDespacho;
import cl.vetnova.envio.service.RutaDespachoService;

@RestController
@RequestMapping("/api/v1/rutadespachos")
public class RutaDespachoController {

    @Autowired
    private RutaDespachoService rutaDespachoService;

    @GetMapping
    public ResponseEntity<List<RutaDespacho>> listar() {
        return ResponseEntity.ok(rutaDespachoService.listar());
    }

    @GetMapping("/optimizar")
    public ResponseEntity<RutaDespacho> optimizar(@RequestParam String origen, @RequestParam String destino) {
        return ResponseEntity.ok(rutaDespachoService.optimizar(origen, destino));
    }

    @GetMapping("/{id}/tiempo")
    public ResponseEntity<Map<String, Object>> calcularTiempo(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("tiempoEstimadoMin", rutaDespachoService.calcularTiempoEstimado(id)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RutaDespacho> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(rutaDespachoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<RutaDespacho> crear(@RequestBody RutaDespacho rutaDespacho) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rutaDespachoService.crear(rutaDespacho));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RutaDespacho> actualizar(@PathVariable Long id, @RequestBody RutaDespacho rutaDespacho) {
        return ResponseEntity.ok(rutaDespachoService.actualizar(id, rutaDespacho));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        rutaDespachoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}