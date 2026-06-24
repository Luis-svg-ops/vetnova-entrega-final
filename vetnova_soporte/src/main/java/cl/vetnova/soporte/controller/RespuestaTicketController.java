package cl.vetnova.soporte.controller;

import cl.vetnova.soporte.dto.RespuestaRequest;
import cl.vetnova.soporte.exception.RegistroInmutableException;
import cl.vetnova.soporte.model.RespuestaTicket;
import cl.vetnova.soporte.service.RespuestaTicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/respuestas-ticket")
public class RespuestaTicketController {

    private final RespuestaTicketService service;

    public RespuestaTicketController(RespuestaTicketService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<RespuestaTicket> registrar(@RequestBody RespuestaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @RequestBody RespuestaRequest request) {
        throw new RegistroInmutableException("Las respuestas no pueden modificarse ni eliminarse");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        throw new RegistroInmutableException("Las respuestas no pueden modificarse ni eliminarse");
    }
}
