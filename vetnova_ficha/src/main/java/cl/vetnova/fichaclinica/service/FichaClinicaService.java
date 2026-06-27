package cl.vetnova.fichaclinica.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.fichaclinica.dto.FichaClinicaResponse;
import cl.vetnova.fichaclinica.exception.BusinessRuleException;
import cl.vetnova.fichaclinica.exception.ConflictException;
import cl.vetnova.fichaclinica.exception.ResourceNotFoundException;
import cl.vetnova.fichaclinica.model.FichaClinica;
import cl.vetnova.fichaclinica.model.Mascota;
import cl.vetnova.fichaclinica.repository.FichaClinicaRepository;
import cl.vetnova.fichaclinica.repository.MascotaRepository;

@Service
public class FichaClinicaService {

    @Autowired
    private FichaClinicaRepository fichaClinicaRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    public List<FichaClinicaResponse> listar() {
        return fichaClinicaRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public FichaClinicaResponse obtenerPorId(Long id) {
        FichaClinica ficha = fichaClinicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ficha clínica no encontrada con id " + id));
        return toResponse(ficha);
    }

    public FichaClinicaResponse buscarPorMascota(Long mascotaId) {
        FichaClinica ficha = fichaClinicaRepository.findByMascotaId(mascotaId)
                .orElseThrow(() -> new ResourceNotFoundException("Ficha clínica no encontrada para la mascota"));
        return toResponse(ficha);
    }

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

    private FichaClinicaResponse toResponse(FichaClinica ficha) {
        Mascota mascota = mascotaRepository.findById(ficha.getMascotaId()).orElse(null);
        String nombreMascota = mascota != null ? mascota.getNombre() : "Desconocida";
        String especie = mascota != null ? mascota.getEspecie() : null;
        Long clienteId = mascota != null ? mascota.getClienteId() : null;
        return new FichaClinicaResponse(
                ficha.getId(),
                ficha.getMascotaId(),
                nombreMascota,
                especie,
                clienteId,
                ficha.getFechaCreacion(),
                ficha.getObservacionesGenerales()
        );
    }
}
