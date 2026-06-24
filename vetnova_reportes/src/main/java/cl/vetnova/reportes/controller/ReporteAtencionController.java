package cl.vetnova.reportes.controller;

import cl.vetnova.reportes.model.ReporteAtencion;
import cl.vetnova.reportes.service.ReporteAtencionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reportes-atencion")
public class ReporteAtencionController {

    private final ReporteAtencionService service;

    public ReporteAtencionController(ReporteAtencionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ReporteAtencion> crear(@RequestBody ReporteAtencion request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }
}
