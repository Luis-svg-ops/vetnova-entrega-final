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

/**
 * Controlador REST para emitir y consultar certificados veterinarios bajo /api/v1/certificados.
 * Los certificados son documentos oficiales inmutables: una vez emitidos no se pueden modificar ni eliminar.
 */
@RestController
@RequestMapping("/api/v1/certificados")
public class CertificadoController {

    @Autowired
    private CertificadoService certificadoService;

    /**
     * Emite un certificado veterinario para una ficha clínica (tipos: SALUD, VACUNACION, VIAJE, ADOPCION).
     * @param certificado datos del certificado (fichaId, veterinarioId y tipo son obligatorios)
     */
    @PostMapping
    public ResponseEntity<Certificado> crear(@RequestBody Certificado certificado) {
        return ResponseEntity.status(HttpStatus.CREATED).body(certificadoService.crear(certificado));
    }

    /**
     * Retorna todos los certificados emitidos en el sistema.
     */
    @GetMapping
    public ResponseEntity<List<Certificado>> listar() {
        return ResponseEntity.ok(certificadoService.listar());
    }

    /**
     * Retorna los certificados de una ficha clínica específica.
     * @param fichaId ID de la ficha cuyos certificados se quieren consultar
     */
    @GetMapping(params = "fichaId")
    public ResponseEntity<List<Certificado>> listarPorFicha(@RequestParam Long fichaId) {
        return ResponseEntity.ok(certificadoService.listarPorFicha(fichaId));
    }

    /**
     * Endpoint bloqueado: los certificados no pueden modificarse una vez emitidos (documento oficial).
     * Siempre lanza RegistroInmutableException con HTTP 409.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @RequestBody Certificado certificado) {
        throw new RegistroInmutableException("Los certificados no pueden modificarse una vez emitidos");
    }

    /**
     * Endpoint bloqueado: los certificados no pueden eliminarse para preservar el historial clínico.
     * Siempre lanza RegistroInmutableException con HTTP 409.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        throw new RegistroInmutableException("Los certificados no pueden eliminarse");
    }
}
