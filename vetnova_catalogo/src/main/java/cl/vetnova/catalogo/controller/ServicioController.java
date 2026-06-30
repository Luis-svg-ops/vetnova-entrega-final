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

/**
 * Controlador REST que expone los endpoints de gestión de servicios veterinarios del catálogo.
 * Los servicios (baños, cirugías, consultas, etc.) tienen precio y duración en minutos, igual que los productos pueden activarse/desactivarse.
 */
@RestController
@RequestMapping("/api/v1/servicios")
public class ServicioController {

    @Autowired
    private ServicioService servicioService;

    /**
     * Crea un nuevo servicio en el catálogo mapeando el body JSON a la entidad Servicio.
     * Retorna el servicio persistido con estado HTTP 201 (Created).
     */
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

    /**
     * Retorna la lista completa de servicios registrados, incluyendo los inactivos.
     * Para ver solo los servicios disponibles, usar el endpoint de catálogo.
     */
    @GetMapping
    public ResponseEntity<List<Servicio>> listar(){
        return ResponseEntity.ok(servicioService.listar());
    }

    /**
     * Activa un servicio (activo=true) para que vuelva a ofrecerse en el catálogo.
     * No elimina ni recrea; solo cambia el campo booleano activo.
     */
    @PutMapping("/{id}/activar")
    public ResponseEntity<Servicio> activar(@PathVariable Long id){
        return ResponseEntity.ok(servicioService.activar(id));
    }

    /**
     * Desactiva un servicio (activo=false) para ocultarlo sin perder su configuración ni historial.
     * Diseño deliberado: permite reactivarlo en el futuro sin recrearlo.
     */
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<Servicio> desactivar(@PathVariable Long id){
        return ResponseEntity.ok(servicioService.desactivar(id));
    }

    /**
     * Actualiza el precio de un servicio. El nuevo valor llega como query param (?nuevoPrecio=X),
     * no en el body JSON, siguiendo el mismo patrón que el endpoint de precio de productos.
     */
    @PutMapping("/{id}/precio")
    public ResponseEntity<Servicio> actualizarPrecio(@PathVariable Long id,
                                                     @RequestParam Double nuevoPrecio){
        return ResponseEntity.ok(servicioService.actualizarPrecio(id, nuevoPrecio));
    }

    /**
     * Elimina físicamente un servicio de la base de datos por su id.
     * Retorna HTTP 204 (No Content) si la eliminación fue exitosa.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        servicioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
