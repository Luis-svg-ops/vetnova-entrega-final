package cl.vetnova.fichaclinica.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.vetnova.fichaclinica.dto.MascotaDesactivacionResponse;
import cl.vetnova.fichaclinica.dto.MascotaResponse;
import cl.vetnova.fichaclinica.model.Mascota;
import cl.vetnova.fichaclinica.service.MascotaService;

/**
 * Controlador REST para gestionar mascotas registradas en el sistema VetNova.
 * Expone los endpoints CRUD bajo /api/v1/mascotas y aplica soft-delete en lugar de borrado físico.
 */
@RestController
@RequestMapping("/api/v1/mascotas")
public class MascotaController {

    @Autowired
    private MascotaService mascotaService;

    /**
     * Retorna todas las mascotas activas e inactivas enriquecidas con el nombre del cliente.
     * El nombre del cliente se obtiene llamando al microservicio vetnova_auth (puerto 8081).
     */
    @GetMapping
    public ResponseEntity<List<MascotaResponse>> listar() {
        return ResponseEntity.ok(mascotaService.listarConCliente());
    }

    /**
     * Retorna una mascota por su ID junto con el nombre del cliente propietario.
     * @param id identificador de la mascota
     */
    @GetMapping("/{id}")
    public ResponseEntity<MascotaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mascotaService.obtenerPorIdConCliente(id));
    }

    /**
     * Registra una nueva mascota y crea automáticamente su ficha clínica vacía.
     * @param mascota datos de la mascota a registrar (clienteId y nombre son obligatorios)
     */
    @PostMapping
    public ResponseEntity<Mascota> crear(@RequestBody Mascota mascota) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mascotaService.crear(mascota));
    }

    /**
     * Actualiza los datos editables de una mascota existente (nombre, peso, etc.).
     * @param id identificador de la mascota; @param mascota datos nuevos a aplicar
     */
    @PutMapping("/{id}")
    public ResponseEntity<Mascota> actualizar(@PathVariable Long id, @RequestBody Mascota mascota) {
        return ResponseEntity.ok(mascotaService.actualizar(id, mascota));
    }

    /**
     * Realiza un soft-delete: pone activo=false en lugar de borrar el registro de la BD.
     * Esto preserva el historial clínico aunque la mascota ya no esté vigente.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<MascotaDesactivacionResponse> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(mascotaService.desactivar(id));
    }
}