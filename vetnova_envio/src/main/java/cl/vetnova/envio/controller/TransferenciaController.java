package cl.vetnova.envio.controller;

import cl.vetnova.envio.dto.CrearTransferenciaRequest;
import cl.vetnova.envio.dto.TransferenciaResponse;
import cl.vetnova.envio.service.TransferenciaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transferencias")
public class TransferenciaController {

    private final TransferenciaService transferenciaService;

    public TransferenciaController(TransferenciaService transferenciaService) {
        this.transferenciaService = transferenciaService;
    }

    @PostMapping
    public ResponseEntity<TransferenciaResponse> crear(@Valid @RequestBody CrearTransferenciaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transferenciaService.crearTransferencia(request));
    }

    @GetMapping
    public ResponseEntity<List<TransferenciaResponse>> listar() {
        return ResponseEntity.ok(transferenciaService.listar());
    }
}
