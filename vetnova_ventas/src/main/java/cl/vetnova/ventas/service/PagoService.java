package cl.vetnova.ventas.service;

import cl.vetnova.ventas.client.InventarioClient;
import cl.vetnova.ventas.dto.CrearPagoRequest;
import cl.vetnova.ventas.dto.OrdenResponse;
import cl.vetnova.ventas.dto.PagoResponse;
import cl.vetnova.ventas.dto.RegistrarPagoRequest;
import cl.vetnova.ventas.exception.BusinessRuleException;
import cl.vetnova.ventas.exception.ConflictException;
import cl.vetnova.ventas.exception.ResourceNotFoundException;
import cl.vetnova.ventas.model.EstadoOrden;
import cl.vetnova.ventas.model.Orden;
import cl.vetnova.ventas.model.Pago;
import cl.vetnova.ventas.repository.OrdenRepository;
import cl.vetnova.ventas.repository.PagoRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PagoService {
    private static final Logger log = LoggerFactory.getLogger(PagoService.class);
    private static final Set<String> METODOS = Set.of("TARJETA", "EFECTIVO", "TRANSFERENCIA", "DEBITO");
    private static final String PENDIENTE = "PENDIENTE";
    private static final String APROBADO = "APROBADO";
    private static final String RECHAZADO = "RECHAZADO";
    private static final String REEMBOLSADO = "REEMBOLSADO";

    private final OrdenRepository ordenRepository;
    private final OrdenService ordenService;
    private final InventarioClient inventarioClient;
    private final PagoRepository pagoRepository;
    private final PasarelaPago pasarelaPago;

    public PagoService(OrdenRepository ordenRepository, OrdenService ordenService, InventarioClient inventarioClient,
                       PagoRepository pagoRepository, PasarelaPago pasarelaPago) {
        this.ordenRepository = ordenRepository;
        this.ordenService = ordenService;
        this.inventarioClient = inventarioClient;
        this.pagoRepository = pagoRepository;
        this.pasarelaPago = pasarelaPago;
    }

    // Regla de negocio: el pago aprobado confirma la orden y descuenta el stock en Inventario
    @Transactional
    public OrdenResponse registrarPago(Long ordenId, RegistrarPagoRequest request) {
        log.info("event=registrar_pago ordenId={} metodo={} monto={}", ordenId, request.getMetodo(), request.getMonto());

        Orden orden = ordenService.buscarOrden(ordenId);

        if (orden.getEstado() != EstadoOrden.PENDIENTE) {
            throw new BusinessRuleException("Solo se pueden pagar órdenes en estado PENDIENTE. Estado actual: " + orden.getEstado());
        }
        if (!request.getMonto().equals(orden.getTotal())) {
            throw new BusinessRuleException("El monto del pago (" + request.getMonto()
                    + ") no coincide con el total de la orden (" + orden.getTotal() + ")");
        }

        Pago pago = new Pago();
        pago.setMetodo(request.getMetodo());
        pago.setMonto(request.getMonto());
        pago.setReferencia(request.getReferencia());
        pago.setEstado("APROBADO");
        orden.addPago(pago);

        orden.setEstado(EstadoOrden.CONFIRMADA);
        orden.setFechaConfirmacion(LocalDateTime.now());

        // Descuento de stock en Inventario por cada detalle de la orden
        orden.getDetalles().forEach(detalle ->
                inventarioClient.registrarSalida(detalle.getProductoId(), orden.getSucursal(),
                        detalle.getCantidad(), "Venta orden " + orden.getId()));

        Orden guardada = ordenRepository.save(orden);
        log.info("event=pago_registrado ordenId={} estado={}", guardada.getId(), guardada.getEstado());
        return ordenService.toResponse(guardada);
    }

    // CA-PAG-01..11: crea un pago en estado PENDIENTE para una orden.
    @Transactional
    public PagoResponse crearPago(CrearPagoRequest request) {
        if (request.getOrdenId() == null) {
            throw new BusinessRuleException("El ordenId es obligatorio");
        }
        Orden orden = ordenService.buscarOrden(request.getOrdenId());
        if (pagoRepository.existsByOrdenIdAndEstado(request.getOrdenId(), APROBADO)) {
            throw new ConflictException("La orden ya tiene un pago aprobado");
        }
        if (request.getMetodo() == null) {
            throw new BusinessRuleException("El método de pago es obligatorio");
        }
        if (!METODOS.contains(request.getMetodo())) {
            throw new BusinessRuleException(
                    "Método de pago no válido. Valores permitidos: TARJETA, EFECTIVO, TRANSFERENCIA, DEBITO");
        }
        if (request.getMonto() == null) {
            throw new BusinessRuleException("El monto es obligatorio");
        }
        if (request.getMonto() <= 0) {
            throw new BusinessRuleException("El monto debe ser mayor a 0");
        }
        if (!request.getMonto().equals(orden.getTotal())) {
            throw new BusinessRuleException("El monto debe ser igual al total de la orden (" + orden.getTotal() + ")");
        }
        if (request.getReferencia() != null && pagoRepository.existsByReferencia(request.getReferencia())) {
            throw new ConflictException("Ya existe un pago con esa referencia");
        }
        Pago pago = new Pago();
        pago.setOrden(orden);
        pago.setMetodo(request.getMetodo());
        pago.setMonto(request.getMonto());
        pago.setReferencia(request.getReferencia());
        pago.setEstado(PENDIENTE);
        pago.setFecha(LocalDateTime.now(ZoneOffset.UTC));
        return toResponse(pagoRepository.save(pago));
    }

    // CA-PAG-12/13/14: procesa el pago contra la pasarela.
    @Transactional
    public PagoResponse procesar(Long id) {
        Pago pago = buscarPago(id);
        if (APROBADO.equals(pago.getEstado())) {
            throw new BusinessRuleException("El pago ya fue procesado");
        }
        if (pasarelaPago.autorizar(pago)) {
            pago.setEstado(APROBADO);
            confirmarOrden(pago.getOrden());
        }
        return toResponse(pagoRepository.save(pago));
    }

    // CA-PAG-15/16: confirma un pago ya aprobado y confirma su orden.
    @Transactional
    public PagoResponse confirmar(Long id) {
        Pago pago = buscarPago(id);
        if (!APROBADO.equals(pago.getEstado())) {
            throw new BusinessRuleException("Solo se puede confirmar un pago aprobado por la pasarela");
        }
        confirmarOrden(pago.getOrden());
        return toResponse(pago);
    }

    // CA-PAG-17/18: rechaza un pago pendiente.
    @Transactional
    public PagoResponse rechazar(Long id, String motivo) {
        Pago pago = buscarPago(id);
        if (APROBADO.equals(pago.getEstado())) {
            throw new BusinessRuleException("No se puede rechazar un pago ya aprobado");
        }
        pago.setEstado(RECHAZADO);
        pago.setMotivoRechazo(motivo);
        return toResponse(pagoRepository.save(pago));
    }

    // CA-PAG-19/20/21: reembolsa un pago aprobado salvo que la orden ya fue entregada.
    @Transactional
    public PagoResponse reembolsar(Long id) {
        Pago pago = buscarPago(id);
        if (!APROBADO.equals(pago.getEstado())) {
            throw new BusinessRuleException("Solo se puede reembolsar un pago aprobado");
        }
        Orden orden = pago.getOrden();
        if (orden.getEstado() == EstadoOrden.ENTREGADA) {
            throw new BusinessRuleException("No se puede reembolsar el pago de una orden ya entregada");
        }
        pago.setEstado(REEMBOLSADO);
        pagoRepository.save(pago);
        orden.getDetalles().forEach(detalle ->
                inventarioClient.registrarEntrada(detalle.getProductoId(), orden.getSucursal(),
                        detalle.getCantidad(), "Reembolso orden " + orden.getId()));
        orden.setEstado(EstadoOrden.CANCELADA);
        ordenRepository.save(orden);
        log.info("event=pago_reembolsado ordenId={} stock_repuesto=true", orden.getId());
        return toResponse(pago);
    }

    private void confirmarOrden(Orden orden) {
        if (orden.getEstado() == EstadoOrden.CONFIRMADA) {
            return;
        }
        orden.setEstado(EstadoOrden.CONFIRMADA);
        orden.setFechaConfirmacion(LocalDateTime.now(ZoneOffset.UTC));
        ordenRepository.save(orden);
        orden.getDetalles().forEach(detalle ->
                inventarioClient.registrarSalida(detalle.getProductoId(), orden.getSucursal(),
                        detalle.getCantidad(), "Venta orden " + orden.getId()));
    }

    private Pago buscarPago(Long id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con id " + id));
    }

    private PagoResponse toResponse(Pago pago) {
        PagoResponse response = new PagoResponse();
        response.setId(pago.getId());
        response.setOrdenId(pago.getOrden().getId());
        response.setMetodo(pago.getMetodo());
        response.setMonto(pago.getMonto());
        response.setEstado(pago.getEstado());
        response.setReferencia(pago.getReferencia());
        response.setFecha(pago.getFecha());
        return response;
    }
}
