package cl.vetnova.inventario.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.vetnova.inventario.dto.DetallePedidoRequest;
import cl.vetnova.inventario.model.DetallePedidoProveedor;
import cl.vetnova.inventario.service.DetallePedidoProveedorService;

@RestController
@RequestMapping("/api/v1/detallepedidoproveedors")
public class DetallePedidoProveedorController {

    @Autowired
    private DetallePedidoProveedorService detallePedidoProveedorService;

    @GetMapping
    public ResponseEntity<List<DetallePedidoProveedor>> listar() {
        return ResponseEntity.ok(detallePedidoProveedorService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetallePedidoProveedor> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(detallePedidoProveedorService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<DetallePedidoProveedor> crear(@RequestBody DetallePedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(detallePedidoProveedorService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetallePedidoProveedor> actualizar(@PathVariable Long id, @RequestBody DetallePedidoRequest request) {
        return ResponseEntity.ok(detallePedidoProveedorService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        detallePedidoProveedorService.eliminar(id);
        return ResponseEntity.ok().build();
    }
}
