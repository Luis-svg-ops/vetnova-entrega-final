package cl.vetnova.notificaciones.controller;

import cl.vetnova.notificaciones.exception.RegistroInmutableException;
import cl.vetnova.notificaciones.model.Notificacion;
import cl.vetnova.notificaciones.service.NotificacionService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {

    private final NotificacionService service;

    public NotificacionController(NotificacionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Notificacion>> listar(@RequestParam Long usuarioId, @RequestParam(required = false) Boolean leida) {
        return ResponseEntity.ok(service.listar(usuarioId, leida));
    }

    @GetMapping("/no-leidas/count")
    public ResponseEntity<Map<String, Object>> contarNoLeidas(@RequestParam Long usuarioId) {
        return ResponseEntity.ok(Map.of("usuarioId", usuarioId, "noLeidas", service.contarNoLeidas(usuarioId)));
    }

    @PostMapping
    public ResponseEntity<Notificacion> crear(@RequestBody Notificacion request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/{id}/leer")
    public ResponseEntity<Notificacion> leer(@PathVariable Long id) {
        return ResponseEntity.ok(service.marcarLeida(id));
    }

    @PostMapping("/{id}/reenviar")
    public ResponseEntity<Notificacion> reenviar(@PathVariable Long id) {
        return ResponseEntity.ok(service.reenviar(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @RequestBody Notificacion request) {
        throw new RegistroInmutableException("Las notificaciones no pueden modificarse");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        throw new RegistroInmutableException("Las notificaciones no pueden eliminarse");
    }
}
