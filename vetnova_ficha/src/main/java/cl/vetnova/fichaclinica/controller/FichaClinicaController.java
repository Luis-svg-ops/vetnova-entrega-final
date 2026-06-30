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
import cl.vetnova.fichaclinica.dto.FichaClinicaResponse;
import cl.vetnova.fichaclinica.exception.RegistroInmutableException;
import cl.vetnova.fichaclinica.model.FichaClinica;
import cl.vetnova.fichaclinica.service.FichaClinicaService;

/**
 * Controlador REST para gestionar fichas clínicas bajo /api/v1/fichas.
 * Cada mascota tiene exactamente una ficha; las fichas son inmutables y no pueden eliminarse.
 */
@RestController
@RequestMapping("/api/v1/fichas")
public class FichaClinicaController {

    @Autowired
    private FichaClinicaService fichaClinicaService;

    /**
     * Crea manualmente una ficha clínica para una mascota (normalmente se crea al registrar la mascota).
     * @param request contiene mascotaId y observaciones generales opcionales
     */
    @PostMapping
    public ResponseEntity<FichaClinica> crear(@RequestBody FichaClinicaRequest request) {
        FichaClinica fichaClinica = new FichaClinica();
        fichaClinica.setMascotaId(request.mascotaId());
        fichaClinica.setObservacionesGenerales(request.observacionesGenerales());
        return ResponseEntity.status(HttpStatus.CREATED).body(fichaClinicaService.crear(fichaClinica));
    }

    /**
     * Retorna todas las fichas clínicas registradas en el sistema.
     */
    @GetMapping
    public ResponseEntity<List<FichaClinicaResponse>> listar() {
        return ResponseEntity.ok(fichaClinicaService.listar());
    }

    /**
     * Retorna una ficha clínica por su ID, incluyendo datos de la mascota asociada.
     * @param id identificador de la ficha clínica
     */
    @GetMapping("/{id}")
    public ResponseEntity<FichaClinicaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(fichaClinicaService.obtenerPorId(id));
    }

    /**
     * Busca la ficha clínica de una mascota específica usando su ID como query param (?mascotaId=X).
     * @param mascotaId ID de la mascota cuya ficha se desea obtener
     */
    @GetMapping(params = "mascotaId")
    public ResponseEntity<FichaClinicaResponse> buscarPorMascota(@RequestParam Long mascotaId) {
        return ResponseEntity.ok(fichaClinicaService.buscarPorMascota(mascotaId));
    }

    /**
     * Endpoint bloqueado por diseño: las fichas clínicas son inmutables para proteger el historial médico.
     * Siempre lanza RegistroInmutableException con HTTP 409.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        throw new RegistroInmutableException("Las fichas clínicas no pueden eliminarse");
    }
}
