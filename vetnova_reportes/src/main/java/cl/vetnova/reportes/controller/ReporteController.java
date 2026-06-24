package cl.vetnova.reportes.controller;

import cl.vetnova.reportes.dto.ReporteRequest;
import cl.vetnova.reportes.model.Reporte;
import cl.vetnova.reportes.service.ReporteService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reportes")
public class ReporteController {

    private final ReporteService service;

    public ReporteController(ReporteService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Reporte>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reporte> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @PostMapping
    public ResponseEntity<Reporte> generar(@RequestBody ReporteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.generar(request));
    }

    @GetMapping("/{id}/exportar")
    public ResponseEntity<Reporte> exportar(@PathVariable Long id, @RequestParam String formato) {
        return ResponseEntity.ok(service.exportar(id, formato));
    }
}
