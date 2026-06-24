package cl.vetnova.envio.controller;

import cl.vetnova.envio.dto.ActualizarEstadoRequest;
import cl.vetnova.envio.dto.CrearEnvioRequest;
import cl.vetnova.envio.dto.EnvioResponse;
import cl.vetnova.envio.dto.TrackingResponse;
import cl.vetnova.envio.service.EnvioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/envios")
public class EnvioController {

    private final EnvioService envioService;

    public EnvioController(EnvioService envioService) {
        this.envioService = envioService;
    }

    @PostMapping
    public ResponseEntity<EnvioResponse> crear(@Valid @RequestBody CrearEnvioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(envioService.crearEnvio(request));
    }

    @GetMapping
    public ResponseEntity<List<EnvioResponse>> listar() {
        return ResponseEntity.ok(envioService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnvioResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(envioService.obtenerPorId(id));
    }

    @GetMapping("/{id}/tracking")
    public ResponseEntity<List<TrackingResponse>> obtenerTracking(@PathVariable Long id) {
        return ResponseEntity.ok(envioService.obtenerTracking(id));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<EnvioResponse> actualizarEstado(@PathVariable Long id,
                                                          @Valid @RequestBody ActualizarEstadoRequest request) {
        return ResponseEntity.ok(envioService.actualizarEstado(id, request));
    }
}
