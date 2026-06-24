package cl.vetnova.fichaclinica.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import cl.vetnova.fichaclinica.exception.RegistroInmutableException;
import cl.vetnova.fichaclinica.model.Procedimiento;
import cl.vetnova.fichaclinica.service.ProcedimientoService;

@RestController
@RequestMapping("/api/v1/procedimientos")
public class ProcedimientoController {

    @Autowired
    private ProcedimientoService procedimientoService;

    @PostMapping
    public ResponseEntity<Procedimiento> crear(@RequestBody Procedimiento procedimiento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(procedimientoService.crear(procedimiento));
    }

    @GetMapping
    public ResponseEntity<List<Procedimiento>> listar() {
        return ResponseEntity.ok(procedimientoService.listar());
    }

    @GetMapping(params = "fichaId")
    public ResponseEntity<List<Procedimiento>> listarPorFicha(@RequestParam Long fichaId) {
        return ResponseEntity.ok(procedimientoService.listarPorFicha(fichaId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @RequestBody Procedimiento procedimiento) {
        throw new RegistroInmutableException("Los procedimientos no pueden modificarse una vez registrados");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        throw new RegistroInmutableException("Los procedimientos no pueden eliminarse");
    }
}
