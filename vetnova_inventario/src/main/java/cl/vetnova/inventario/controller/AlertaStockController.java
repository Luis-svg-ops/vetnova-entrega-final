package cl.vetnova.inventario.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.vetnova.inventario.dto.AlertaLeidaResponse;
import cl.vetnova.inventario.model.AlertaStock;
import cl.vetnova.inventario.service.AlertaStockService;

@RestController
@RequestMapping("/api/v1/alertastocks")
public class AlertaStockController {

    @Autowired
    private AlertaStockService alertaStockService;

    @GetMapping
    public ResponseEntity<List<AlertaStock>> listar() {
        return ResponseEntity.ok(alertaStockService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertaStock> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(alertaStockService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<AlertaStock> crear(@RequestBody AlertaStock alertaStock) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alertaStockService.crear(alertaStock));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlertaStock> actualizar(@PathVariable Long id, @RequestBody AlertaStock alertaStock) {
        return ResponseEntity.ok(alertaStockService.actualizar(id, alertaStock));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        alertaStockService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/leer")
    public ResponseEntity<AlertaLeidaResponse> marcarLeida(@PathVariable Long id) {
        return ResponseEntity.ok(alertaStockService.marcarLeida(id));
    }
}