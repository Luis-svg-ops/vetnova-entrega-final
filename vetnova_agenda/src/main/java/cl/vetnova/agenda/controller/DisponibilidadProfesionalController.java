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

import cl.vetnova.agenda.model.DisponibilidadProfesional;
import cl.vetnova.agenda.service.DisponibilidadProfesionalService;

/**
 * Controlador REST para administrar los horarios de disponibilidad de los veterinarios.
 * Permite registrar en qué días y horas cada veterinario puede atender en cada sucursal.
 */
@RestController
@RequestMapping("/api/v1/disponibilidad")

public class DisponibilidadProfesionalController {

    @Autowired
    private DisponibilidadProfesionalService disponibilidadService;

    /**
     * Registra un nuevo horario de disponibilidad para un veterinario.
     * @param disponibilidad objeto con día, hora inicio/fin, sucursal y veterinarioId
     * @return la disponibilidad creada con estado HTTP 201
     */
    @PostMapping
    public ResponseEntity<DisponibilidadProfesional> crear(
            @RequestBody DisponibilidadProfesional disponibilidad){
        return ResponseEntity.status(HttpStatus.CREATED).body(disponibilidadService.crear(disponibilidad));
    }

    /**
     * Retorna todos los horarios de disponibilidad registrados.
     * @return lista completa de disponibilidades con estado HTTP 200
     */
    @GetMapping
    public ResponseEntity<List<DisponibilidadProfesional>> listar(){
        return ResponseEntity.ok(disponibilidadService.listar());
    }

    /**
     * Busca un horario de disponibilidad específico por su id.
     * @param id identificador de la disponibilidad
     * @return la disponibilidad encontrada o excepción si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<DisponibilidadProfesional> obtenerPorId(@PathVariable Long id){
        return ResponseEntity.ok(disponibilidadService.obtenerPorId(id));
    }

    /**
     * Actualiza los datos de un horario de disponibilidad existente.
     * @param id identificador de la disponibilidad a modificar
     * @param disponibilidad nuevos datos (día, horas, sucursal)
     * @return la disponibilidad actualizada; falla si hay citas en los próximos 7 días
     */
    @PutMapping("/{id}")
    public ResponseEntity<DisponibilidadProfesional> actualizar(@PathVariable Long id,
            @RequestBody DisponibilidadProfesional disponibilidad){
        return ResponseEntity.ok(disponibilidadService.actualizar(id, disponibilidad));
    }

    /**
     * Activa un horario de disponibilidad previamente desactivado.
     * @param id identificador de la disponibilidad a activar
     * @return la disponibilidad con el flag activa = true
     */
    @PutMapping("/{id}/activar")
    public ResponseEntity<DisponibilidadProfesional> activar(@PathVariable Long id){
        return ResponseEntity.ok(disponibilidadService.activar(id));
    }

    /**
     * Desactiva un horario de disponibilidad sin eliminarlo del sistema.
     * @param id identificador de la disponibilidad a desactivar
     * @return la disponibilidad con el flag activa = false
     */
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<DisponibilidadProfesional> desactivar(@PathVariable Long id){
        return ResponseEntity.ok(disponibilidadService.desactivar(id));
    }

    /**
     * Elimina un horario de disponibilidad; falla si el veterinario tiene citas futuras.
     * @param id identificador de la disponibilidad a eliminar
     * @return respuesta vacía con estado HTTP 200
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        disponibilidadService.eliminar(id);
        return ResponseEntity.ok().build();
    }
}
