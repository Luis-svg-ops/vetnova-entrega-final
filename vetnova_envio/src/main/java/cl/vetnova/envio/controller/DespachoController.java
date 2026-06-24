package cl.vetnova.envio.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.vetnova.envio.dto.ActualizarEstadoRequest;
import cl.vetnova.envio.model.Despacho;
import cl.vetnova.envio.model.ItemDespacho;
import cl.vetnova.envio.service.DespachoService;
import cl.vetnova.envio.service.ItemDespachoService;

@RestController
@RequestMapping("/api/v1/despachos")
public class DespachoController {

    @Autowired
    private DespachoService despachoService;

    @Autowired
    private ItemDespachoService itemDespachoService;

    @GetMapping
    public ResponseEntity<List<Despacho>> listar() {
        return ResponseEntity.ok(despachoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Despacho> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(despachoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Despacho> crear(@RequestBody Despacho despacho) {
        return ResponseEntity.status(HttpStatus.CREATED).body(despachoService.crear(despacho));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Despacho> actualizar(@PathVariable Long id, @RequestBody Despacho despacho) {
        return ResponseEntity.ok(despachoService.actualizar(id, despacho));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        despachoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/iniciar")
    public ResponseEntity<Despacho> iniciar(@PathVariable Long id) {
        return ResponseEntity.ok(despachoService.iniciar(id));
    }

    @PutMapping("/{id}/enviar")
    public ResponseEntity<Despacho> enviar(@PathVariable Long id) {
        return ResponseEntity.ok(despachoService.enviar(id));
    }

    @PutMapping("/{id}/entrega")
    public ResponseEntity<Despacho> confirmarEntrega(@PathVariable Long id) {
        return ResponseEntity.ok(despachoService.confirmarEntrega(id));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Despacho> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(despachoService.cancelar(id));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Despacho> actualizarEstado(@PathVariable Long id,
            @RequestBody ActualizarEstadoRequest request) {
        return ResponseEntity.ok(despachoService.actualizarEstado(id, request.getEstado()));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<ItemDespacho> agregarItem(@PathVariable Long id, @RequestBody ItemDespacho item) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemDespachoService.agregarItem(id, item));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public ResponseEntity<Void> eliminarItem(@PathVariable Long id, @PathVariable Long itemId) {
        itemDespachoService.eliminarItem(id, itemId);
        return ResponseEntity.ok().build();
    }
}