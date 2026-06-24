package cl.vetnova.laboratorio.controller;

import cl.vetnova.laboratorio.dto.RegistrarResultadoRequest;
import cl.vetnova.laboratorio.model.ResultadoExamen;
import cl.vetnova.laboratorio.service.ResultadoExamenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resultados-examen")
public class ResultadoExamenController {

    private final ResultadoExamenService service;

    public ResultadoExamenController(ResultadoExamenService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ResultadoExamen> registrar(@RequestBody RegistrarResultadoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(request));
    }

    @PutMapping("/{id}/publicar")
    public ResponseEntity<ResultadoExamen> publicar(@PathVariable Long id) {
        return ResponseEntity.ok(service.publicar(id));
    }
}
