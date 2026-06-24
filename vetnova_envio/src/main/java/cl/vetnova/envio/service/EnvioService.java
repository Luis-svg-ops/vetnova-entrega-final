package cl.vetnova.envio.service;

import cl.vetnova.envio.client.NotificacionesClient;
import cl.vetnova.envio.client.VentasClient;
import cl.vetnova.envio.dto.ActualizarEstadoRequest;
import cl.vetnova.envio.dto.CrearEnvioRequest;
import cl.vetnova.envio.dto.EnvioResponse;
import cl.vetnova.envio.dto.TrackingResponse;
import cl.vetnova.envio.exception.BusinessRuleException;
import cl.vetnova.envio.exception.ResourceNotFoundException;
import cl.vetnova.envio.model.Envio;
import cl.vetnova.envio.model.EstadoEnvio;
import cl.vetnova.envio.model.HistorialTracking;
import cl.vetnova.envio.model.TipoEnvio;
import cl.vetnova.envio.repository.EnvioRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnvioService {
    private static final Logger log = LoggerFactory.getLogger(EnvioService.class);

    private final EnvioRepository envioRepository;
    private final VentasClient ventasClient;
    private final NotificacionesClient notificacionesClient;

    public EnvioService(EnvioRepository envioRepository,
                        VentasClient ventasClient,
                        NotificacionesClient notificacionesClient) {
        this.envioRepository = envioRepository;
        this.ventasClient = ventasClient;
        this.notificacionesClient = notificacionesClient;
    }

    @Transactional
    public EnvioResponse crearEnvio(CrearEnvioRequest request) {
        log.info("event=crear_envio ordenId={} tipo={}", request.getOrdenId(), request.getTipoEnvio());

        // Regla de negocio: el despacho siempre nace desde una orden real de Ventas
        if (!ventasClient.ordenExiste(request.getOrdenId())) {
            throw new BusinessRuleException("No existe la orden " + request.getOrdenId() + " en el microservicio de Ventas");
        }

        TipoEnvio tipo = TipoEnvio.valueOf(request.getTipoEnvio());
        if (tipo == TipoEnvio.DOMICILIO &&
                (request.getDireccionEntrega() == null || request.getDireccionEntrega().isBlank())) {
            throw new BusinessRuleException("Para envío a DOMICILIO la dirección de entrega es obligatoria");
        }

        Envio envio = new Envio();
        envio.setOrdenId(request.getOrdenId());
        envio.setTipoEnvio(tipo);
        envio.setIdSucursalOrigen(request.getIdSucursalOrigen());
        envio.setDireccionEntrega(request.getDireccionEntrega());
        envio.setNumeroGuia("GD-" + System.currentTimeMillis());

        HistorialTracking inicial = new HistorialTracking();
        inicial.setEstado(EstadoEnvio.PREPARANDO);
        inicial.setObservacion("El envío está siendo preparado");
        envio.addTracking(inicial);

        Envio guardado = envioRepository.save(envio);
        log.info("event=envio_creado envioId={} guia={}", guardado.getId(), guardado.getNumeroGuia());
        return toResponse(guardado);
    }

    @Transactional(readOnly = true)
    public List<EnvioResponse> listar() {
        return envioRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EnvioResponse obtenerPorId(Long id) {
        return toResponse(buscarEnvio(id));
    }

    @Transactional(readOnly = true)
    public List<TrackingResponse> obtenerTracking(Long id) {
        return buscarEnvio(id).getHistorial().stream().map(this::toTracking).toList();
    }

    @Transactional
    public EnvioResponse actualizarEstado(Long id, ActualizarEstadoRequest request) {
        Envio envio = buscarEnvio(id);
        EstadoEnvio nuevoEstado = EstadoEnvio.valueOf(request.getEstado());
        log.info("event=actualizar_estado_envio envioId={} de={} a={}", id, envio.getEstadoActual(), nuevoEstado);

        if (envio.getEstadoActual() == EstadoEnvio.ENTREGADO || envio.getEstadoActual() == EstadoEnvio.CANCELADO) {
            throw new BusinessRuleException("El envío ya está en estado final " + envio.getEstadoActual());
        }
        if (nuevoEstado == EstadoEnvio.ENTREGADO && envio.getEstadoActual() != EstadoEnvio.EN_RUTA) {
            throw new BusinessRuleException("Solo un envío EN_RUTA puede pasar a ENTREGADO");
        }

        envio.setEstadoActual(nuevoEstado);
        HistorialTracking tracking = new HistorialTracking();
        tracking.setEstado(nuevoEstado);
        tracking.setObservacion(request.getObservacion());
        envio.addTracking(tracking);

        Envio guardado = envioRepository.save(envio);
        notificacionesClient.avisarCambioEstado(1L, guardado.getId(), nuevoEstado.name());
        return toResponse(guardado);
    }

    private Envio buscarEnvio(Long id) {
        return envioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Envío no encontrado con id " + id));
    }

    private EnvioResponse toResponse(Envio envio) {
        EnvioResponse response = new EnvioResponse();
        response.setId(envio.getId());
        response.setNumeroGuia(envio.getNumeroGuia());
        response.setOrdenId(envio.getOrdenId());
        response.setTipoEnvio(envio.getTipoEnvio().name());
        response.setIdSucursalOrigen(envio.getIdSucursalOrigen());
        response.setDireccionEntrega(envio.getDireccionEntrega());
        response.setEstadoActual(envio.getEstadoActual().name());
        response.setFechaCreacion(envio.getFechaCreacion());
        response.setHistorial(envio.getHistorial().stream().map(this::toTracking).toList());
        return response;
    }

    private TrackingResponse toTracking(HistorialTracking tracking) {
        TrackingResponse tr = new TrackingResponse();
        tr.setId(tracking.getId());
        tr.setEstado(tracking.getEstado().name());
        tr.setObservacion(tracking.getObservacion());
        tr.setFecha(tracking.getFecha());
        return tr;
    }
}
