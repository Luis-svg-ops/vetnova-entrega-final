package cl.vetnova.ventas.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.vetnova.ventas.dto.ActualizarCantidadRequest;
import cl.vetnova.ventas.dto.AgregarItemCarritoRequest;
import cl.vetnova.ventas.dto.CarritoItemResultado;
import cl.vetnova.ventas.model.Carrito;
import cl.vetnova.ventas.service.CarritoService;

@RestController
@RequestMapping("/api/v1/carritos")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @GetMapping
    public ResponseEntity<List<Carrito>> listar() {
        return ResponseEntity.ok(carritoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Carrito> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(carritoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Carrito> crear(@RequestBody Carrito carrito) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carritoService.crear(carrito));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Carrito> actualizar(@PathVariable Long id, @RequestBody Carrito carrito) {
        return ResponseEntity.ok(carritoService.actualizar(id, carrito));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<Carrito> agregarItem(@PathVariable Long id, @RequestBody AgregarItemCarritoRequest request) {
        CarritoItemResultado resultado = carritoService.agregarItem(id, request);
        HttpStatus status = resultado.creado() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(resultado.carrito());
    }

    @PutMapping("/{id}/items/{itemId}")
    public ResponseEntity<Carrito> actualizarCantidad(@PathVariable Long id, @PathVariable Long itemId,
            @RequestBody ActualizarCantidadRequest request) {
        return ResponseEntity.ok(carritoService.actualizarCantidad(id, itemId, request.getCantidad()));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public ResponseEntity<Carrito> quitarItem(@PathVariable Long id, @PathVariable Long itemId) {
        return ResponseEntity.ok(carritoService.quitarItem(id, itemId));
    }

    @GetMapping("/{id}/total")
    public ResponseEntity<Map<String, Object>> calcularTotal(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("total", carritoService.calcularTotal(id)));
    }

    @DeleteMapping("/{id}/items")
    public ResponseEntity<Carrito> vaciar(@PathVariable Long id) {
        return ResponseEntity.ok(carritoService.vaciar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        carritoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
