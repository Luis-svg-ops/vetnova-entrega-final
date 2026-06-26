package cl.vetnova.laboratorio.service;

import cl.vetnova.laboratorio.dto.ActualizarEstadoMuestraRequest;
import cl.vetnova.laboratorio.dto.RecepcionMuestraRequest;
import cl.vetnova.laboratorio.dto.RegistrarMuestraRequest;
import cl.vetnova.laboratorio.exception.BusinessRuleException;
import cl.vetnova.laboratorio.exception.ConflictException;
import cl.vetnova.laboratorio.exception.ResourceNotFoundException;
import cl.vetnova.laboratorio.model.Muestra;
import cl.vetnova.laboratorio.model.OrdenExamen;
import cl.vetnova.laboratorio.repository.MuestraRepository;
import cl.vetnova.laboratorio.repository.OrdenExamenRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MuestraService {

    private static final Set<String> ESTADOS = Set.of("RECIBIDA", "EN_PROCESO", "PROCESADA", "DESCARTADA");
    private static final Map<String, Set<String>> TRANSICIONES = Map.of(
            "RECIBIDA", Set.of("EN_PROCESO", "DESCARTADA"),
            "EN_PROCESO", Set.of("PROCESADA", "DESCARTADA"),
            "PROCESADA", Set.of(),
            "DESCARTADA", Set.of());

    private final MuestraRepository muestraRepository;
    private final OrdenExamenRepository ordenRepository;

    public MuestraService(MuestraRepository muestraRepository, OrdenExamenRepository ordenRepository) {
        this.muestraRepository = muestraRepository;
        this.ordenRepository = ordenRepository;
    }

    @Transactional(readOnly = true)
    public Muestra buscar(Long id) {
        return muestraRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Muestra no encontrada"));
    }

    @Transactional
    public Muestra crear(RegistrarMuestraRequest request) {
        if (request.getOrdenExamenId() == null) {
            throw new BusinessRuleException("El ordenExamenId es obligatorio");
        }
        OrdenExamen orden = ordenRepository.findById(request.getOrdenExamenId())
                .orElseThrow(() -> new ResourceNotFoundException("Orden de examen no encontrada"));
        if ("CANCELADA".equals(orden.getEstado())) {
            throw new BusinessRuleException("No se puede registrar una muestra para una orden cancelada");
        }
        if (Boolean.FALSE.equals(orden.getTipoExamen().getRequiereMuestra())) {
            throw new BusinessRuleException("Este tipo de examen no requiere muestra");
        }
        if (muestraRepository.existsByOrdenExamenId(orden.getId())) {
            throw new ConflictException("La orden ya tiene una muestra registrada");
        }
        if (request.getTipo() == null) {
            throw new BusinessRuleException("El tipo de muestra es obligatorio");
        }
        if (request.getTipo().trim().isEmpty()) {
            throw new BusinessRuleException("El tipo de muestra no puede estar vacío");
        }
        if (request.getCodigoMuestra() == null) {
            throw new BusinessRuleException("El código de muestra es obligatorio");
        }
        if (muestraRepository.existsByCodigoMuestra(request.getCodigoMuestra())) {
            throw new ConflictException("Ya existe una muestra con ese código");
        }
        Muestra muestra = new Muestra();
        muestra.setOrdenExamenId(orden.getId());
        muestra.setTipo(request.getTipo());
        muestra.setCodigoMuestra(request.getCodigoMuestra());
        muestra.setDescripcion(request.getDescripcion());
        muestra.setEstadoProcesamiento("RECIBIDA");
        return muestraRepository.save(muestra);
    }

    @Transactional
    public Muestra registrarRecepcion(Long id, RecepcionMuestraRequest request) {
        Muestra muestra = buscar(id);
        if (request.getResponsableId() == null) {
            throw new BusinessRuleException("El responsable de recepción es obligatorio");
        }
        if (muestra.getFechaRecepcion() != null) {
            throw new BusinessRuleException("La muestra ya fue recibida");
        }
        muestra.setFechaRecepcion(LocalDateTime.now(ZoneOffset.UTC));
        muestra.setResponsableRecepcion(request.getResponsableId());
        muestra.setEstadoProcesamiento("RECIBIDA");
        ordenRepository.findById(muestra.getOrdenExamenId()).ifPresent(orden -> {
            orden.setEstado("EN_PROCESO");
            ordenRepository.save(orden);
        });
        return muestraRepository.save(muestra);
    }

    @Transactional
    public Muestra actualizarEstado(Long id, ActualizarEstadoMuestraRequest request) {
        Muestra muestra = buscar(id);
        if (request.getEstado() == null || !ESTADOS.contains(request.getEstado())) {
            throw new BusinessRuleException(
                    "Estado no válido. Valores permitidos: RECIBIDA, EN_PROCESO, PROCESADA, DESCARTADA");
        }
        if (!TRANSICIONES.get(muestra.getEstadoProcesamiento()).contains(request.getEstado())) {
            throw new BusinessRuleException(
                    "Transición de estado no permitida: " + muestra.getEstadoProcesamiento() + " → " + request.getEstado());
        }
        muestra.setEstadoProcesamiento(request.getEstado());
        return muestraRepository.save(muestra);
    }
}
