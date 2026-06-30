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

/**
 * Controlador REST para registrar y consultar procedimientos médicos bajo /api/v1/procedimientos.
 * Los procedimientos son inmutables: una vez registrados no se pueden modificar ni eliminar.
 */
@RestController
@RequestMapping("/api/v1/procedimientos")
public class ProcedimientoController {

    @Autowired
    private ProcedimientoService procedimientoService;

    /**
     * Registra un nuevo procedimiento médico en la ficha clínica de una mascota.
     * @param procedimiento datos del procedimiento (fichaId, nombre, descripcion y veterinarioId son obligatorios)
     */
    @PostMapping
    public ResponseEntity<Procedimiento> crear(@RequestBody Procedimiento procedimiento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(procedimientoService.crear(procedimiento));
    }

    /**
     * Retorna todos los procedimientos registrados en el sistema.
     */
    @GetMapping
    public ResponseEntity<List<Procedimiento>> listar() {
        return ResponseEntity.ok(procedimientoService.listar());
    }

    /**
     * Retorna los procedimientos de una ficha clínica específica, ordenados cronológicamente.
     * @param fichaId ID de la ficha cuyos procedimientos se quieren consultar
     */
    @GetMapping(params = "fichaId")
    public ResponseEntity<List<Procedimiento>> listarPorFicha(@RequestParam Long fichaId) {
        return ResponseEntity.ok(procedimientoService.listarPorFicha(fichaId));
    }

    /**
     * Endpoint bloqueado: los procedimientos no pueden modificarse para garantizar integridad del historial.
     * Siempre lanza RegistroInmutableException con HTTP 409.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @RequestBody Procedimiento procedimiento) {
        throw new RegistroInmutableException("Los procedimientos no pueden modificarse una vez registrados");
    }

    /**
     * Endpoint bloqueado: los procedimientos no pueden eliminarse para preservar el historial clínico.
     * Siempre lanza RegistroInmutableException con HTTP 409.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        throw new RegistroInmutableException("Los procedimientos no pueden eliminarse");
    }
}
