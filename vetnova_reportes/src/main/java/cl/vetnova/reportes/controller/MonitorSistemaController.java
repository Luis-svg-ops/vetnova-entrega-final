package cl.vetnova.reportes.controller;

import cl.vetnova.reportes.model.MonitorSistema;
import cl.vetnova.reportes.service.MonitorSistemaService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/monitor")
public class MonitorSistemaController {

    private final MonitorSistemaService service;

    public MonitorSistemaController(MonitorSistemaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<MonitorSistema> registrar(@RequestBody MonitorSistema request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(request));
    }

    @GetMapping("/{microservicio}/historial")
    public ResponseEntity<List<MonitorSistema>> historial(@PathVariable String microservicio) {
        return ResponseEntity.ok(service.historial(microservicio));
    }
}
