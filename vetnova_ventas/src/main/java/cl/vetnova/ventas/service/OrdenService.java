package cl.vetnova.ventas.service;

import cl.vetnova.ventas.client.AuthClient;
import cl.vetnova.ventas.client.InventarioClient;
import cl.vetnova.ventas.dto.*;
import cl.vetnova.ventas.exception.BusinessRuleException;
import cl.vetnova.ventas.exception.ResourceNotFoundException;
import cl.vetnova.ventas.model.DetalleOrden;
import cl.vetnova.ventas.model.EstadoOrden;
import cl.vetnova.ventas.model.Orden;
import cl.vetnova.ventas.repository.OrdenRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrdenService {
    private static final Logger log = LoggerFactory.getLogger(OrdenService.class);

    private final OrdenRepository ordenRepository;
    private final InventarioClient inventarioClient;
    private final AuthClient authClient;
    private final double iva;

    public OrdenService(OrdenRepository ordenRepository,
                        InventarioClient inventarioClient,
                        AuthClient authClient,
                        @Value("${app.iva}") double iva) {
        this.ordenRepository = ordenRepository;
        this.inventarioClient = inventarioClient;
        this.authClient = authClient;
        this.iva = iva;
    }

    @Transactional
    public OrdenResponse crearOrden(CrearOrdenRequest request) {
        log.info("event=crear_orden clienteId={} sucursal={} items={}",
                request.getClienteId(), request.getSucursal(), request.getDetalles().size());

        if (!authClient.clienteExiste(request.getClienteId())) {
            throw new ResourceNotFoundException("Cliente no encontrado con id " + request.getClienteId());
        }

        // Regla de negocio: antes de crear la orden se valida stock contra Inventario
        for (DetalleOrdenRequest detalle : request.getDetalles()) {
            Integer disponible = inventarioClient.consultarStock(detalle.getProductoId(), request.getSucursal());
            if (disponible < detalle.getCantidad()) {
                throw new BusinessRuleException("Stock insuficiente para el producto " + detalle.getProductoId()
                        + ". Disponible: " + disponible + ", solicitado: " + detalle.getCantidad());
            }
        }

        Orden orden = new Orden();
        orden.setClienteId(request.getClienteId());
        orden.setSucursal(request.getSucursal());

        double subtotal = 0.0;
        for (DetalleOrdenRequest d : request.getDetalles()) {
            DetalleOrden detalle = new DetalleOrden();
            detalle.setProductoId(d.getProductoId());
            detalle.setNombreProducto(d.getNombreProducto());
            detalle.setCantidad(d.getCantidad());
            detalle.setPrecioUnitario(d.getPrecioUnitario());
            detalle.setSubtotal(d.getPrecioUnitario() * d.getCantidad());
            subtotal += detalle.getSubtotal();
            orden.addDetalle(detalle);
        }

        orden.setSubtotal(subtotal);
        orden.setImpuestos(Math.round(subtotal * iva * 100.0) / 100.0);
        orden.setTotal(orden.getSubtotal() + orden.getImpuestos());

        Orden guardada = ordenRepository.save(orden);
        log.info("event=orden_creada ordenId={} total={}", guardada.getId(), guardada.getTotal());
        return toResponse(guardada);
    }

    @Transactional(readOnly = true)
    public List<OrdenResponse> listar() {
        return ordenRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public OrdenResponse obtenerPorId(Long id) {
        return toResponse(buscarOrden(id));
    }

    @Transactional(readOnly = true)
    public boolean existe(Long id) {
        return ordenRepository.existsById(id);
    }

    @Transactional
    public OrdenResponse cambiarEstado(Long id, CambiarEstadoRequest request) {
        Orden orden = buscarOrden(id);
        EstadoOrden nuevoEstado = EstadoOrden.valueOf(request.getEstado());
        log.info("event=cambiar_estado_orden ordenId={} de={} a={}", id, orden.getEstado(), nuevoEstado);

        validarTransicion(orden.getEstado(), nuevoEstado);
        orden.setEstado(nuevoEstado);
        return toResponse(ordenRepository.save(orden));
    }

    // CA-ORD-07/15/16/17: confirma una orden PENDIENTE con pago aprobado y stock suficiente,
    // descontando stock en Inventario por cada ítem.
    @Transactional
    public OrdenResponse confirmar(Long id) {
        Orden orden = buscarOrden(id);
        if (orden.getEstado() == EstadoOrden.CANCELADA) {
            throw new BusinessRuleException("No se puede confirmar una orden cancelada");
        }
        if (orden.getEstado() != EstadoOrden.PENDIENTE) {
            throw new BusinessRuleException("Solo una orden PENDIENTE puede pasar a CONFIRMADA");
        }
        if (orden.getDetalles().isEmpty()) {
            throw new BusinessRuleException("La orden no tiene ítems");
        }
        if (!tienePagoAprobado(orden)) {
            throw new BusinessRuleException("La orden no puede confirmarse sin un pago aprobado");
        }
        for (DetalleOrden detalle : orden.getDetalles()) {
            Integer disponible = inventarioClient.consultarStock(detalle.getProductoId(), orden.getSucursal());
            if (disponible < detalle.getCantidad()) {
                throw new BusinessRuleException("Stock insuficiente para confirmar la orden");
            }
        }
        for (DetalleOrden detalle : orden.getDetalles()) {
            inventarioClient.registrarSalida(detalle.getProductoId(), orden.getSucursal(),
                    detalle.getCantidad(), "Confirmación orden " + id);
        }
        orden.setEstado(EstadoOrden.CONFIRMADA);
        orden.setFechaConfirmacion(LocalDateTime.now(ZoneOffset.UTC));
        return toResponse(ordenRepository.save(orden));
    }

    // CA-ORD-08/18/19/20: cancela una orden; si estaba CONFIRMADA repone el stock.
    @Transactional
    public OrdenResponse cancelar(Long id) {
        Orden orden = buscarOrden(id);
        if (orden.getEstado() == EstadoOrden.ENTREGADA) {
            throw new BusinessRuleException("No se puede cancelar una orden ya entregada");
        }
        if (orden.getEstado() == EstadoOrden.ENVIADA) {
            throw new BusinessRuleException("No se puede cancelar una orden que ya fue enviada");
        }
        if (orden.getEstado() == EstadoOrden.CONFIRMADA) {
            for (DetalleOrden detalle : orden.getDetalles()) {
                inventarioClient.registrarEntrada(detalle.getProductoId(), orden.getSucursal(),
                        detalle.getCantidad(), "Cancelación orden " + id);
            }
        }
        orden.setEstado(EstadoOrden.CANCELADA);
        return toResponse(ordenRepository.save(orden));
    }

    private boolean tienePagoAprobado(Orden orden) {
        return orden.getPagos().stream().anyMatch(pago -> "APROBADO".equals(pago.getEstado()));
    }

    private static final java.util.Set<String> TIPOS_ITEM = java.util.Set.of("PRODUCTO", "SERVICIO");

    // CA-DOR-03..17: agrega un ítem a una orden PENDIENTE y recalcula sus totales.
    @Transactional
    public OrdenResponse agregarDetalle(Long ordenId, ItemOrdenRequest request) {
        Orden orden = buscarOrden(ordenId);
        if (orden.getEstado() != EstadoOrden.PENDIENTE) {
            throw new BusinessRuleException("No se pueden agregar ítems a una orden que no está en estado PENDIENTE");
        }
        if (request.getItemId() == null) {
            throw new BusinessRuleException("El itemId es obligatorio");
        }
        if (request.getTipoItem() == null) {
            throw new BusinessRuleException("El tipoItem es obligatorio");
        }
        if (!TIPOS_ITEM.contains(request.getTipoItem())) {
            throw new BusinessRuleException("Tipo de ítem no válido. Valores permitidos: PRODUCTO, SERVICIO");
        }
        if (request.getCantidad() == null) {
            throw new BusinessRuleException("La cantidad es obligatoria");
        }
        if (request.getCantidad() <= 0) {
            throw new BusinessRuleException("La cantidad debe ser mayor a 0");
        }
        if (request.getPrecioUnitario() == null) {
            throw new BusinessRuleException("El precio unitario es obligatorio");
        }
        if (request.getPrecioUnitario() < 0) {
            throw new BusinessRuleException("El precio unitario no puede ser negativo");
        }
        double subtotalCalculado = request.getPrecioUnitario() * request.getCantidad();
        if (request.getSubtotal() != null && request.getSubtotal() != subtotalCalculado) {
            throw new BusinessRuleException("El subtotal debe ser igual a cantidad × precioUnitario");
        }
        DetalleOrden detalle = new DetalleOrden();
        detalle.setProductoId(request.getItemId());
        detalle.setTipoItem(request.getTipoItem());
        detalle.setNombreProducto(request.getNombreProducto());
        detalle.setCantidad(request.getCantidad());
        detalle.setPrecioUnitario(request.getPrecioUnitario());
        detalle.setSubtotal(subtotalCalculado);
        orden.addDetalle(detalle);
        recalcular(orden);
        return toResponse(ordenRepository.save(orden));
    }

    // CA-DOR-15: actualiza la cantidad de un detalle y recalcula subtotal y totales.
    @Transactional
    public OrdenResponse actualizarDetalle(Long ordenId, Long detalleId, ActualizarCantidadRequest request) {
        Orden orden = buscarOrden(ordenId);
        DetalleOrden detalle = buscarDetalle(orden, detalleId);
        if (request.getCantidad() == null) {
            throw new BusinessRuleException("La cantidad es obligatoria");
        }
        if (request.getCantidad() <= 0) {
            throw new BusinessRuleException("La cantidad debe ser mayor a 0");
        }
        detalle.setCantidad(request.getCantidad());
        detalle.setSubtotal(detalle.getPrecioUnitario() * request.getCantidad());
        recalcular(orden);
        return toResponse(ordenRepository.save(orden));
    }

    // CA-DOR-18: elimina un detalle salvo que sea el único de la orden.
    @Transactional
    public OrdenResponse eliminarDetalle(Long ordenId, Long detalleId) {
        Orden orden = buscarOrden(ordenId);
        if (orden.getDetalles().size() <= 1) {
            throw new BusinessRuleException("La orden debe tener al menos un ítem");
        }
        DetalleOrden detalle = buscarDetalle(orden, detalleId);
        orden.getDetalles().remove(detalle);
        recalcular(orden);
        return toResponse(ordenRepository.save(orden));
    }

    private DetalleOrden buscarDetalle(Orden orden, Long detalleId) {
        return orden.getDetalles().stream()
                .filter(detalle -> detalleId.equals(detalle.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Detalle no encontrado"));
    }

    private void recalcular(Orden orden) {
        double subtotal = orden.getDetalles().stream().mapToDouble(DetalleOrden::getSubtotal).sum();
        orden.setSubtotal(subtotal);
        orden.setImpuestos(Math.round(subtotal * iva * 100.0) / 100.0);
        orden.setTotal(orden.getSubtotal() + orden.getImpuestos());
    }

    // Reglas de transición de estado de la orden
    private void validarTransicion(EstadoOrden actual, EstadoOrden nuevo) {
        if (actual == EstadoOrden.ENTREGADA || actual == EstadoOrden.CANCELADA) {
            throw new BusinessRuleException("La orden ya está en estado final " + actual + " y no se puede modificar");
        }
        if (nuevo == EstadoOrden.ENVIADA && actual != EstadoOrden.CONFIRMADA) {
            throw new BusinessRuleException("Solo una orden CONFIRMADA puede pasar a ENVIADA");
        }
        if (nuevo == EstadoOrden.ENTREGADA && actual != EstadoOrden.ENVIADA) {
            throw new BusinessRuleException("Solo una orden ENVIADA puede pasar a ENTREGADA");
        }
        if (nuevo == EstadoOrden.CONFIRMADA && actual != EstadoOrden.PENDIENTE) {
            throw new BusinessRuleException("Solo una orden PENDIENTE puede pasar a CONFIRMADA");
        }
    }

    Orden buscarOrden(Long id) {
        return ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con id " + id));
    }

    OrdenResponse toResponse(Orden orden) {
        OrdenResponse response = new OrdenResponse();
        response.setId(orden.getId());
        response.setClienteId(orden.getClienteId());
        response.setSucursal(orden.getSucursal());
        response.setEstado(orden.getEstado().name());
        response.setSubtotal(orden.getSubtotal());
        response.setImpuestos(orden.getImpuestos());
        response.setTotal(orden.getTotal());
        response.setFechaCreacion(orden.getFechaCreacion());
        response.setFechaConfirmacion(orden.getFechaConfirmacion());
        response.setDetalles(orden.getDetalles().stream().map(d -> {
            DetalleOrdenResponse dr = new DetalleOrdenResponse();
            dr.setId(d.getId());
            dr.setProductoId(d.getProductoId());
            dr.setNombreProducto(d.getNombreProducto());
            dr.setCantidad(d.getCantidad());
            dr.setPrecioUnitario(d.getPrecioUnitario());
            dr.setSubtotal(d.getSubtotal());
            return dr;
        }).toList());
        response.setPagos(orden.getPagos().stream().map(p -> {
            PagoResponse pr = new PagoResponse();
            pr.setId(p.getId());
            pr.setOrdenId(orden.getId());
            pr.setMetodo(p.getMetodo());
            pr.setMonto(p.getMonto());
            pr.setEstado(p.getEstado());
            pr.setReferencia(p.getReferencia());
            pr.setFecha(p.getFecha());
            return pr;
        }).toList());
        return response;
    }
}
