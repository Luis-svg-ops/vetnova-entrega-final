package cl.vetnova.reportes.controller;

import cl.vetnova.reportes.model.ReporteStock;
import cl.vetnova.reportes.service.ReporteStockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reportes-stock")
public class ReporteStockController {

    private final ReporteStockService service;

    public ReporteStockController(ReporteStockService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ReporteStock> crear(@RequestBody ReporteStock request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }
}
