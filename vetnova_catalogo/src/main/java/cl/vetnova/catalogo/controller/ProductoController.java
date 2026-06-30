package cl.vetnova.catalogo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.vetnova.catalogo.dto.ProductoRequest;
import cl.vetnova.catalogo.dto.ProductoResponse;
import cl.vetnova.catalogo.service.ProductoService;

/**
 * Controlador REST que expone los endpoints CRUD y de estado para productos del catálogo.
 * Gestiona creación, consulta, activación/desactivación, cambio de precio y eliminación de productos.
 */
@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    /**
     * Crea un nuevo producto en el catálogo a partir del body JSON recibido.
     * Retorna el producto creado (con id y fecha de actualización) con estado HTTP 201.
     */
    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@RequestBody ProductoRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(request));
    }

    /**
     * Retorna todos los productos registrados, incluyendo los inactivos.
     * Para obtener solo productos activos, usar el endpoint /catalogo/disponibles.
     */
    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listar(){
        return ResponseEntity.ok(productoService.listar());
    }

    /**
     * Busca y retorna un producto por su id; lanza 404 si no existe.
     * @param id identificador único del producto.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> obtenerPorId(@PathVariable Long id){
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    /**
     * Activa un producto (activo=true) para que vuelva a aparecer en el catálogo visible.
     * No elimina ni recrea el producto; solo cambia su estado lógico.
     */
    @PutMapping("/{id}/activar")
    public ResponseEntity<ProductoResponse> activar(@PathVariable Long id){
        return ResponseEntity.ok(productoService.activar(id));
    }

    /**
     * Desactiva un producto (activo=false) para ocultarlo del catálogo sin borrar su historial.
     * Diseño deliberado: permite recuperar el producto en el futuro sin perder datos de ventas.
     */
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<ProductoResponse> desactivar(@PathVariable Long id){
        return ResponseEntity.ok(productoService.desactivar(id));
    }

    /**
     * Actualiza el precio de un producto. El nuevo precio se pasa como query param (?nuevoPrecio=X),
     * no en el body JSON, para minimizar el payload y hacer explícito que solo cambia el precio.
     */
    @PutMapping("/{id}/precio")
    public ResponseEntity<ProductoResponse> actualizarPrecio(@PathVariable Long id,
                                                             @RequestParam Double nuevoPrecio){
        return ResponseEntity.ok(productoService.actualizarPrecio(id, nuevoPrecio));
    }

    /**
     * Elimina físicamente un producto de la base de datos por su id.
     * Retorna HTTP 204 (No Content) si la operación fue exitosa.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
