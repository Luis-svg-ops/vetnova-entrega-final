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

@RestController
@RequestMapping("/api/v1/bloques")
public class BloqueAgendaController {

    @Autowired
    private BloqueAgendaService bloqueAgendaService;

    @PostMapping
    public ResponseEntity<BloqueAgenda> crear(@RequestBody BloqueAgenda bloque){
        return ResponseEntity.status(HttpStatus.CREATED).body(bloqueAgendaService.crear(bloque));
    }

    @GetMapping
    public ResponseEntity<List<BloqueAgenda>> listar(){
        return ResponseEntity.ok(bloqueAgendaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BloqueAgenda> obtenerPorId(@PathVariable Long id){
        return ResponseEntity.ok(bloqueAgendaService.obtenerPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        bloqueAgendaService.eliminar(id);
        return ResponseEntity.ok().build();
    }
}
