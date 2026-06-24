package cl.vetnova.notificaciones.controller;

import cl.vetnova.notificaciones.dto.RenderizarRequest;
import cl.vetnova.notificaciones.model.PlantillaMensaje;
import cl.vetnova.notificaciones.service.PlantillaMensajeService;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/plantillas")
public class PlantillaMensajeController {

    private final PlantillaMensajeService service;

    public PlantillaMensajeController(PlantillaMensajeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PlantillaMensaje> crear(@RequestBody PlantillaMensaje request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlantillaMensaje> actualizar(@PathVariable Long id, @RequestBody PlantillaMensaje request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @PostMapping("/{id}/renderizar")
    public ResponseEntity<Map<String, String>> renderizar(@PathVariable Long id, @RequestBody RenderizarRequest request) {
        return ResponseEntity.ok(Map.of("contenido", service.renderizar(id, request.getValores())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Plantilla eliminada correctamente"));
    }
}
