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
import org.springframework.web.bind.annotation.RestController;

import cl.vetnova.catalogo.dto.OfertaRequest;
import cl.vetnova.catalogo.model.Oferta;
import cl.vetnova.catalogo.service.OfertaService;

/**
 * Controlador REST que expone los endpoints de gestión de ofertas sobre productos del catálogo.
 * Una oferta aplica un descuento porcentual a un producto durante un rango de fechas determinado.
 */
@RestController
@RequestMapping("/api/v1/ofertas")
public class OfertaController {

    @Autowired
    private OfertaService ofertaService;

    /**
     * Crea una nueva oferta asociada a un producto, con descuento y rango de fechas en el body JSON.
     * Retorna la oferta persistida con estado HTTP 201; el servicio valida solapamiento con otras ofertas activas.
     */
    @PostMapping
    public ResponseEntity<Oferta> crear(@RequestBody OfertaRequest request){
        Oferta oferta = new Oferta();
        oferta.setProductoId(request.productoId());
        oferta.setDescuento(request.descuento());
        oferta.setFechaInicio(request.fechaInicio());
        oferta.setFechaFin(request.fechaFin());
        oferta.setActiva(request.activa());
        return ResponseEntity.status(HttpStatus.CREATED).body(ofertaService.crear(oferta));
    }

    /**
     * Retorna todas las ofertas registradas, incluyendo las inactivas y vencidas.
     * Permite a administradores revisar el historial completo de promociones.
     */
    @GetMapping
    public ResponseEntity<List<Oferta>> listar(){
        return ResponseEntity.ok(ofertaService.listar());
    }

    /**
     * Activa una oferta (activa=true) para que sea considerada en el catálogo de promociones.
     * No se valida solapamiento en la activación; eso ocurre solo al crear.
     */
    @PutMapping("/{id}/activar")
    public ResponseEntity<Oferta> activar(@PathVariable Long id){
        return ResponseEntity.ok(ofertaService.activar(id));
    }

    /**
     * Desactiva una oferta (activa=false) sin eliminarla, conservando el registro histórico.
     * Diseño deliberado: permite reactivarla o usarla como referencia sin perder datos.
     */
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<Oferta> desactivar(@PathVariable Long id){
        return ResponseEntity.ok(ofertaService.desactivar(id));
    }

    /**
     * Elimina físicamente una oferta de la base de datos por su id.
     * Retorna HTTP 204 (No Content) si la operación fue exitosa.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        ofertaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
