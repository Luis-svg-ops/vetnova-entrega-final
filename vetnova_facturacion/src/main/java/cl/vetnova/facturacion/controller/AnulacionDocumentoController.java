package cl.vetnova.facturacion.controller;

import cl.vetnova.facturacion.dto.AnulacionRequest;
import cl.vetnova.facturacion.exception.RegistroInmutableException;
import cl.vetnova.facturacion.model.AnulacionDocumento;
import cl.vetnova.facturacion.service.AnulacionDocumentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/anulaciones")
public class AnulacionDocumentoController {

    private final AnulacionDocumentoService service;

    public AnulacionDocumentoController(AnulacionDocumentoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AnulacionDocumento> registrar(@RequestBody AnulacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(request));
    }

    @PostMapping("/{id}/notificar-sii")
    public ResponseEntity<AnulacionDocumento> notificarSII(@PathVariable Long id) {
        return ResponseEntity.ok(service.notificarSII(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @RequestBody AnulacionRequest request) {
        throw new RegistroInmutableException("Las anulaciones no pueden modificarse ni eliminarse");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        throw new RegistroInmutableException("Las anulaciones no pueden modificarse ni eliminarse");
    }
}
