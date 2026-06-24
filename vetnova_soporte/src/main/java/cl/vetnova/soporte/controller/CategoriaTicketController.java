package cl.vetnova.soporte.controller;

import cl.vetnova.soporte.dto.CategoriaTicketRequest;
import cl.vetnova.soporte.model.CategoriaTicket;
import cl.vetnova.soporte.service.CategoriaTicketService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categorias-ticket")
public class CategoriaTicketController {

    private final CategoriaTicketService service;

    public CategoriaTicketController(CategoriaTicketService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaTicket>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaTicket> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarEntidad(id));
    }

    @PostMapping
    public ResponseEntity<CategoriaTicket> crear(@RequestBody CategoriaTicketRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaTicket> actualizar(@PathVariable Long id, @RequestBody CategoriaTicketRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Categoría eliminada correctamente"));
    }
}
