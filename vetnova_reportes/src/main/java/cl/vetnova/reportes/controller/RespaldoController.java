package cl.vetnova.reportes.controller;

import cl.vetnova.reportes.model.Respaldo;
import cl.vetnova.reportes.service.RespaldoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/respaldos")
public class RespaldoController {

    private final RespaldoService service;

    public RespaldoController(RespaldoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Respaldo> ejecutar(@RequestBody Respaldo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.ejecutar(request));
    }

    @PostMapping("/{id}/restaurar")
    public ResponseEntity<Respaldo> restaurar(@PathVariable Long id) {
        return ResponseEntity.ok(service.restaurar(id));
    }
}
