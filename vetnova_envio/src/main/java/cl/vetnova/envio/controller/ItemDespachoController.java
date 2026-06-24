package cl.vetnova.envio.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.vetnova.envio.model.ItemDespacho;
import cl.vetnova.envio.service.ItemDespachoService;

@RestController
@RequestMapping("/api/v1/itemdespachos")
public class ItemDespachoController {

    @Autowired
    private ItemDespachoService itemDespachoService;

    @GetMapping
    public ResponseEntity<List<ItemDespacho>> listar() {
        return ResponseEntity.ok(itemDespachoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDespacho> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(itemDespachoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<ItemDespacho> crear(@RequestBody ItemDespacho itemDespacho) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemDespachoService.crear(itemDespacho));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemDespacho> actualizar(@PathVariable Long id, @RequestBody ItemDespacho itemDespacho) {
        return ResponseEntity.ok(itemDespachoService.actualizar(id, itemDespacho));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        itemDespachoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}