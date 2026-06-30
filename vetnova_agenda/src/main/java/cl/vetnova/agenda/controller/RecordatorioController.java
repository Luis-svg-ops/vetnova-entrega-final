package cl.vetnova.agenda.controller;

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
import org.springframework.web.bind.annotation.RestController;

import cl.vetnova.agenda.dto.RecordatorioRequest;
import cl.vetnova.agenda.exception.RegistroInmutableException;
import cl.vetnova.agenda.model.Recordatorio;
import cl.vetnova.agenda.service.RecordatorioService;

/**
 * Controlador REST para consultar y reenviar recordatorios de citas veterinarias.
 * Los recordatorios son inmutables: no se pueden modificar ni eliminar manualmente (solo el sistema los gestiona).
 */
@RestController
@RequestMapping("/api/v1/recordatorios")
public class RecordatorioController {

    @Autowired
    private RecordatorioService recordatorioService;

    /**
     * Retorna todos los recordatorios registrados en el sistema.
     * @return lista completa de recordatorios con estado HTTP 200
     */
    @GetMapping
    public ResponseEntity<List<Recordatorio>> listar() {
        return ResponseEntity.ok(recordatorioService.listar());
    }

    /**
     * Busca un recordatorio específico por su id.
     * @param id identificador del recordatorio
     * @return el recordatorio encontrado o excepción 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<Recordatorio> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(recordatorioService.obtenerPorId(id));
    }

    /**
     * Crea manualmente un recordatorio de tipo EMAIL, SMS o PUSH para una cita existente.
     * @param request DTO con citaId y tipo de recordatorio
     * @return el recordatorio creado con estado HTTP 201
     */
    @PostMapping
    public ResponseEntity<Recordatorio> crear(@RequestBody RecordatorioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recordatorioService.crear(request));
    }

    /**
     * Marca un recordatorio como enviado (simula el reenvío manual de la notificación).
     * @param id identificador del recordatorio a reenviar
     * @return el recordatorio con enviado = true
     */
    @PutMapping("/{id}/reenviar")
    public ResponseEntity<Recordatorio> reenviar(@PathVariable Long id) {
        return ResponseEntity.ok(recordatorioService.reenviar(id));
    }

    /**
     * Intento de actualización rechazado: los recordatorios son inmutables por diseño.
     * @param id no utilizado; el endpoint siempre lanza excepción
     * @param request no utilizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @RequestBody RecordatorioRequest request) {
        // Decisión de diseño: los recordatorios solo los gestiona el sistema, nunca el usuario
        throw new RegistroInmutableException("Recordatorios no modificables manualmente");
    }

    /**
     * Intento de eliminación rechazado: los recordatorios son inmutables por diseño.
     * @param id no utilizado; el endpoint siempre lanza excepción
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        // Decisión de diseño: los recordatorios solo los gestiona el sistema, nunca el usuario
        throw new RegistroInmutableException("Recordatorios no modificables manualmente");
    }
}
