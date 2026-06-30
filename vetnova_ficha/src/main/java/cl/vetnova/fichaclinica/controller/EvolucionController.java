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
import cl.vetnova.fichaclinica.model.Evolucion;
import cl.vetnova.fichaclinica.service.EvolucionService;

/**
 * Controlador REST para registrar y consultar evoluciones clínicas bajo /api/v1/evoluciones.
 * Las evoluciones son registros médicos inmutables: una vez creadas no se pueden editar ni eliminar.
 */
@RestController
@RequestMapping("/api/v1/evoluciones")
public class EvolucionController {

    @Autowired
    private EvolucionService evolucionService;

    /**
     * Registra una nueva evolución clínica asociada a una ficha y una cita.
     * @param evolucion datos de la evolución (fichaId, veterinarioId, citaId y descripcion son obligatorios)
     */
    @PostMapping
    public ResponseEntity<Evolucion> crear(@RequestBody Evolucion evolucion) {
        return ResponseEntity.status(HttpStatus.CREATED).body(evolucionService.crear(evolucion));
    }

    /**
     * Retorna todas las evoluciones registradas en el sistema.
     */
    @GetMapping
    public ResponseEntity<List<Evolucion>> listar() {
        return ResponseEntity.ok(evolucionService.listar());
    }

    /**
     * Retorna las evoluciones de una ficha clínica específica, ordenadas cronológicamente.
     * @param fichaId ID de la ficha cuyas evoluciones se quieren consultar
     */
    @GetMapping(params = "fichaId")
    public ResponseEntity<List<Evolucion>> listarPorFicha(@RequestParam Long fichaId) {
        return ResponseEntity.ok(evolucionService.listarPorFicha(fichaId));
    }

    /**
     * Endpoint bloqueado: las evoluciones no pueden modificarse para garantizar integridad del historial.
     * Siempre lanza RegistroInmutableException con HTTP 409.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @RequestBody Evolucion evolucion) {
        throw new RegistroInmutableException("Las evoluciones no pueden modificarse una vez registradas");
    }

    /**
     * Endpoint bloqueado: las evoluciones no pueden eliminarse para preservar el historial clínico.
     * Siempre lanza RegistroInmutableException con HTTP 409.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        throw new RegistroInmutableException("Las evoluciones no pueden eliminarse");
    }
}
