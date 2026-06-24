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

@RestController
@RequestMapping("/api/v1/boxes")

public class BoxController {

    @Autowired
    private BoxService boxService;

    @PostMapping
    public ResponseEntity<Box> crear(@Valid @RequestBody Box box){
        return ResponseEntity.status(HttpStatus.CREATED).body(boxService.crear(box));
    }

    @GetMapping
    public ResponseEntity<List<Box>> listar(){
        return ResponseEntity.ok(boxService.listar());
    }

    @PutMapping("/{id}/reservar")
    public ResponseEntity<Box> reservar(@PathVariable Long id){
        return ResponseEntity.ok(boxService.reservar(id));
    }

    @PutMapping("/{id}/liberar")
    public ResponseEntity<Box> liberar(@PathVariable Long id){
        return ResponseEntity.ok(boxService.liberar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        boxService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}