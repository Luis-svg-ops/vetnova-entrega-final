package cl.vetnova.catalogo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.vetnova.catalogo.model.Producto;
import cl.vetnova.catalogo.service.CatalogoBuscadorService;

@RestController
@RequestMapping("/api/v1/catalogo")
public class CatalogoBuscadorController {

    @Autowired
    private CatalogoBuscadorService buscadorService;

    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarPorNombre(@RequestParam(required = false) String nombre){
        return ResponseEntity.ok(buscadorService.buscarPorNombre(nombre));
    }

    @GetMapping("/buscar/categoria")
    public ResponseEntity<List<Producto>> filtrarPorCategoria(@RequestParam Long categoriaId){
        return ResponseEntity.ok(buscadorService.filtrarPorCategoria(categoriaId));
    }

    @GetMapping("/buscar/rango")
    public ResponseEntity<List<Producto>> filtrarPorRango(@RequestParam Double min, @RequestParam Double max){
        return ResponseEntity.ok(buscadorService.filtrarPorRango(min, max));
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<Producto>> listarDisponibles(@RequestParam(required = false) String sucursal){
        return ResponseEntity.ok(buscadorService.listarDisponibles(sucursal));
    }

    @GetMapping("/detalle")
    public ResponseEntity<Object> getDetalle(@RequestParam Long itemId, @RequestParam String tipo){
        return ResponseEntity.ok(buscadorService.getDetalle(itemId, tipo));
    }
}
