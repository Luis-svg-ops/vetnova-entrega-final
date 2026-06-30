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

// Valida reglas de creación/actualización/soft-delete; integra con AuthClient para enriquecer respuestas
@Service
public class MascotaService {

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private FichaClinicaRepository fichaClinicaRepository;

    // AuthClient llama a vetnova_auth (puerto 8081) para obtener el nombre del cliente propietario
    @Autowired
    private AuthClient authClient;

    public List<Mascota> listar() {
        return mascotaRepository.findAll();
    }

    // Enriquece cada mascota con el nombre del cliente; si auth cae, nombreCliente queda null (degradación suave)
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

    public Mascota crear(Mascota mascota) {
        // ── Campos obligatorios: validan sin tocar la BD ──────────────────────────────
        if (mascota.getClienteId() == null) {
            throw new BusinessRuleException("El clienteId es obligatorio");
        }
        if (mascota.getNombre() == null) {
            throw new BusinessRuleException("El nombre es obligatorio");
        }
        // isBlank() atrapa cadenas de solo espacios — separado del null para mensajes distintos
        if (mascota.getNombre().isBlank()) {
            throw new BusinessRuleException("El nombre no puede estar vacío");
        }
        if (mascota.getEspecie() == null) {
            throw new BusinessRuleException("La especie es obligatoria");
        }
        if (mascota.getEspecie().isBlank()) {
            throw new BusinessRuleException("La especie no puede estar vacía");
        }

        // ── Campos opcionales: solo se validan si vienen con valor ────────────────────
        // Una mascota sin fecha de nacimiento conocida es válida; pero si viene, no puede ser futura
        if (mascota.getFechaNacimiento() != null && mascota.getFechaNacimiento().isAfter(LocalDate.now())) {
            throw new BusinessRuleException("La fecha de nacimiento no puede ser futura");
        }
        // Peso null = no registrado (válido); peso <= 0 = dato erróneo (inválido)
        if (mascota.getPeso() != null && mascota.getPeso() <= 0) {
            throw new BusinessRuleException("El peso debe ser mayor a 0");
        }

        // ── Unicidad: requiere consulta a BD, se hace al final para no gastar red antes ─
        // El microchip identifica al animal de forma global; duplicarlo sería corrupción de datos
        if (mascota.getMicrochip() != null && mascotaRepository.existsByMicrochip(mascota.getMicrochip())) {
            throw new ConflictException("Ya existe una mascota con ese microchip");
        }

        // Toda mascota nueva comienza activa; el soft-delete la desactivará si es necesario
        mascota.setActivo(true);
        Mascota guardada = mascotaRepository.save(mascota);

        // La ficha clínica se crea aquí mismo — garantiza la relación 1:1 desde el inicio.
        // Si el cliente intenta crear otra ficha vía POST /fichas → FichaClinicaService lanzará 409.
        FichaClinica ficha = new FichaClinica();
        ficha.setMascotaId(guardada.getId());
        ficha.setFechaCreacion(Date.valueOf(LocalDate.now()));
        fichaClinicaRepository.save(ficha);

        return guardada;
    }

    // Se actualiza sobre el objeto existente de BD para no sobrescribir campos no editables
    public Mascota actualizar(Long id, Mascota datos) {
        Mascota existente = obtenerPorId(id);
        // Peso null = no cambiar; peso <= 0 = dato inválido
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

    // Soft delete: activo=false preserva el historial clínico (evoluciones, recetas, vacunas siguen referenciando esta mascota)
    public MascotaDesactivacionResponse desactivar(Long id) {
        Mascota mascota = obtenerPorId(id);
        // Idempotente: llamar dos veces no falla ni genera escritura innecesaria en BD
        if (Boolean.FALSE.equals(mascota.getActivo())) {
            return new MascotaDesactivacionResponse(mascota, "La mascota ya estaba inactiva");
        }
        mascota.setActivo(false);
        return new MascotaDesactivacionResponse(mascotaRepository.save(mascota), "Mascota desactivada");
    }
}
