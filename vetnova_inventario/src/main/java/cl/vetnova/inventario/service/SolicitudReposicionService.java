package cl.vetnova.inventario.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.inventario.dto.ResolucionRequest;
import cl.vetnova.inventario.dto.SolicitudReposicionRequest;
import cl.vetnova.inventario.exception.BusinessRuleException;
import cl.vetnova.inventario.exception.ConflictException;
import cl.vetnova.inventario.exception.ResourceNotFoundException;
import cl.vetnova.inventario.model.SolicitudReposicion;
import cl.vetnova.inventario.repository.InventarioRepository;
import cl.vetnova.inventario.repository.SolicitudReposicionRepository;

@Service
public class SolicitudReposicionService {

    private static final String PENDIENTE = "pendiente";
    private static final String APROBADA = "aprobada";
    private static final String RECHAZADA = "rechazada";

    @Autowired
    private SolicitudReposicionRepository solicitudReposicionRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    public List<SolicitudReposicion> listar() {
        return solicitudReposicionRepository.findAll();
    }

    public SolicitudReposicion obtenerPorId(Long id) {
        return solicitudReposicionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SolicitudReposicion no encontrado con id " + id));
    }

    public SolicitudReposicion crear(SolicitudReposicionRequest request) {
        if (request.getInventarioId() == null) {
            throw new BusinessRuleException("El inventarioId es obligatorio");
        }
        if (!inventarioRepository.existsById(request.getInventarioId())) {
            throw new ResourceNotFoundException("Inventario no encontrado");
        }
        if (request.getCantidadSolicitada() == null) {
            throw new BusinessRuleException("La cantidad solicitada es obligatoria");
        }
        if (request.getCantidadSolicitada() <= 0) {
            throw new BusinessRuleException("La cantidad debe ser mayor a 0");
        }
        if (request.getMotivo() == null) {
            throw new BusinessRuleException("El motivo es obligatorio");
        }
        if (request.getMotivo().isBlank()) {
            throw new BusinessRuleException("El motivo no puede estar vacío");
        }
        if (request.getSolicitadoPor() == null) {
            throw new BusinessRuleException("El solicitante es obligatorio");
        }
        if (solicitudReposicionRepository.existsByInventarioIdAndEstado(request.getInventarioId(), PENDIENTE)) {
            throw new ConflictException("Ya existe solicitud pendiente para este inventario");
        }

        SolicitudReposicion solicitud = new SolicitudReposicion();
        solicitud.setInventarioId(request.getInventarioId());
        solicitud.setCantidadSolicitada(request.getCantidadSolicitada());
        solicitud.setMotivo(request.getMotivo());
        solicitud.setSolicitadoPor(request.getSolicitadoPor());
        solicitud.setEstado(PENDIENTE);
        solicitud.setAprobadoPor(null);
        solicitud.setFechaSolicitud(LocalDateTime.now(ZoneOffset.UTC));
        return solicitudReposicionRepository.save(solicitud);
    }

    public SolicitudReposicion aprobar(Long id, ResolucionRequest request) {
        SolicitudReposicion solicitud = obtenerPorId(id);
        if (!PENDIENTE.equals(solicitud.getEstado())) {
            throw new BusinessRuleException("La solicitud ya fue resuelta");
        }
        if (request.getAprobadoPor() == null) {
            throw new BusinessRuleException("El aprobador es obligatorio");
        }
        solicitud.setEstado(APROBADA);
        solicitud.setAprobadoPor(request.getAprobadoPor());
        solicitud.setFechaResolucion(LocalDateTime.now(ZoneOffset.UTC));
        return solicitudReposicionRepository.save(solicitud);
    }

    public SolicitudReposicion rechazar(Long id, ResolucionRequest request) {
        SolicitudReposicion solicitud = obtenerPorId(id);
        if (!PENDIENTE.equals(solicitud.getEstado())) {
            throw new BusinessRuleException("La solicitud ya fue resuelta");
        }
        solicitud.setEstado(RECHAZADA);
        solicitud.setAprobadoPor(request.getAprobadoPor());
        solicitud.setMotivoRechazo(request.getMotivo());
        solicitud.setFechaResolucion(LocalDateTime.now(ZoneOffset.UTC));
        return solicitudReposicionRepository.save(solicitud);
    }

    public SolicitudReposicion actualizar(Long id, SolicitudReposicion datos) {
        SolicitudReposicion existente = obtenerPorId(id);
        if (APROBADA.equals(existente.getEstado()) || RECHAZADA.equals(existente.getEstado())) {
            throw new BusinessRuleException("No se puede modificar solicitud resuelta");
        }
        existente.setInventarioId(datos.getInventarioId());
        existente.setCantidadSolicitada(datos.getCantidadSolicitada());
        existente.setMotivo(datos.getMotivo());
        existente.setSolicitadoPor(datos.getSolicitadoPor());
        return solicitudReposicionRepository.save(existente);
    }
}
