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

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@RequestBody ProductoRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listar(){
        return ResponseEntity.ok(productoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> obtenerPorId(@PathVariable Long id){
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @PutMapping("/{id}/activar")
    public ResponseEntity<ProductoResponse> activar(@PathVariable Long id){
        return ResponseEntity.ok(productoService.activar(id));
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<ProductoResponse> desactivar(@PathVariable Long id){
        return ResponseEntity.ok(productoService.desactivar(id));
    }

    @PutMapping("/{id}/precio")
    public ResponseEntity<ProductoResponse> actualizarPrecio(@PathVariable Long id,
                                                             @RequestParam Double nuevoPrecio){
        return ResponseEntity.ok(productoService.actualizarPrecio(id, nuevoPrecio));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
