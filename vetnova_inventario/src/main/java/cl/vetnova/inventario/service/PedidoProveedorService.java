package cl.vetnova.inventario.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.inventario.dto.DetallePedidoRequest;
import cl.vetnova.inventario.dto.PedidoProveedorRequest;
import cl.vetnova.inventario.exception.BusinessRuleException;
import cl.vetnova.inventario.exception.ResourceNotFoundException;
import cl.vetnova.inventario.model.DetallePedidoProveedor;
import cl.vetnova.inventario.model.Inventario;
import cl.vetnova.inventario.model.PedidoProveedor;
import cl.vetnova.inventario.model.Proveedor;
import cl.vetnova.inventario.model.TipoMovimiento;
import cl.vetnova.inventario.repository.DetallePedidoProveedorRepository;
import cl.vetnova.inventario.repository.InventarioRepository;
import cl.vetnova.inventario.repository.PedidoProveedorRepository;
import cl.vetnova.inventario.repository.ProveedorProductoRepository;
import cl.vetnova.inventario.repository.ProveedorRepository;

@Service
public class PedidoProveedorService {

    private static final Set<String> SUCURSALES = Set.of("CHILLAN", "LOS_ANGELES", "TALCA");
    private static final String PENDIENTE = "pendiente";
    private static final String ENVIADO = "enviado";
    private static final String RECIBIDO = "recibido";
    private static final String CANCELADO = "cancelado";

    @Autowired
    private PedidoProveedorRepository pedidoProveedorRepository;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private ProveedorProductoRepository proveedorProductoRepository;

    @Autowired
    private DetallePedidoProveedorRepository detallePedidoProveedorRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private OperacionStock operacionStock;

    public List<PedidoProveedor> listar() {
        return pedidoProveedorRepository.findAll();
    }

    public PedidoProveedor obtenerPorId(Long id) {
        return pedidoProveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PedidoProveedor no encontrado con id " + id));
    }

    public PedidoProveedor crear(PedidoProveedorRequest request) {
        if (request.getProveedorId() == null) {
            throw new BusinessRuleException("El proveedorId es obligatorio");
        }
        Proveedor proveedor = proveedorRepository.findById(request.getProveedorId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));
        if (Boolean.FALSE.equals(proveedor.getActivo())) {
            throw new BusinessRuleException("Proveedor inactivo");
        }
        if (request.getSucursal() == null) {
            throw new BusinessRuleException("La sucursal es obligatoria");
        }
        if (!SUCURSALES.contains(request.getSucursal())) {
            throw new BusinessRuleException("Sucursal no válida. Opciones: CHILLAN, LOS_ANGELES, TALCA");
        }
        if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
            throw new BusinessRuleException("El pedido debe tener al menos un detalle");
        }
        for (DetallePedidoRequest detalle : request.getDetalles()) {
            if (!proveedorProductoRepository.existsByProveedorIdAndProductoId(
                    request.getProveedorId(), detalle.getProductoId())) {
                throw new BusinessRuleException("Producto no suministrado por este proveedor");
            }
        }

        PedidoProveedor pedido = new PedidoProveedor();
        pedido.setProveedorId(request.getProveedorId());
        pedido.setSucursal(request.getSucursal());
        pedido.setResponsable(request.getResponsable());
        pedido.setEstado(PENDIENTE);
        pedido.setFechaPedido(LocalDateTime.now(ZoneOffset.UTC));
        PedidoProveedor guardado = pedidoProveedorRepository.save(pedido);

        for (DetallePedidoRequest detalle : request.getDetalles()) {
            DetallePedidoProveedor item = new DetallePedidoProveedor();
            item.setPedidoId(guardado.getId());
            item.setProductoId(detalle.getProductoId());
            item.setCantidad(detalle.getCantidad());
            item.setPrecioUnitario(detalle.getPrecioUnitario());
            item.setSubtotal(detalle.getCantidad() * detalle.getPrecioUnitario());
            detallePedidoProveedorRepository.save(item);
        }
        return guardado;
    }

    public PedidoProveedor enviar(Long id) {
        PedidoProveedor pedido = obtenerPorId(id);
        if (ENVIADO.equals(pedido.getEstado())) {
            throw new BusinessRuleException("El pedido ya fue enviado");
        }
        if (RECIBIDO.equals(pedido.getEstado())) {
            throw new BusinessRuleException("No se puede enviar pedido ya recibido");
        }
        pedido.setEstado(ENVIADO);
        return pedidoProveedorRepository.save(pedido);
    }

    public PedidoProveedor recibir(Long id) {
        PedidoProveedor pedido = obtenerPorId(id);
        if (!ENVIADO.equals(pedido.getEstado())) {
            throw new BusinessRuleException("Debe estar enviado antes de recibir");
        }
        for (DetallePedidoProveedor detalle : detallePedidoProveedorRepository.findByPedidoId(id)) {
            Inventario inventario = obtenerOCrearInventario(detalle.getProductoId(), pedido.getSucursal());
            operacionStock.aplicarMovimiento(inventario, TipoMovimiento.ENTRADA, detalle.getCantidad(),
                    "Recepción pedido " + id, pedido.getResponsable());
        }
        pedido.setEstado(RECIBIDO);
        pedido.setFechaRecepcion(LocalDateTime.now(ZoneOffset.UTC));
        return pedidoProveedorRepository.save(pedido);
    }

    public PedidoProveedor cancelar(Long id) {
        PedidoProveedor pedido = obtenerPorId(id);
        if (RECIBIDO.equals(pedido.getEstado())) {
            throw new BusinessRuleException("No se puede cancelar pedido ya recibido");
        }
        pedido.setEstado(CANCELADO);
        return pedidoProveedorRepository.save(pedido);
    }

    public PedidoProveedor actualizar(Long id, PedidoProveedor datos) {
        PedidoProveedor existente = obtenerPorId(id);
        existente.setProveedorId(datos.getProveedorId());
        existente.setSucursal(datos.getSucursal());
        existente.setEstado(datos.getEstado());
        existente.setFechaPedido(datos.getFechaPedido());
        existente.setFechaRecepcion(datos.getFechaRecepcion());
        existente.setResponsable(datos.getResponsable());
        return pedidoProveedorRepository.save(existente);
    }

    public void eliminar(Long id) {
        if (!pedidoProveedorRepository.existsById(id)) {
            throw new ResourceNotFoundException("PedidoProveedor no encontrado con id " + id);
        }
        pedidoProveedorRepository.deleteById(id);
    }

    private Inventario obtenerOCrearInventario(Long productoId, String sucursal) {
        return inventarioRepository.findByProductoIdAndSucursal(productoId, sucursal)
                .orElseGet(() -> {
                    Inventario inventario = new Inventario();
                    inventario.setProductoId(productoId);
                    inventario.setSucursal(sucursal);
                    inventario.setStockDisponible(0);
                    inventario.setStockMinimo(0);
                    inventario.setStockTransito(0);
                    return inventario;
                });
    }
}
