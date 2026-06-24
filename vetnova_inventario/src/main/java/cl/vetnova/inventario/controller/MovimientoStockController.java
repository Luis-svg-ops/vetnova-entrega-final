package cl.vetnova.inventario.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.vetnova.inventario.dto.MovimientoStockRequest;
import cl.vetnova.inventario.dto.MovimientoStockResponse;
import cl.vetnova.inventario.exception.RegistroInmutableException;
import cl.vetnova.inventario.model.MovimientoStock;
import cl.vetnova.inventario.service.MovimientoStockService;

@RestController
@RequestMapping("/api/v1/movimientos")
public class MovimientoStockController {

    @Autowired
    private MovimientoStockService movimientoStockService;

    @GetMapping
    public ResponseEntity<List<MovimientoStock>> listar() {
        return ResponseEntity.ok(movimientoStockService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoStock> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(movimientoStockService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<MovimientoStockResponse> registrar(@RequestBody MovimientoStockRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoStockService.registrar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @RequestBody MovimientoStockRequest request) {
        throw new RegistroInmutableException("Los movimientos no pueden ser modificados");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        throw new RegistroInmutableException("Los movimientos no pueden ser eliminados");
    }
}
