package cl.vetnova.fichaclinica.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.vetnova.fichaclinica.dto.FichaClinicaRequest;
import cl.vetnova.fichaclinica.exception.RegistroInmutableException;
import cl.vetnova.fichaclinica.model.FichaClinica;
import cl.vetnova.fichaclinica.service.FichaClinicaService;

@RestController
@RequestMapping("/api/v1/fichas")
public class FichaClinicaController {

    @Autowired
    private FichaClinicaService fichaClinicaService;

    @PostMapping
    public ResponseEntity<FichaClinica> crear(@RequestBody FichaClinicaRequest request) {
        FichaClinica fichaClinica = new FichaClinica();
        fichaClinica.setMascotaId(request.mascotaId());
        fichaClinica.setObservacionesGenerales(request.observacionesGenerales());
        return ResponseEntity.status(HttpStatus.CREATED).body(fichaClinicaService.crear(fichaClinica));
    }

    @GetMapping
    public ResponseEntity<List<FichaClinica>> listar() {
        return ResponseEntity.ok(fichaClinicaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FichaClinica> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(fichaClinicaService.obtenerPorId(id));
    }

    @GetMapping(params = "mascotaId")
    public ResponseEntity<FichaClinica> buscarPorMascota(@RequestParam Long mascotaId) {
        return ResponseEntity.ok(fichaClinicaService.buscarPorMascota(mascotaId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        throw new RegistroInmutableException("Las fichas clínicas no pueden eliminarse");
    }
}
