package cl.vetnova.agenda.controller;

import java.util.List;

import cl.vetnova.agenda.model.Box;

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

import cl.vetnova.agenda.service.BoxService;

import jakarta.validation.Valid;

/**
 * Controlador REST para gestionar los boxes (consultorios físicos) de la clínica.
 * Expone endpoints para crear, listar, reservar, liberar y eliminar boxes.
 */
@RestController
@RequestMapping("/api/v1/boxes")

public class BoxController {

    @Autowired
    private BoxService boxService;

    /**
     * Crea un nuevo box validando que nombre y sucursal no sean nulos.
     * @param box objeto Box con los datos del consultorio a registrar
     * @return el box creado con estado HTTP 201
     */
    @PostMapping
    public ResponseEntity<Box> crear(@Valid @RequestBody Box box){
        return ResponseEntity.status(HttpStatus.CREATED).body(boxService.crear(box));
    }

    /**
     * Retorna todos los boxes registrados en el sistema.
     * @return lista completa de boxes con estado HTTP 200
     */
    @GetMapping
    public ResponseEntity<List<Box>> listar(){
        return ResponseEntity.ok(boxService.listar());
    }

    /**
     * Marca un box como no disponible (reservado) para una cita.
     * @param id identificador del box a reservar
     * @return el box actualizado; lanza excepción si ya está reservado
     */
    @PutMapping("/{id}/reservar")
    public ResponseEntity<Box> reservar(@PathVariable Long id){
        return ResponseEntity.ok(boxService.reservar(id));
    }

    /**
     * Marca un box como disponible nuevamente tras finalizar una cita.
     * @param id identificador del box a liberar
     * @return el box actualizado; lanza excepción si ya está disponible
     */
    @PutMapping("/{id}/liberar")
    public ResponseEntity<Box> liberar(@PathVariable Long id){
        return ResponseEntity.ok(boxService.liberar(id));
    }

    /**
     * Elimina permanentemente un box del sistema por su id.
     * @param id identificador del box a eliminar
     * @return respuesta vacía con estado HTTP 204
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        boxService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}