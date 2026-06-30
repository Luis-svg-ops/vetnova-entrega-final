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

/**
 * Controlador REST que centraliza los endpoints de búsqueda y filtrado del catálogo público.
 * Provee búsqueda unificada por nombre, categoría, rango de precio y sucursal; siempre trabaja sobre datos de esta misma BD.
 */
@RestController
@RequestMapping("/api/v1/catalogo")
public class CatalogoBuscadorController {

    @Autowired
    private CatalogoBuscadorService buscadorService;

    /**
     * Busca productos activos cuyo nombre contenga el texto indicado (insensible a mayúsculas).
     * @param nombre texto parcial o completo a buscar; es obligatorio (lanza 400 si falta).
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarPorNombre(@RequestParam(required = false) String nombre){
        return ResponseEntity.ok(buscadorService.buscarPorNombre(nombre));
    }

    /**
     * Filtra y retorna productos activos que pertenecen a la categoría indicada.
     * @param categoriaId id de la categoría; lanza 404 si la categoría no existe.
     */
    @GetMapping("/buscar/categoria")
    public ResponseEntity<List<Producto>> filtrarPorCategoria(@RequestParam Long categoriaId){
        return ResponseEntity.ok(buscadorService.filtrarPorCategoria(categoriaId));
    }

    /**
     * Filtra productos activos cuyo precio está entre los valores min y max indicados.
     * @param min precio mínimo (no puede ser negativo ni mayor que max).
     * @param max precio máximo del rango buscado.
     */
    @GetMapping("/buscar/rango")
    public ResponseEntity<List<Producto>> filtrarPorRango(@RequestParam Double min, @RequestParam Double max){
        return ResponseEntity.ok(buscadorService.filtrarPorRango(min, max));
    }

    /**
     * Lista todos los productos activos disponibles para una sucursal válida del sistema.
     * @param sucursal nombre de la sucursal (CHILLAN, LOS_ANGELES, TALCA, SANTIAGO); lanza 404 si no existe.
     */
    @GetMapping("/disponibles")
    public ResponseEntity<List<Producto>> listarDisponibles(@RequestParam(required = false) String sucursal){
        return ResponseEntity.ok(buscadorService.listarDisponibles(sucursal));
    }

    /**
     * Retorna el detalle de un ítem activo por id, diferenciando si es producto o servicio mediante el param tipo.
     * @param itemId id del producto o servicio; @param tipo valor permitido: "producto" o "servicio".
     */
    @GetMapping("/detalle")
    public ResponseEntity<Object> getDetalle(@RequestParam Long itemId, @RequestParam String tipo){
        return ResponseEntity.ok(buscadorService.getDetalle(itemId, tipo));
    }
}
