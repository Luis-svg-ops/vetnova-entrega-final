package cl.vetnova.ventas.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.vetnova.ventas.dto.CrearPagoRequest;
import cl.vetnova.ventas.dto.PagoResponse;
import cl.vetnova.ventas.dto.RechazarPagoRequest;
import cl.vetnova.ventas.service.PagoService;

@RestController
@RequestMapping("/api/v1/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping
    public ResponseEntity<PagoResponse> crear(@RequestBody CrearPagoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.crearPago(request));
    }

    @PutMapping("/{id}/procesar")
    public ResponseEntity<PagoResponse> procesar(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.procesar(id));
    }

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<PagoResponse> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.confirmar(id));
    }

    @PutMapping("/{id}/rechazar")
    public ResponseEntity<PagoResponse> rechazar(@PathVariable Long id, @RequestBody RechazarPagoRequest request) {
        return ResponseEntity.ok(pagoService.rechazar(id, request.getMotivo()));
    }

    @PutMapping("/{id}/reembolsar")
    public ResponseEntity<PagoResponse> reembolsar(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.reembolsar(id));
    }
}
