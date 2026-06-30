package cl.vetnova.agenda.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.vetnova.agenda.model.BloqueAgenda;
import cl.vetnova.agenda.service.BloqueAgendaService;

/**
 * Controlador REST para gestionar bloques de indisponibilidad en la agenda de un veterinario.
 * Un bloque impide que se agenden citas en el período indicado (vacaciones, capacitaciones, etc.).
 */
@RestController
@RequestMapping("/api/v1/bloques")
public class BloqueAgendaController {

    @Autowired
    private BloqueAgendaService bloqueAgendaService;

    /**
     * Crea un nuevo bloque de agenda; falla si ya existen citas confirmadas en ese período.
     * @param bloque objeto con veterinarioId, fechaInicio, fechaFin, motivo y creadoPor
     * @return el bloque creado con estado HTTP 201
     */
    @PostMapping
    public ResponseEntity<BloqueAgenda> crear(@RequestBody BloqueAgenda bloque){
        return ResponseEntity.status(HttpStatus.CREATED).body(bloqueAgendaService.crear(bloque));
    }

    /**
     * Retorna todos los bloques de agenda registrados en el sistema.
     * @return lista completa de bloques con estado HTTP 200
     */
    @GetMapping
    public ResponseEntity<List<BloqueAgenda>> listar(){
        return ResponseEntity.ok(bloqueAgendaService.listar());
    }

    /**
     * Busca un bloque de agenda específico por su id.
     * @param id identificador del bloque
     * @return el bloque encontrado o excepción si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<BloqueAgenda> obtenerPorId(@PathVariable Long id){
        return ResponseEntity.ok(bloqueAgendaService.obtenerPorId(id));
    }

    /**
     * Elimina un bloque de agenda; no permite eliminar un bloque que ya esté en curso.
     * @param id identificador del bloque a eliminar
     * @return respuesta vacía con estado HTTP 200
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        bloqueAgendaService.eliminar(id);
        return ResponseEntity.ok().build();
    }
}
