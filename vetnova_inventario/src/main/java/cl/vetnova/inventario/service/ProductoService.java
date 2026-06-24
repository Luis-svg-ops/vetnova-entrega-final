package cl.vetnova.inventario.service;

import cl.vetnova.inventario.dto.ProductoRequest;
import cl.vetnova.inventario.dto.ProductoResponse;
import cl.vetnova.inventario.dto.StockSucursalResponse;
import cl.vetnova.inventario.exception.BusinessRuleException;
import cl.vetnova.inventario.exception.ResourceNotFoundException;
import cl.vetnova.inventario.model.Producto;
import cl.vetnova.inventario.repository.ProductoRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoService {
    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        log.info("event=crear_producto sku={}", request.getSku());
        if (productoRepository.existsBySku(request.getSku())) {
            throw new BusinessRuleException("Ya existe un producto con el SKU " + request.getSku());
        }
        Producto producto = new Producto();
        producto.setSku(request.getSku());
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        Producto guardado = productoRepository.save(producto);
        log.info("event=producto_creado productoId={}", guardado.getId());
        return toResponse(guardado);
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listar() {
        return productoRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtenerPorId(Long id) {
        return toResponse(buscarProducto(id));
    }

    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        log.info("event=actualizar_producto productoId={}", id);
        Producto producto = buscarProducto(id);
        productoRepository.findBySku(request.getSku()).ifPresent(otro -> {
            if (!otro.getId().equals(id)) {
                throw new BusinessRuleException("El SKU " + request.getSku() + " ya está en uso por otro producto");
            }
        });
        producto.setSku(request.getSku());
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        return toResponse(productoRepository.save(producto));
    }

    @Transactional
    public void desactivar(Long id) {
        log.info("event=desactivar_producto productoId={}", id);
        Producto producto = buscarProducto(id);
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    Producto buscarProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id " + id));
    }

    ProductoResponse toResponse(Producto producto) {
        ProductoResponse response = new ProductoResponse();
        response.setId(producto.getId());
        response.setSku(producto.getSku());
        response.setNombre(producto.getNombre());
        response.setDescripcion(producto.getDescripcion());
        response.setPrecio(producto.getPrecio());
        response.setActivo(producto.getActivo());
        response.setFechaCreacion(producto.getFechaCreacion());
        response.setStock(producto.getStockSucursales().stream().map(s -> {
            StockSucursalResponse sr = new StockSucursalResponse();
            sr.setIdSucursal(s.getIdSucursal());
            sr.setCantidad(s.getCantidad());
            sr.setStockMinimo(s.getStockMinimo());
            sr.setCritico(s.getCantidad() <= s.getStockMinimo());
            return sr;
        }).toList());
        return response;
    }
}
