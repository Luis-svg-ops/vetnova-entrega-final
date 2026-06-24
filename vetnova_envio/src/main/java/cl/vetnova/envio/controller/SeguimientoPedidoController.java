package cl.vetnova.envio.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import cl.vetnova.envio.dto.ActualizarEstadoRequest;
import cl.vetnova.envio.model.RegistroSeguimiento;
import cl.vetnova.envio.model.SeguimientoPedido;
import cl.vetnova.envio.service.SeguimientoPedidoService;

@RestController
@RequestMapping("/api/v1/seguimientopedidos")
public class SeguimientoPedidoController {

    @Autowired
    private SeguimientoPedidoService seguimientoPedidoService;

    @GetMapping
    public ResponseEntity<List<SeguimientoPedido>> listar() {
        return ResponseEntity.ok(seguimientoPedidoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeguimientoPedido> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(seguimientoPedidoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<SeguimientoPedido> crear(@RequestBody SeguimientoPedido seguimientoPedido) {
        return ResponseEntity.status(HttpStatus.CREATED).body(seguimientoPedidoService.crear(seguimientoPedido));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SeguimientoPedido> actualizar(@PathVariable Long id, @RequestBody SeguimientoPedido seguimientoPedido) {
        return ResponseEntity.ok(seguimientoPedidoService.actualizar(id, seguimientoPedido));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        seguimientoPedidoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<SeguimientoPedido> actualizarEstado(@PathVariable Long id,
            @RequestBody ActualizarEstadoRequest request) {
        return ResponseEntity.ok(
                seguimientoPedidoService.actualizarEstado(id, request.getEstado(), request.getObservacion()));
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<Map<String, List<RegistroSeguimiento>>> historial(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("historial", seguimientoPedidoService.getHistorial(id)));
    }
}