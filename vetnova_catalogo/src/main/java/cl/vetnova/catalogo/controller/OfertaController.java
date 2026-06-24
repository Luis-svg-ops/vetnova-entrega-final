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

@RestController
@RequestMapping("/api/v1/ofertas")
public class OfertaController {

    @Autowired
    private OfertaService ofertaService;

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

    @GetMapping
    public ResponseEntity<List<Oferta>> listar(){
        return ResponseEntity.ok(ofertaService.listar());
    }

    @PutMapping("/{id}/activar")
    public ResponseEntity<Oferta> activar(@PathVariable Long id){
        return ResponseEntity.ok(ofertaService.activar(id));
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<Oferta> desactivar(@PathVariable Long id){
        return ResponseEntity.ok(ofertaService.desactivar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        ofertaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
