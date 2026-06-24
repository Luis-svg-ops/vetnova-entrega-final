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

    
@RestController
@RequestMapping("/api/v1/disponibilidad")

public class DisponibilidadProfesionalController {

    @Autowired
    private DisponibilidadProfesionalService disponibilidadService;

    @PostMapping
    public ResponseEntity<DisponibilidadProfesional> crear(
            @RequestBody DisponibilidadProfesional disponibilidad){
        return ResponseEntity.status(HttpStatus.CREATED).body(disponibilidadService.crear(disponibilidad));
    }

    @GetMapping
    public ResponseEntity<List<DisponibilidadProfesional>> listar(){
        return ResponseEntity.ok(disponibilidadService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisponibilidadProfesional> obtenerPorId(@PathVariable Long id){
        return ResponseEntity.ok(disponibilidadService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DisponibilidadProfesional> actualizar(@PathVariable Long id,
            @RequestBody DisponibilidadProfesional disponibilidad){
        return ResponseEntity.ok(disponibilidadService.actualizar(id, disponibilidad));
    }

    @PutMapping("/{id}/activar")
    public ResponseEntity<DisponibilidadProfesional> activar(@PathVariable Long id){
        return ResponseEntity.ok(disponibilidadService.activar(id));
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<DisponibilidadProfesional> desactivar(@PathVariable Long id){
        return ResponseEntity.ok(disponibilidadService.desactivar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        disponibilidadService.eliminar(id);
        return ResponseEntity.ok().build();
    }
}
