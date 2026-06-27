package cl.vetnova.fichaclinica.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.fichaclinica.client.AuthClient;
import cl.vetnova.fichaclinica.dto.MascotaDesactivacionResponse;
import cl.vetnova.fichaclinica.dto.MascotaResponse;
import cl.vetnova.fichaclinica.exception.BusinessRuleException;
import cl.vetnova.fichaclinica.exception.ConflictException;
import cl.vetnova.fichaclinica.exception.ResourceNotFoundException;
import cl.vetnova.fichaclinica.model.FichaClinica;
import cl.vetnova.fichaclinica.model.Mascota;
import cl.vetnova.fichaclinica.repository.FichaClinicaRepository;
import cl.vetnova.fichaclinica.repository.MascotaRepository;

@Service
public class MascotaService {

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private FichaClinicaRepository fichaClinicaRepository;

    @Autowired
    private AuthClient authClient;

    public List<Mascota> listar() {
        return mascotaRepository.findAll();
    }

    public List<MascotaResponse> listarConCliente() {
        return mascotaRepository.findAll().stream()
                .map(m -> new MascotaResponse(m, authClient.obtenerNombreCliente(m.getClienteId())))
                .toList();
    }

    public Mascota obtenerPorId(Long id) {
        return mascotaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrado con id " + id));
    }

    public MascotaResponse obtenerPorIdConCliente(Long id) {
        Mascota m = obtenerPorId(id);
        return new MascotaResponse(m, authClient.obtenerNombreCliente(m.getClienteId()));
    }

    // CA-MAS-01..14: registra una mascota y crea su FichaClinica automáticamente.
    public Mascota crear(Mascota mascota) {
        if (mascota.getClienteId() == null) {
            throw new BusinessRuleException("El clienteId es obligatorio");
        }
        if (mascota.getNombre() == null) {
            throw new BusinessRuleException("El nombre es obligatorio");
        }
        if (mascota.getNombre().isBlank()) {
            throw new BusinessRuleException("El nombre no puede estar vacío");
        }
        if (mascota.getEspecie() == null) {
            throw new BusinessRuleException("La especie es obligatoria");
        }
        if (mascota.getEspecie().isBlank()) {
            throw new BusinessRuleException("La especie no puede estar vacía");
        }
        if (mascota.getFechaNacimiento() != null && mascota.getFechaNacimiento().isAfter(LocalDate.now())) {
            throw new BusinessRuleException("La fecha de nacimiento no puede ser futura");
        }
        if (mascota.getPeso() != null && mascota.getPeso() <= 0) {
            throw new BusinessRuleException("El peso debe ser mayor a 0");
        }
        if (mascota.getMicrochip() != null && mascotaRepository.existsByMicrochip(mascota.getMicrochip())) {
            throw new ConflictException("Ya existe una mascota con ese microchip");
        }
        mascota.setActivo(true);
        Mascota guardada = mascotaRepository.save(mascota);

        FichaClinica ficha = new FichaClinica();
        ficha.setMascotaId(guardada.getId());
        ficha.setFechaCreacion(Date.valueOf(LocalDate.now()));
        fichaClinicaRepository.save(ficha);

        return guardada;
    }

    // CA-MAS-15/16: actualiza datos de la mascota validando el peso.
    public Mascota actualizar(Long id, Mascota datos) {
        Mascota existente = obtenerPorId(id);
        if (datos.getPeso() != null && datos.getPeso() <= 0) {
            throw new BusinessRuleException("El peso debe ser mayor a 0");
        }
        existente.setNombre(datos.getNombre());
        existente.setEspecie(datos.getEspecie());
        existente.setRaza(datos.getRaza());
        existente.setSexo(datos.getSexo());
        existente.setFechaNacimiento(datos.getFechaNacimiento());
        existente.setPeso(datos.getPeso());
        existente.setMicrochip(datos.getMicrochip());
        return mascotaRepository.save(existente);
    }

    // CA-MAS-17..20: desactivación lógica (soft-delete) idempotente.
    public MascotaDesactivacionResponse desactivar(Long id) {
        Mascota mascota = obtenerPorId(id);
        if (Boolean.FALSE.equals(mascota.getActivo())) {
            return new MascotaDesactivacionResponse(mascota, "La mascota ya estaba inactiva");
        }
        mascota.setActivo(false);
        return new MascotaDesactivacionResponse(mascotaRepository.save(mascota), "Mascota desactivada");
    }
}
