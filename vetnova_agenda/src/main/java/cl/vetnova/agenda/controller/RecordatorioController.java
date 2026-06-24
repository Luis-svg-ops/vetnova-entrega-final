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

@RestController
@RequestMapping("/api/v1/recordatorios")
public class RecordatorioController {

    @Autowired
    private RecordatorioService recordatorioService;

    @GetMapping
    public ResponseEntity<List<Recordatorio>> listar() {
        return ResponseEntity.ok(recordatorioService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recordatorio> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(recordatorioService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Recordatorio> crear(@RequestBody RecordatorioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recordatorioService.crear(request));
    }

    @PutMapping("/{id}/reenviar")
    public ResponseEntity<Recordatorio> reenviar(@PathVariable Long id) {
        return ResponseEntity.ok(recordatorioService.reenviar(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @RequestBody RecordatorioRequest request) {
        throw new RegistroInmutableException("Recordatorios no modificables manualmente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        throw new RegistroInmutableException("Recordatorios no modificables manualmente");
    }
}
