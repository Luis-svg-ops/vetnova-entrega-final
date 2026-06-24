package cl.vetnova.inventario.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.vetnova.inventario.dto.PedidoProveedorRequest;
import cl.vetnova.inventario.model.PedidoProveedor;
import cl.vetnova.inventario.service.PedidoProveedorService;

@RestController
@RequestMapping("/api/v1/pedidoproveedors")
public class PedidoProveedorController {

    @Autowired
    private PedidoProveedorService pedidoProveedorService;

    @GetMapping
    public ResponseEntity<List<PedidoProveedor>> listar() {
        return ResponseEntity.ok(pedidoProveedorService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoProveedor> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoProveedorService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<PedidoProveedor> crear(@RequestBody PedidoProveedorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoProveedorService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoProveedor> actualizar(@PathVariable Long id, @RequestBody PedidoProveedor pedidoProveedor) {
        return ResponseEntity.ok(pedidoProveedorService.actualizar(id, pedidoProveedor));
    }

    @PutMapping("/{id}/enviar")
    public ResponseEntity<PedidoProveedor> enviar(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoProveedorService.enviar(id));
    }

    @PutMapping("/{id}/recibir")
    public ResponseEntity<PedidoProveedor> recibir(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoProveedorService.recibir(id));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<PedidoProveedor> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoProveedorService.cancelar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pedidoProveedorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
