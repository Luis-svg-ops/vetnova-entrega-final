package cl.vetnova.inventario.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.vetnova.inventario.dto.CancelacionResponse;
import cl.vetnova.inventario.model.TransferenciaStock;
import cl.vetnova.inventario.service.TransferenciaStockService;

@RestController
@RequestMapping("/api/v1/transferenciastocks")
public class TransferenciaStockController {

    @Autowired
    private TransferenciaStockService transferenciaStockService;

    @GetMapping
    public ResponseEntity<List<TransferenciaStock>> listar() {
        return ResponseEntity.ok(transferenciaStockService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransferenciaStock> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(transferenciaStockService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<TransferenciaStock> crear(@RequestBody TransferenciaStock transferenciaStock) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transferenciaStockService.crear(transferenciaStock));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransferenciaStock> actualizar(@PathVariable Long id, @RequestBody TransferenciaStock transferenciaStock) {
        return ResponseEntity.ok(transferenciaStockService.actualizar(id, transferenciaStock));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        transferenciaStockService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/iniciar")
    public ResponseEntity<TransferenciaStock> iniciar(@PathVariable Long id) {
        return ResponseEntity.ok(transferenciaStockService.iniciar(id));
    }

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<TransferenciaStock> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(transferenciaStockService.confirmarRecepcion(id));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<CancelacionResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(transferenciaStockService.cancelar(id));
    }
}