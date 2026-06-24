package cl.vetnova.laboratorio.controller;

import cl.vetnova.laboratorio.dto.TipoExamenRequest;
import cl.vetnova.laboratorio.model.TipoExamen;
import cl.vetnova.laboratorio.service.TipoExamenService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tipos-examen")
public class TipoExamenController {

    private final TipoExamenService service;

    public TipoExamenController(TipoExamenService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TipoExamen>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoExamen> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarEntidad(id));
    }

    @PostMapping
    public ResponseEntity<TipoExamen> crear(@RequestBody TipoExamenRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoExamen> actualizar(@PathVariable Long id, @RequestBody TipoExamenRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Tipo de examen eliminado correctamente"));
    }
}
