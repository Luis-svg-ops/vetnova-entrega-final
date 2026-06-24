package cl.vetnova.inventario.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.vetnova.inventario.dto.AsociarProductoRequest;
import cl.vetnova.inventario.model.Proveedor;
import cl.vetnova.inventario.model.ProveedorProducto;
import cl.vetnova.inventario.service.ProveedorService;

@RestController
@RequestMapping("/api/v1/proveedors")
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;

    @GetMapping
    public ResponseEntity<List<Proveedor>> listar() {
        return ResponseEntity.ok(proveedorService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Proveedor> crear(@RequestBody Proveedor proveedor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proveedorService.crear(proveedor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> actualizar(@PathVariable Long id, @RequestBody Proveedor proveedor) {
        return ResponseEntity.ok(proveedorService.actualizar(id, proveedor));
    }

    @PostMapping("/{id}/productos")
    public ResponseEntity<ProveedorProducto> asociarProducto(@PathVariable Long id,
            @RequestBody AsociarProductoRequest request) {
        return ResponseEntity.ok(proveedorService.asociarProducto(id, request.getProductoId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Proveedor> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.desactivar(id));
    }
}
