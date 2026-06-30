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

import cl.vetnova.fichaclinica.dto.RecetaRequest;
import cl.vetnova.fichaclinica.exception.RegistroInmutableException;
import cl.vetnova.fichaclinica.model.Receta;
import cl.vetnova.fichaclinica.service.RecetaService;

/**
 * Controlador REST para emitir y consultar recetas médicas bajo /api/v1/recetas.
 * Las recetas son documentos legales inmutables: una vez emitidas no pueden modificarse ni eliminarse.
 */
@RestController
@RequestMapping("/api/v1/recetas")
public class RecetaController {

    @Autowired
    private RecetaService recetaService;

    /**
     * Emite una nueva receta médica para una ficha clínica con uno o más medicamentos.
     * @param request datos de la receta (fichaId, veterinarioId y lista de medicamentos son obligatorios)
     */
    @PostMapping
    public ResponseEntity<Receta> crear(@RequestBody RecetaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recetaService.crear(request));
    }

    /**
     * Retorna todas las recetas emitidas en el sistema.
     */
    @GetMapping
    public ResponseEntity<List<Receta>> listar() {
        return ResponseEntity.ok(recetaService.listar());
    }

    /**
     * Retorna las recetas de una ficha clínica específica, ordenadas de más reciente a más antigua.
     * @param fichaId ID de la ficha cuyas recetas se quieren consultar
     */
    @GetMapping(params = "fichaId")
    public ResponseEntity<List<Receta>> listarPorFicha(@RequestParam Long fichaId) {
        return ResponseEntity.ok(recetaService.listarPorFicha(fichaId));
    }

    /**
     * Endpoint bloqueado: las recetas médicas no pueden modificarse una vez emitidas (documento legal).
     * Siempre lanza RegistroInmutableException con HTTP 409.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @RequestBody RecetaRequest request) {
        throw new RegistroInmutableException("Las recetas no pueden modificarse una vez emitidas");
    }

    /**
     * Endpoint bloqueado: las recetas médicas no pueden eliminarse para preservar el historial clínico.
     * Siempre lanza RegistroInmutableException con HTTP 409.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        throw new RegistroInmutableException("Las recetas no pueden eliminarse");
    }
}
