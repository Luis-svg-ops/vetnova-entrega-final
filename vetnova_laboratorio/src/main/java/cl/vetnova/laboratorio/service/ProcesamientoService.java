package cl.vetnova.laboratorio.service;

import cl.vetnova.laboratorio.client.AuthClient;
import cl.vetnova.laboratorio.dto.CompletarProcesamientoRequest;
import cl.vetnova.laboratorio.dto.CrearProcesamientoRequest;
import cl.vetnova.laboratorio.exception.BusinessRuleException;
import cl.vetnova.laboratorio.exception.ConflictException;
import cl.vetnova.laboratorio.exception.ResourceNotFoundException;
import cl.vetnova.laboratorio.model.Muestra;
import cl.vetnova.laboratorio.model.Procesamiento;
import cl.vetnova.laboratorio.repository.MuestraRepository;
import cl.vetnova.laboratorio.repository.ProcesamientoRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProcesamientoService {

    private final ProcesamientoRepository procesamientoRepository;
    private final MuestraRepository muestraRepository;
    private final AuthClient authClient;

    public ProcesamientoService(ProcesamientoRepository procesamientoRepository, MuestraRepository muestraRepository,
                                AuthClient authClient) {
        this.procesamientoRepository = procesamientoRepository;
        this.muestraRepository = muestraRepository;
        this.authClient = authClient;
    }

    @Transactional(readOnly = true)
    public Procesamiento buscar(Long id) {
        return procesamientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Procesamiento no encontrado"));
    }

    @Transactional
    public Procesamiento crear(CrearProcesamientoRequest request) {
        if (request.getMuestraId() == null) {
            throw new BusinessRuleException("El muestraId es obligatorio");
        }
        Muestra muestra = muestraRepository.findById(request.getMuestraId())
                .orElseThrow(() -> new ResourceNotFoundException("Muestra no encontrada"));
        if (muestra.getFechaRecepcion() == null) {
            throw new BusinessRuleException("La muestra no ha sido recibida formalmente");
        }
        if (procesamientoRepository.existsByMuestraId(muestra.getId())) {
            throw new ConflictException("La muestra ya tiene un procesamiento asociado");
        }
        if (request.getTecnicoId() == null) {
            throw new BusinessRuleException("El tecnicoId es obligatorio");
        }
        if (!authClient.usuarioExiste(request.getTecnicoId())) {
            throw new ResourceNotFoundException("Técnico no encontrado");
        }
        if (request.getMetodologia() == null) {
            throw new BusinessRuleException("La metodología es obligatoria");
        }
        if (request.getMetodologia().trim().isEmpty()) {
            throw new BusinessRuleException("La metodología no puede estar vacía");
        }
        Procesamiento procesamiento = new Procesamiento();
        procesamiento.setMuestraId(muestra.getId());
        procesamiento.setTecnicoId(request.getTecnicoId());
        procesamiento.setMetodologia(request.getMetodologia());
        procesamiento.setObservaciones(request.getObservaciones());
        procesamiento.setEstado("EN_PROCESO");
        return procesamientoRepository.save(procesamiento);
    }

    @Transactional
    public Procesamiento iniciar(Long id) {
        Procesamiento procesamiento = buscar(id);
        if (procesamiento.getFechaInicio() != null) {
            throw new BusinessRuleException("El procesamiento ya fue iniciado");
        }
        procesamiento.setEstado("EN_PROCESO");
        procesamiento.setFechaInicio(LocalDateTime.now(ZoneOffset.UTC));
        muestraRepository.findById(procesamiento.getMuestraId()).ifPresent(muestra -> {
            muestra.setEstadoProcesamiento("EN_PROCESO");
            muestraRepository.save(muestra);
        });
        return procesamientoRepository.save(procesamiento);
    }

    @Transactional
    public Procesamiento completar(Long id, CompletarProcesamientoRequest request) {
        Procesamiento procesamiento = buscar(id);
        if (procesamiento.getFechaInicio() == null) {
            throw new BusinessRuleException("No se puede completar un procesamiento que no fue iniciado");
        }
        if (request.getFechaFin() != null && request.getFechaFin().isBefore(procesamiento.getFechaInicio())) {
            throw new BusinessRuleException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }
        procesamiento.setEstado("COMPLETADO");
        procesamiento.setFechaFin(request.getFechaFin() != null ? request.getFechaFin() : LocalDateTime.now(ZoneOffset.UTC));
        if (request.getObservaciones() != null) {
            procesamiento.setObservaciones(request.getObservaciones());
        }
        muestraRepository.findById(procesamiento.getMuestraId()).ifPresent(muestra -> {
            muestra.setEstadoProcesamiento("PROCESADA");
            muestraRepository.save(muestra);
        });
        return procesamientoRepository.save(procesamiento);
    }
}
