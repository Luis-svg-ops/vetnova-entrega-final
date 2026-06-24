package cl.vetnova.envio.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.vetnova.envio.model.GuiaDespacho;
import cl.vetnova.envio.service.GuiaDespachoService;

@RestController
@RequestMapping("/api/v1/guiadespachos")
public class GuiaDespachoController {

    @Autowired
    private GuiaDespachoService guiaDespachoService;

    @GetMapping
    public ResponseEntity<List<GuiaDespacho>> listar() {
        return ResponseEntity.ok(guiaDespachoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuiaDespacho> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(guiaDespachoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<GuiaDespacho> crear(@RequestBody GuiaDespacho guiaDespacho) {
        return ResponseEntity.status(HttpStatus.CREATED).body(guiaDespachoService.crear(guiaDespacho));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GuiaDespacho> actualizar(@PathVariable Long id, @RequestBody GuiaDespacho guiaDespacho) {
        return ResponseEntity.ok(guiaDespachoService.actualizar(id, guiaDespacho));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        guiaDespachoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}