package cl.vetnova.laboratorio.service;

import cl.vetnova.laboratorio.client.AuthClient;
import cl.vetnova.laboratorio.dto.RegistrarResultadoRequest;
import cl.vetnova.laboratorio.exception.BusinessRuleException;
import cl.vetnova.laboratorio.exception.ConflictException;
import cl.vetnova.laboratorio.exception.ResourceNotFoundException;
import cl.vetnova.laboratorio.model.Muestra;
import cl.vetnova.laboratorio.model.OrdenExamen;
import cl.vetnova.laboratorio.model.ResultadoExamen;
import cl.vetnova.laboratorio.repository.MuestraRepository;
import cl.vetnova.laboratorio.repository.OrdenExamenRepository;
import cl.vetnova.laboratorio.repository.ResultadoExamenRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResultadoExamenService {

    private final ResultadoExamenRepository resultadoRepository;
    private final OrdenExamenRepository ordenRepository;
    private final MuestraRepository muestraRepository;
    private final AuthClient authClient;

    public ResultadoExamenService(ResultadoExamenRepository resultadoRepository, OrdenExamenRepository ordenRepository,
                                  MuestraRepository muestraRepository, AuthClient authClient) {
        this.resultadoRepository = resultadoRepository;
        this.ordenRepository = ordenRepository;
        this.muestraRepository = muestraRepository;
        this.authClient = authClient;
    }

    @Transactional(readOnly = true)
    public ResultadoExamen buscar(Long id) {
        return resultadoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resultado no encontrado"));
    }

    @Transactional
    public ResultadoExamen registrar(RegistrarResultadoRequest request) {
        if (request.getOrdenExamenId() == null) {
            throw new BusinessRuleException("El ordenExamenId es obligatorio");
        }
        OrdenExamen orden = ordenRepository.findById(request.getOrdenExamenId())
                .orElseThrow(() -> new ResourceNotFoundException("Orden de examen no encontrada"));
        if (!"EN_PROCESO".equals(orden.getEstado())) {
            throw new BusinessRuleException("No se puede registrar resultado de una orden que no está en proceso");
        }
        if (resultadoRepository.existsByOrdenExamenId(orden.getId())) {
            throw new ConflictException("La orden ya tiene un resultado registrado");
        }
        if (request.getMuestraId() != null) {
            Muestra muestra = muestraRepository.findById(request.getMuestraId()).orElse(null);
            if (muestra != null && !"PROCESADA".equals(muestra.getEstadoProcesamiento())) {
                throw new BusinessRuleException("La muestra aún no ha sido procesada");
            }
        }
        if (request.getTecnicoId() == null) {
            throw new BusinessRuleException("El tecnicoId es obligatorio");
        }
        if (!authClient.usuarioExiste(request.getTecnicoId())) {
            throw new ResourceNotFoundException("Técnico no encontrado");
        }
        if (request.getResultado() == null) {
            throw new BusinessRuleException("El resultado es obligatorio");
        }
        if (request.getResultado().trim().isEmpty()) {
            throw new BusinessRuleException("El resultado no puede estar vacío");
        }
        ResultadoExamen resultado = new ResultadoExamen();
        resultado.setOrdenExamenId(orden.getId());
        resultado.setMuestraId(request.getMuestraId());
        resultado.setTecnicoId(request.getTecnicoId());
        resultado.setResultado(request.getResultado());
        resultado.setObservaciones(request.getObservaciones());
        resultado.setDisponible(false);
        resultado.setFechaRegistro(LocalDateTime.now(ZoneOffset.UTC));
        return resultadoRepository.save(resultado);
    }

    @Transactional
    public ResultadoExamen publicar(Long id) {
        ResultadoExamen resultado = buscar(id);
        if (Boolean.TRUE.equals(resultado.getDisponible())) {
            throw new BusinessRuleException("El resultado ya fue publicado");
        }
        resultado.setDisponible(true);
        ordenRepository.findById(resultado.getOrdenExamenId()).ifPresent(orden -> {
            orden.setEstado("LISTA");
            ordenRepository.save(orden);
        });
        // Notificación al cliente vía MS Notificaciones → diferida.
        return resultadoRepository.save(resultado);
    }
}
