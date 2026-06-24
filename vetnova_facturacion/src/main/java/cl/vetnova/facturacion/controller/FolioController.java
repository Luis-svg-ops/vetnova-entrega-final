package cl.vetnova.facturacion.controller;

import cl.vetnova.facturacion.dto.FolioRequest;
import cl.vetnova.facturacion.model.Folio;
import cl.vetnova.facturacion.service.FolioService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/folios")
public class FolioController {

    private final FolioService service;

    public FolioController(FolioService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Folio>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Folio> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @PostMapping
    public ResponseEntity<Folio> crear(@RequestBody FolioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }
}
