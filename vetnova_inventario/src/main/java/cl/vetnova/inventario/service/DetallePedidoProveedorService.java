package cl.vetnova.inventario.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.inventario.dto.DetallePedidoRequest;
import cl.vetnova.inventario.exception.BusinessRuleException;
import cl.vetnova.inventario.exception.ConflictException;
import cl.vetnova.inventario.exception.ResourceNotFoundException;
import cl.vetnova.inventario.model.DetallePedidoProveedor;
import cl.vetnova.inventario.model.PedidoProveedor;
import cl.vetnova.inventario.repository.DetallePedidoProveedorRepository;
import cl.vetnova.inventario.repository.PedidoProveedorRepository;
import cl.vetnova.inventario.repository.ProductoRepository;
import cl.vetnova.inventario.repository.ProveedorProductoRepository;

@Service
public class DetallePedidoProveedorService {

    private static final String RECIBIDO = "recibido";
    private static final String CANCELADO = "cancelado";

    @Autowired
    private DetallePedidoProveedorRepository detallePedidoProveedorRepository;

    @Autowired
    private PedidoProveedorRepository pedidoProveedorRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ProveedorProductoRepository proveedorProductoRepository;

    public List<DetallePedidoProveedor> listar() {
        return detallePedidoProveedorRepository.findAll();
    }

    public DetallePedidoProveedor obtenerPorId(Long id) {
        return detallePedidoProveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DetallePedidoProveedor no encontrado con id " + id));
    }

    public DetallePedidoProveedor crear(DetallePedidoRequest request) {
        if (request.getPedidoId() == null) {
            throw new BusinessRuleException("El pedidoId es obligatorio");
        }
        PedidoProveedor pedido = buscarPedido(request.getPedidoId());
        if (RECIBIDO.equals(pedido.getEstado()) || CANCELADO.equals(pedido.getEstado())) {
            throw new BusinessRuleException("No se pueden agregar detalles a pedido recibido o cancelado");
        }
        if (request.getProductoId() == null) {
            throw new BusinessRuleException("El productoId es obligatorio");
        }
        if (!productoRepository.existsById(request.getProductoId())) {
            throw new ResourceNotFoundException("Producto no encontrado");
        }
        if (!proveedorProductoRepository.existsByProveedorIdAndProductoId(
                pedido.getProveedorId(), request.getProductoId())) {
            throw new BusinessRuleException("Producto no suministrado por el proveedor");
        }
        if (detallePedidoProveedorRepository.existsByPedidoIdAndProductoId(
                request.getPedidoId(), request.getProductoId())) {
            throw new ConflictException("Producto ya en el pedido. Modifique la cantidad");
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
        if (request.getPrecioUnitario() <= 0) {
            throw new BusinessRuleException("El precio unitario debe ser mayor a 0");
        }

        DetallePedidoProveedor detalle = new DetallePedidoProveedor();
        detalle.setPedidoId(request.getPedidoId());
        detalle.setProductoId(request.getProductoId());
        detalle.setCantidad(request.getCantidad());
        detalle.setPrecioUnitario(request.getPrecioUnitario());
        detalle.setSubtotal(request.getCantidad() * request.getPrecioUnitario());
        return detallePedidoProveedorRepository.save(detalle);
    }

    public DetallePedidoProveedor actualizar(Long id, DetallePedidoRequest request) {
        DetallePedidoProveedor detalle = obtenerPorId(id);
        PedidoProveedor pedido = buscarPedido(detalle.getPedidoId());
        if (CANCELADO.equals(pedido.getEstado())) {
            throw new BusinessRuleException("No se puede modificar detalle de pedido cancelado");
        }
        if (request.getCantidad() != null) {
            detalle.setCantidad(request.getCantidad());
        }
        if (request.getPrecioUnitario() != null) {
            detalle.setPrecioUnitario(request.getPrecioUnitario());
        }
        detalle.setSubtotal(detalle.getCantidad() * detalle.getPrecioUnitario());
        return detallePedidoProveedorRepository.save(detalle);
    }

    public void eliminar(Long id) {
        DetallePedidoProveedor detalle = obtenerPorId(id);
        PedidoProveedor pedido = buscarPedido(detalle.getPedidoId());
        if (RECIBIDO.equals(pedido.getEstado())) {
            throw new BusinessRuleException("No se puede eliminar detalle de pedido recibido");
        }
        if (detallePedidoProveedorRepository.countByPedidoId(detalle.getPedidoId()) <= 1) {
            throw new BusinessRuleException("Mínimo un detalle");
        }
        detallePedidoProveedorRepository.deleteById(id);
    }

    private PedidoProveedor buscarPedido(Long pedidoId) {
        return pedidoProveedorRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
    }
}
