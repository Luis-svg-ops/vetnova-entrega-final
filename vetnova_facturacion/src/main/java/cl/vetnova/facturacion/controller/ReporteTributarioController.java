package cl.vetnova.facturacion.controller;

import cl.vetnova.facturacion.dto.ReporteRequest;
import cl.vetnova.facturacion.model.ReporteTributario;
import cl.vetnova.facturacion.service.ReporteTributarioService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reportes-tributarios")
public class ReporteTributarioController {

    private final ReporteTributarioService service;

    public ReporteTributarioController(ReporteTributarioService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ReporteTributario>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReporteTributario> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @PostMapping
    public ResponseEntity<ReporteTributario> generar(@RequestBody ReporteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.generar(request));
    }

    @GetMapping("/{id}/exportar")
    public ResponseEntity<ReporteTributario> exportar(@PathVariable Long id, @RequestParam String formato) {
        return ResponseEntity.ok(service.exportar(id, formato));
    }
}
