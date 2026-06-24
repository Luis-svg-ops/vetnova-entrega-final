package cl.vetnova.notificaciones.controller;

import cl.vetnova.notificaciones.model.CanalNotificacion;
import cl.vetnova.notificaciones.service.CanalNotificacionService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/canales")
public class CanalNotificacionController {

    private final CanalNotificacionService service;

    public CanalNotificacionController(CanalNotificacionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CanalNotificacion>> listar(@RequestParam Long usuarioId) {
        return ResponseEntity.ok(service.listar(usuarioId));
    }

    @PostMapping
    public ResponseEntity<CanalNotificacion> crear(@RequestBody CanalNotificacion request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CanalNotificacion> actualizar(@PathVariable Long id, @RequestBody CanalNotificacion request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<CanalNotificacion> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(service.desactivar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Canal eliminado correctamente"));
    }
}
