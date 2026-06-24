package cl.vetnova.auth.controller;

import cl.vetnova.auth.dto.ApiResponse;
import cl.vetnova.auth.dto.AuditoriaResponse;
import cl.vetnova.auth.dto.CrearAuditoriaRequest;
import cl.vetnova.auth.exception.RegistroInmutableException;
import cl.vetnova.auth.service.AuditoriaService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {
    private final AuditoriaService service;

    public AuditoriaController(AuditoriaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AuditoriaResponse>> crear(@RequestBody CrearAuditoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Registro creado", service.crear(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AuditoriaResponse>>> consultar(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) String accion) {
        return ResponseEntity.ok(ApiResponse.ok("Registros encontrados", service.consultar(usuarioId, accion)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> modificar(@PathVariable Long id) {
        throw new RegistroInmutableException("Los registros de auditoría no pueden ser modificados");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        throw new RegistroInmutableException("Los registros de auditoría no pueden ser eliminados");
    }
}
