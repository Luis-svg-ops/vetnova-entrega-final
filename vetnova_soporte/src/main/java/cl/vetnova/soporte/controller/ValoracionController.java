package cl.vetnova.soporte.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.vetnova.soporte.dto.PromedioValoracionResponse;
import cl.vetnova.soporte.exception.RegistroInmutableException;
import cl.vetnova.soporte.model.Valoracion;
import cl.vetnova.soporte.service.ValoracionService;

@RestController
@RequestMapping("/valoraciones")
public class ValoracionController {

    private final ValoracionService valoracionService;

    public ValoracionController(ValoracionService valoracionService) {
        this.valoracionService = valoracionService;
    }

    @PostMapping
    public ResponseEntity<Valoracion> crear(@RequestBody Valoracion valoracion) {
        return ResponseEntity.status(HttpStatus.CREATED).body(valoracionService.crear(valoracion));
    }

    @GetMapping("/promedio")
    public ResponseEntity<PromedioValoracionResponse> promedio(@RequestParam String sucursalId) {
        return ResponseEntity.ok(valoracionService.promedioPorSucursal(sucursalId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @RequestBody Valoracion valoracion) {
        throw new RegistroInmutableException("Las valoraciones no pueden modificarse ni eliminarse");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        throw new RegistroInmutableException("Las valoraciones no pueden modificarse ni eliminarse");
    }
}
