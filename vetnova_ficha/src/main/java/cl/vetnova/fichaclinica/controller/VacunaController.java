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

@RestController
@RequestMapping("/api/v1/vacunas")
public class VacunaController {

    @Autowired
    private VacunaService vacunaService;

    @PostMapping
    public ResponseEntity<Vacuna> crear(@RequestBody Vacuna vacuna) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vacunaService.crear(vacuna));
    }

    @GetMapping
    public ResponseEntity<List<Vacuna>> listar() {
        return ResponseEntity.ok(vacunaService.listar());
    }

    @GetMapping(params = "fichaId")
    public ResponseEntity<List<Vacuna>> listarPorFicha(@RequestParam Long fichaId) {
        return ResponseEntity.ok(vacunaService.listarPorFicha(fichaId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @RequestBody Vacuna vacuna) {
        throw new RegistroInmutableException("Las vacunas no pueden modificarse una vez registradas");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        throw new RegistroInmutableException("Las vacunas no pueden eliminarse");
    }
}
