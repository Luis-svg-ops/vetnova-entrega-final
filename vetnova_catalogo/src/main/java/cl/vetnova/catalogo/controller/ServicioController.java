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

import cl.vetnova.catalogo.dto.ServicioRequest;
import cl.vetnova.catalogo.model.Servicio;
import cl.vetnova.catalogo.service.ServicioService;

@RestController
@RequestMapping("/api/v1/servicios")
public class ServicioController {

    @Autowired
    private ServicioService servicioService;

    @PostMapping
    public ResponseEntity<Servicio> crear(@RequestBody ServicioRequest request){
        Servicio servicio = new Servicio();
        servicio.setNombre(request.nombre());
        servicio.setDescripcion(request.descripcion());
        servicio.setPrecio(request.precio());
        servicio.setDuracionMinutos(request.duracionMinutos());
        servicio.setActivo(request.activo());
        servicio.setCategoriaId(request.categoriaId());
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioService.crear(servicio));
    }

    @GetMapping
    public ResponseEntity<List<Servicio>> listar(){
        return ResponseEntity.ok(servicioService.listar());
    }

    @PutMapping("/{id}/activar")
    public ResponseEntity<Servicio> activar(@PathVariable Long id){
        return ResponseEntity.ok(servicioService.activar(id));
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<Servicio> desactivar(@PathVariable Long id){
        return ResponseEntity.ok(servicioService.desactivar(id));
    }

    @PutMapping("/{id}/precio")
    public ResponseEntity<Servicio> actualizarPrecio(@PathVariable Long id,
                                                     @RequestParam Double nuevoPrecio){
        return ResponseEntity.ok(servicioService.actualizarPrecio(id, nuevoPrecio));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        servicioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
