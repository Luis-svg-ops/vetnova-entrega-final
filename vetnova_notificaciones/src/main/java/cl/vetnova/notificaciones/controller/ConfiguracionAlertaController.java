package cl.vetnova.notificaciones.controller;

import cl.vetnova.notificaciones.model.ConfiguracionAlerta;
import cl.vetnova.notificaciones.service.ConfiguracionAlertaService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/configuraciones-alerta")
public class ConfiguracionAlertaController {

    private final ConfiguracionAlertaService service;

    public ConfiguracionAlertaController(ConfiguracionAlertaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ConfiguracionAlerta>> listar(@RequestParam Long usuarioId) {
        return ResponseEntity.ok(service.listar(usuarioId));
    }

    @PostMapping
    public ResponseEntity<ConfiguracionAlerta> crear(@RequestBody ConfiguracionAlerta request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<ConfiguracionAlerta> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(service.desactivar(id));
    }

    @PutMapping("/{id}/activar")
    public ResponseEntity<ConfiguracionAlerta> activar(@PathVariable Long id) {
        return ResponseEntity.ok(service.activar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Configuración de alerta eliminada correctamente"));
    }
}
