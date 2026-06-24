package cl.vetnova.soporte.controller;

import cl.vetnova.soporte.dto.DerivacionRequest;
import cl.vetnova.soporte.exception.RegistroInmutableException;
import cl.vetnova.soporte.model.DerivacionTicket;
import cl.vetnova.soporte.service.DerivacionTicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/derivaciones")
public class DerivacionTicketController {

    private final DerivacionTicketService service;

    public DerivacionTicketController(DerivacionTicketService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DerivacionTicket> registrar(@RequestBody DerivacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @RequestBody DerivacionRequest request) {
        throw new RegistroInmutableException("Las derivaciones no pueden modificarse ni eliminarse");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        throw new RegistroInmutableException("Las derivaciones no pueden modificarse ni eliminarse");
    }
}
