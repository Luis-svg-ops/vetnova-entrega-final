package cl.vetnova.soporte.controller;

import cl.vetnova.soporte.dto.CerrarTicketRequest;
import cl.vetnova.soporte.dto.ClasificarTicketRequest;
import cl.vetnova.soporte.dto.CrearTicketRequest;
import cl.vetnova.soporte.dto.DerivarTicketRequest;
import cl.vetnova.soporte.dto.ResponderTicketRequest;
import cl.vetnova.soporte.model.DerivacionTicket;
import cl.vetnova.soporte.model.RespuestaTicket;
import cl.vetnova.soporte.model.Ticket;
import cl.vetnova.soporte.model.Valoracion;
import cl.vetnova.soporte.service.DerivacionTicketService;
import cl.vetnova.soporte.service.RespuestaTicketService;
import cl.vetnova.soporte.service.TicketService;
import cl.vetnova.soporte.service.ValoracionService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final DerivacionTicketService derivacionService;
    private final RespuestaTicketService respuestaService;
    private final ValoracionService valoracionService;

    public TicketController(TicketService ticketService, DerivacionTicketService derivacionService,
                            RespuestaTicketService respuestaService, ValoracionService valoracionService) {
        this.ticketService = ticketService;
        this.derivacionService = derivacionService;
        this.respuestaService = respuestaService;
        this.valoracionService = valoracionService;
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> listar(@RequestParam(required = false) String estado) {
        return ResponseEntity.ok(ticketService.listar(estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.buscar(id));
    }

    @PostMapping
    public ResponseEntity<Ticket> crear(@RequestBody CrearTicketRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.crear(request));
    }

    @PutMapping("/{id}/clasificar")
    public ResponseEntity<Ticket> clasificar(@PathVariable Long id, @RequestBody ClasificarTicketRequest request) {
        return ResponseEntity.ok(ticketService.clasificar(id, request));
    }

    @PutMapping("/{id}/derivar")
    public ResponseEntity<Ticket> derivar(@PathVariable Long id, @RequestBody DerivarTicketRequest request) {
        return ResponseEntity.ok(ticketService.derivar(id, request));
    }

    @PutMapping("/{id}/escalar")
    public ResponseEntity<Ticket> escalar(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.escalar(id));
    }

    @PostMapping("/{id}/respuestas")
    public ResponseEntity<RespuestaTicket> responder(@PathVariable Long id, @RequestBody ResponderTicketRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.responder(id, request));
    }

    @PutMapping("/{id}/cerrar")
    public ResponseEntity<Ticket> cerrar(@PathVariable Long id, @RequestBody CerrarTicketRequest request) {
        return ResponseEntity.ok(ticketService.cerrar(id, request));
    }

    @GetMapping("/{id}/derivaciones")
    public ResponseEntity<List<DerivacionTicket>> derivaciones(@PathVariable Long id) {
        return ResponseEntity.ok(derivacionService.listarPorTicket(id));
    }

    @GetMapping("/{id}/respuestas")
    public ResponseEntity<List<RespuestaTicket>> respuestas(@PathVariable Long id) {
        return ResponseEntity.ok(respuestaService.listarPorTicket(id));
    }

    @GetMapping("/{id}/valoracion")
    public ResponseEntity<Valoracion> valoracion(@PathVariable Long id) {
        return ResponseEntity.ok(valoracionService.obtenerPorTicket(id));
    }
}
