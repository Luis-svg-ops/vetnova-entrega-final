package cl.vetnova.reportes.controller;

import cl.vetnova.reportes.model.Dashboard;
import cl.vetnova.reportes.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Dashboard> obtener(@RequestParam(required = false) String sucursal) {
        return ResponseEntity.ok(service.cargarIndicadores(sucursal));
    }
}
