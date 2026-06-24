package cl.vetnova.fichaclinica.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.fichaclinica.exception.BusinessRuleException;
import cl.vetnova.fichaclinica.exception.ConflictException;
import cl.vetnova.fichaclinica.exception.ResourceNotFoundException;
import cl.vetnova.fichaclinica.model.FichaClinica;
import cl.vetnova.fichaclinica.repository.FichaClinicaRepository;
import cl.vetnova.fichaclinica.repository.MascotaRepository;

@Service
public class FichaClinicaService {

    @Autowired
    private FichaClinicaRepository fichaClinicaRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    public List<FichaClinica> listar() {
        return fichaClinicaRepository.findAll();
    }

    public FichaClinica obtenerPorId(Long id) {
        return fichaClinicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ficha clínica no encontrada con id " + id));
    }

    // CA-FIC-18: búsqueda de la ficha por mascota.
    public FichaClinica buscarPorMascota(Long mascotaId) {
        return fichaClinicaRepository.findByMascotaId(mascotaId)
                .orElseThrow(() -> new ResourceNotFoundException("Ficha clínica no encontrada para la mascota"));
    }

    // CA-FIC-01/02/03/05: crea una ficha para una mascota que aún no tenga una.
    public FichaClinica crear(FichaClinica ficha) {
        if (ficha.getMascotaId() == null) {
            throw new BusinessRuleException("El mascotaId es obligatorio");
        }
        if (!mascotaRepository.existsById(ficha.getMascotaId())) {
            throw new ResourceNotFoundException("Mascota no encontrada");
        }
        if (fichaClinicaRepository.existsByMascotaId(ficha.getMascotaId())) {
            throw new ConflictException("La mascota ya tiene una ficha clínica");
        }
        ficha.setFechaCreacion(Date.valueOf(LocalDate.now()));
        return fichaClinicaRepository.save(ficha);
    }
}
