package cl.vetnova.notificaciones.controller;

import cl.vetnova.notificaciones.exception.RegistroInmutableException;
import cl.vetnova.notificaciones.model.HistorialMensaje;
import cl.vetnova.notificaciones.service.HistorialMensajeService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/historial-mensajes")
public class HistorialMensajeController {

    private final HistorialMensajeService service;

    public HistorialMensajeController(HistorialMensajeService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<HistorialMensaje>> listar(@RequestParam(required = false) Long notificacionId,
                                                         @RequestParam(required = false) Long canalId,
                                                         @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(service.listar(notificacionId, canalId, estado));
    }

    @PostMapping
    public ResponseEntity<HistorialMensaje> crear(@RequestBody HistorialMensaje request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @RequestBody HistorialMensaje request) {
        throw new RegistroInmutableException("El historial de mensajes no puede modificarse");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        throw new RegistroInmutableException("El historial de mensajes no puede eliminarse");
    }
}
