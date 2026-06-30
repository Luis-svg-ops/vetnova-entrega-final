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
import cl.vetnova.fichaclinica.model.Vacuna;
import cl.vetnova.fichaclinica.service.VacunaService;

/**
 * Controlador REST para gestionar registros de vacunas bajo /api/v1/vacunas.
 * Las vacunas son inmutables: una vez registradas no se pueden modificar ni eliminar.
 */
@RestController
@RequestMapping("/api/v1/vacunas")
public class VacunaController {

    @Autowired
    private VacunaService vacunaService;

    /**
     * Registra una nueva vacuna en la ficha clínica de una mascota.
     * @param vacuna datos de la vacuna (fichaId, nombre y fechaAplicacion son obligatorios)
     */
    @PostMapping
    public ResponseEntity<Vacuna> crear(@RequestBody Vacuna vacuna) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vacunaService.crear(vacuna));
    }

    /**
     * Retorna todas las vacunas registradas en el sistema.
     */
    @GetMapping
    public ResponseEntity<List<Vacuna>> listar() {
        return ResponseEntity.ok(vacunaService.listar());
    }

    /**
     * Retorna las vacunas de una ficha clínica específica, ordenadas por fecha de aplicación.
     * @param fichaId ID de la ficha cuyas vacunas se quieren consultar
     */
    @GetMapping(params = "fichaId")
    public ResponseEntity<List<Vacuna>> listarPorFicha(@RequestParam Long fichaId) {
        return ResponseEntity.ok(vacunaService.listarPorFicha(fichaId));
    }

    /**
     * Endpoint bloqueado: los registros de vacunación no pueden modificarse para evitar alteración del historial.
     * Siempre lanza RegistroInmutableException con HTTP 409.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @RequestBody Vacuna vacuna) {
        throw new RegistroInmutableException("Las vacunas no pueden modificarse una vez registradas");
    }

    /**
     * Endpoint bloqueado: los registros de vacunación no pueden eliminarse para preservar el historial clínico.
     * Siempre lanza RegistroInmutableException con HTTP 409.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        throw new RegistroInmutableException("Las vacunas no pueden eliminarse");
    }
}
