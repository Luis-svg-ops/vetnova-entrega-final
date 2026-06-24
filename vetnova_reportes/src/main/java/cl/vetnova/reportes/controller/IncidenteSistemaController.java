package cl.vetnova.reportes.controller;

import cl.vetnova.reportes.model.IncidenteSistema;
import cl.vetnova.reportes.service.IncidenteSistemaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/incidentes")
public class IncidenteSistemaController {

    private final IncidenteSistemaService service;

    public IncidenteSistemaController(IncidenteSistemaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<IncidenteSistema> registrar(@RequestBody IncidenteSistema request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(request));
    }

    @PostMapping("/{id}/notificar")
    public ResponseEntity<IncidenteSistema> notificar(@PathVariable Long id) {
        return ResponseEntity.ok(service.notificarAdministrador(id));
    }

    @PutMapping("/{id}/resolver")
    public ResponseEntity<IncidenteSistema> resolver(@PathVariable Long id) {
        return ResponseEntity.ok(service.resolver(id));
    }
}
