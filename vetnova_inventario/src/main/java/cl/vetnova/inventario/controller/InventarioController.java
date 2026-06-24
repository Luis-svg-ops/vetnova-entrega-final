package cl.vetnova.inventario.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.vetnova.inventario.dto.EntradaStockRequest;
import cl.vetnova.inventario.dto.SalidaStockRequest;
import cl.vetnova.inventario.dto.StockMinimoRequest;
import cl.vetnova.inventario.dto.StockTotalResponse;
import cl.vetnova.inventario.model.Inventario;
import cl.vetnova.inventario.service.InventarioService;

@RestController
@RequestMapping("/api/v1/inventarios")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @GetMapping
    public ResponseEntity<List<Inventario>> listar() {
        return ResponseEntity.ok(inventarioService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventario> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Inventario> crear(@RequestBody Inventario inventario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.crear(inventario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inventario> actualizar(@PathVariable Long id, @RequestBody Inventario inventario) {
        return ResponseEntity.ok(inventarioService.actualizar(id, inventario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        inventarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/entrada")
    public ResponseEntity<Inventario> registrarEntrada(@PathVariable Long id, @RequestBody EntradaStockRequest request) {
        return ResponseEntity.ok(inventarioService.registrarEntrada(id, request.getCantidad(), request.getResponsable()));
    }

    @PostMapping("/{id}/salida")
    public ResponseEntity<Inventario> registrarSalida(@PathVariable Long id, @RequestBody SalidaStockRequest request) {
        return ResponseEntity.ok(inventarioService.registrarSalida(id, request.getCantidad(), request.getMotivo()));
    }

    @PutMapping("/{id}/stock-minimo")
    public ResponseEntity<Inventario> ajustarStockMinimo(@PathVariable Long id, @RequestBody StockMinimoRequest request) {
        return ResponseEntity.ok(inventarioService.ajustarStockMinimo(id, request.getMinimo()));
    }

    @GetMapping("/{id}/stock-total")
    public ResponseEntity<StockTotalResponse> getStockTotal(@PathVariable Long id) {
        return ResponseEntity.ok(new StockTotalResponse(inventarioService.getStockTotal(id)));
    }
}