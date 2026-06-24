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

@RestController
@RequestMapping("/api/v1/recetas")
public class RecetaController {

    @Autowired
    private RecetaService recetaService;

    @PostMapping
    public ResponseEntity<Receta> crear(@RequestBody RecetaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recetaService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<Receta>> listar() {
        return ResponseEntity.ok(recetaService.listar());
    }

    @GetMapping(params = "fichaId")
    public ResponseEntity<List<Receta>> listarPorFicha(@RequestParam Long fichaId) {
        return ResponseEntity.ok(recetaService.listarPorFicha(fichaId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @RequestBody RecetaRequest request) {
        throw new RegistroInmutableException("Las recetas no pueden modificarse una vez emitidas");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        throw new RegistroInmutableException("Las recetas no pueden eliminarse");
    }
}
