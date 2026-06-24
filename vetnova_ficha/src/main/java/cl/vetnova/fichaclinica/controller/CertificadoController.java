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
import cl.vetnova.fichaclinica.model.Certificado;
import cl.vetnova.fichaclinica.service.CertificadoService;

@RestController
@RequestMapping("/api/v1/certificados")
public class CertificadoController {

    @Autowired
    private CertificadoService certificadoService;

    @PostMapping
    public ResponseEntity<Certificado> crear(@RequestBody Certificado certificado) {
        return ResponseEntity.status(HttpStatus.CREATED).body(certificadoService.crear(certificado));
    }

    @GetMapping
    public ResponseEntity<List<Certificado>> listar() {
        return ResponseEntity.ok(certificadoService.listar());
    }

    @GetMapping(params = "fichaId")
    public ResponseEntity<List<Certificado>> listarPorFicha(@RequestParam Long fichaId) {
        return ResponseEntity.ok(certificadoService.listarPorFicha(fichaId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @RequestBody Certificado certificado) {
        throw new RegistroInmutableException("Los certificados no pueden modificarse una vez emitidos");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        throw new RegistroInmutableException("Los certificados no pueden eliminarse");
    }
}
