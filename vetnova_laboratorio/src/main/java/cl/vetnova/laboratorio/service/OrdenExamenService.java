package cl.vetnova.laboratorio.service;

import cl.vetnova.laboratorio.client.AuthClient;
import cl.vetnova.laboratorio.dto.CancelarOrdenRequest;
import cl.vetnova.laboratorio.dto.CrearOrdenExamenRequest;
import cl.vetnova.laboratorio.dto.ProgramarOrdenRequest;
import cl.vetnova.laboratorio.exception.BusinessRuleException;
import cl.vetnova.laboratorio.exception.ResourceNotFoundException;
import cl.vetnova.laboratorio.model.OrdenExamen;
import cl.vetnova.laboratorio.model.TipoExamen;
import cl.vetnova.laboratorio.repository.OrdenExamenRepository;
import cl.vetnova.laboratorio.repository.TipoExamenRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrdenExamenService {

    private final OrdenExamenRepository ordenRepository;
    private final TipoExamenRepository tipoExamenRepository;
    private final AuthClient authClient;

    public OrdenExamenService(OrdenExamenRepository ordenRepository, TipoExamenRepository tipoExamenRepository,
                              AuthClient authClient) {
        this.ordenRepository = ordenRepository;
        this.tipoExamenRepository = tipoExamenRepository;
        this.authClient = authClient;
    }

    @Transactional(readOnly = true)
    public List<OrdenExamen> listar(Long mascotaId) {
        return mascotaId == null ? ordenRepository.findAll()
                : ordenRepository.findByMascotaIdOrderByFechaSolicitudDesc(mascotaId);
    }

    @Transactional(readOnly = true)
    public OrdenExamen buscar(Long id) {
        return ordenRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Orden de examen no encontrada"));
    }

    @Transactional
    public OrdenExamen crear(CrearOrdenExamenRequest request) {
        if (request.getMascotaId() == null) {
            throw new BusinessRuleException("El mascotaId es obligatorio");
        }
        // La existencia y el estado activo de la mascota viven en MS Ficha Clínica → verificación diferida.
        if (request.getVeterinarioId() == null) {
            throw new BusinessRuleException("El veterinarioId es obligatorio");
        }
        String rol = authClient.obtenerRol(request.getVeterinarioId());
        if (!"VETERINARIO".equals(rol)) {
            throw new BusinessRuleException("El usuario indicado no tiene rol de veterinario");
        }
        if (request.getTipoExamenId() == null) {
            throw new BusinessRuleException("El tipo de examen es obligatorio");
        }
        TipoExamen tipoExamen = tipoExamenRepository.findById(request.getTipoExamenId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de examen no encontrado"));
        LocalDateTime fechaSolicitud = LocalDateTime.now(ZoneOffset.UTC);
        if (request.getFechaProgramada() != null && request.getFechaProgramada().isBefore(fechaSolicitud)) {
            throw new BusinessRuleException("La fecha programada no puede ser anterior a la fecha de solicitud");
        }
        OrdenExamen orden = new OrdenExamen();
        orden.setMascotaId(request.getMascotaId());
        orden.setVeterinarioId(request.getVeterinarioId());
        orden.setTipoExamen(tipoExamen);
        orden.setDescripcion(request.getDescripcion());
        orden.setFechaProgramada(request.getFechaProgramada());
        orden.setEstado("SOLICITADA");
        orden.setFechaSolicitud(fechaSolicitud);
        return ordenRepository.save(orden);
    }

    @Transactional
    public OrdenExamen programar(Long id, ProgramarOrdenRequest request) {
        OrdenExamen orden = buscar(id);
        if (request.getFechaProgramada() == null || request.getFechaProgramada().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("La fecha programada debe ser futura");
        }
        if (!"SOLICITADA".equals(orden.getEstado())) {
            throw new BusinessRuleException("Solo se puede programar una orden en estado SOLICITADA");
        }
        orden.setFechaProgramada(request.getFechaProgramada());
        orden.setEstado("PROGRAMADA");
        return ordenRepository.save(orden);
    }

    @Transactional
    public OrdenExamen cancelar(Long id, CancelarOrdenRequest request) {
        OrdenExamen orden = buscar(id);
        if ("LISTA".equals(orden.getEstado())) {
            throw new BusinessRuleException("No se puede cancelar una orden con resultado listo");
        }
        if (request.getMotivo() == null || request.getMotivo().trim().isEmpty()) {
            throw new BusinessRuleException("El motivo de cancelación es obligatorio");
        }
        orden.setEstado("CANCELADA");
        orden.setMotivoCancelacion(request.getMotivo());
        return ordenRepository.save(orden);
    }
}
