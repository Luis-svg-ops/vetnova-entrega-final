package cl.vetnova.soporte.controller;

import cl.vetnova.soporte.dto.CerrarEscalamientoRequest;
import cl.vetnova.soporte.dto.EscalamientoRequest;
import cl.vetnova.soporte.dto.GestionarEscalamientoRequest;
import cl.vetnova.soporte.model.EscalamientoTicket;
import cl.vetnova.soporte.service.EscalamientoTicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/escalamientos")
public class EscalamientoTicketController {

    private final EscalamientoTicketService service;

    public EscalamientoTicketController(EscalamientoTicketService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EscalamientoTicket> crear(@RequestBody EscalamientoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/{id}/gestionar")
    public ResponseEntity<EscalamientoTicket> gestionar(@PathVariable Long id, @RequestBody GestionarEscalamientoRequest request) {
        return ResponseEntity.ok(service.gestionar(id, request));
    }

    @PutMapping("/{id}/cerrar")
    public ResponseEntity<EscalamientoTicket> cerrar(@PathVariable Long id, @RequestBody CerrarEscalamientoRequest request) {
        return ResponseEntity.ok(service.cerrar(id, request));
    }
}
