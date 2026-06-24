package cl.vetnova.catalogo.service;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.catalogo.dto.ProductoRequest;
import cl.vetnova.catalogo.dto.ProductoResponse;
import cl.vetnova.catalogo.exception.BusinessRuleException;
import cl.vetnova.catalogo.exception.ConflictException;
import cl.vetnova.catalogo.exception.ResourceNotFoundException;
import cl.vetnova.catalogo.model.Producto;
import cl.vetnova.catalogo.repository.CategoriaRepository;
import cl.vetnova.catalogo.repository.ProductoRepository;

@Service
public class ProductoService {
    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);
    private static final String URL_REGEX = "^https?://[^\\s]+$";

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    public ProductoResponse crear(ProductoRequest request){
        if (request.getNombre() == null) {
            throw new BusinessRuleException("El nombre es obligatorio");
        }
        if (request.getNombre().isBlank()) {
            throw new BusinessRuleException("El nombre no puede estar vacío");
        }
        if (productoRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ConflictException("Ya existe un producto con ese nombre");
        }
        validarPrecio(request.getPrecio());
        if (request.getCategoriaId() == null) {
            throw new BusinessRuleException("La categoría es obligatoria");
        }
        if (!categoriaRepository.existsById(request.getCategoriaId())) {
            throw new ResourceNotFoundException("Categoría no encontrada");
        }
        if (request.getImagenUrl() != null && !request.getImagenUrl().matches(URL_REGEX)) {
            throw new BusinessRuleException("El formato de la URL de imagen no es válido");
        }
        log.info("event=crear_producto_catalogo nombre={}", request.getNombre());
        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setCategoriaId(request.getCategoriaId());
        producto.setImagenUrl(request.getImagenUrl());
        producto.setActivo(true);
        producto.setFechaActualizacion(LocalDate.now());
        return toResponse(productoRepository.save(producto));
    }

    public List<ProductoResponse> listar(){
        return productoRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ProductoResponse obtenerPorId(Long id){
        return toResponse(buscar(id));
    }

    public ProductoResponse activar(Long id){
        log.info("event=activar_producto productoId={}", id);
        Producto producto = buscar(id);
        producto.setActivo(true);
        producto.setFechaActualizacion(LocalDate.now());
        return toResponse(productoRepository.save(producto));
    }

    public ProductoResponse desactivar(Long id){
        log.info("event=desactivar_producto productoId={}", id);
        Producto producto = buscar(id);
        producto.setActivo(false);
        producto.setFechaActualizacion(LocalDate.now());
        return toResponse(productoRepository.save(producto));
    }

    public ProductoResponse actualizarPrecio(Long id, Double nuevoPrecio){
        log.info("event=actualizar_precio_producto productoId={} precio={}", id, nuevoPrecio);
        Producto producto = buscar(id);
        validarPrecio(nuevoPrecio);
        producto.setPrecio(nuevoPrecio);
        producto.setFechaActualizacion(LocalDate.now());
        return toResponse(productoRepository.save(producto));
    }

    public void eliminar(Long id){
        log.info("event=eliminar_producto productoId={}", id);
        buscar(id);
        productoRepository.deleteById(id);
    }

    private void validarPrecio(Double precio){
        if (precio == null) {
            throw new BusinessRuleException("El precio es obligatorio");
        }
        if (precio <= 0) {
            throw new BusinessRuleException("El precio debe ser mayor a 0");
        }
    }

    private Producto buscar(Long id){
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id " + id));
    }

    private ProductoResponse toResponse(Producto producto){
        ProductoResponse response = new ProductoResponse();
        response.setId(producto.getId());
        response.setNombre(producto.getNombre());
        response.setDescripcion(producto.getDescripcion());
        response.setPrecio(producto.getPrecio());
        response.setActivo(producto.getActivo());
        response.setCategoriaId(producto.getCategoriaId());
        response.setImagenUrl(producto.getImagenUrl());
        response.setFechaActualizacion(producto.getFechaActualizacion());
        return response;
    }
}
