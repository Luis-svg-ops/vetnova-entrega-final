package cl.vetnova.catalogo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.vetnova.catalogo.dto.CategoriaRequest;
import cl.vetnova.catalogo.model.Categoria;
import cl.vetnova.catalogo.service.CategoriaService;

/**
 * Controlador REST que expone los endpoints de gestión de categorías del catálogo.
 * Las categorías clasifican productos y servicios; son el punto de partida para organizar el catálogo.
 */
@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    /**
     * Crea una nueva categoría a partir de los datos enviados en el body JSON.
     * Retorna la categoría persistida con su id generado y estado HTTP 201 (Created).
     */
    @PostMapping
    public ResponseEntity<Categoria> crear(@RequestBody CategoriaRequest request){
        Categoria categoria = new Categoria();
        categoria.setNombre(request.nombre());
        categoria.setDescripcion(request.descripcion());
        categoria.setTipo(request.tipo());
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.crear(categoria));
    }

    /**
     * Retorna la lista completa de categorías registradas en el sistema.
     * No aplica filtros; si se necesita filtrar, usar el buscador de catálogo.
     */
    @GetMapping
    public ResponseEntity<List<Categoria>> listar(){
        return ResponseEntity.ok(categoriaService.listar());
    }

    /**
     * Elimina físicamente una categoría por su id, solo si no tiene productos ni servicios asociados.
     * Retorna HTTP 204 (No Content) cuando la eliminación es exitosa.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
