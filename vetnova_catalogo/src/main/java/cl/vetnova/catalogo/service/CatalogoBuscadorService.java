package cl.vetnova.catalogo.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.catalogo.exception.BusinessRuleException;
import cl.vetnova.catalogo.exception.ResourceNotFoundException;
import cl.vetnova.catalogo.model.Producto;
import cl.vetnova.catalogo.repository.CategoriaRepository;
import cl.vetnova.catalogo.repository.ProductoRepository;
import cl.vetnova.catalogo.repository.ServicioRepository;

/**
 * Servicio de búsqueda unificada del catálogo veterinario; centraliza todos los filtros de consulta pública.
 * Opera exclusivamente sobre la BD local del microservicio; no realiza llamadas a otros microservicios.
 */
@Service
public class CatalogoBuscadorService {

    // Sucursales válidas hardcodeadas; cualquier valor fuera de este Set retorna 404
    private static final Set<String> SUCURSALES = Set.of("CHILLAN", "LOS_ANGELES", "TALCA", "SANTIAGO");

    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private ServicioRepository servicioRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;

    /**
     * Busca productos activos cuyo nombre contenga el texto dado (búsqueda parcial, insensible a mayúsculas).
     * El nombre es obligatorio; se lanza BusinessRuleException si viene nulo o en blanco.
     */
    public List<Producto> buscarPorNombre(String nombre){
        if (nombre == null) {
            throw new BusinessRuleException("El nombre de búsqueda es obligatorio");
        }
        if (nombre.isBlank()) {
            throw new BusinessRuleException("El nombre de búsqueda no puede estar vacío");
        }
        return productoRepository.findByActivoTrueAndNombreContainingIgnoreCase(nombre);
    }

    /**
     * Filtra y retorna solo los productos activos de una categoría determinada.
     * Valida que la categoría exista en la BD antes de consultar los productos asociados.
     */
    public List<Producto> filtrarPorCategoria(Long categoriaId){
        // Se verifica la categoría en la BD local; este MS no llama a otro para validarla
        if (!categoriaRepository.existsById(categoriaId)) {
            throw new ResourceNotFoundException("Categoría no encontrada");
        }
        return productoRepository.findByCategoriaIdAndActivoTrue(categoriaId);
    }

    /**
     * Filtra productos activos con precio dentro del rango [min, max] indicado.
     * Lanza BusinessRuleException si el rango es inválido (min negativo o min mayor que max).
     */
    public List<Producto> filtrarPorRango(Double min, Double max){
        if (min < 0) {
            throw new BusinessRuleException("El precio mínimo no puede ser negativo");
        }
        if (min > max) {
            throw new BusinessRuleException("El precio mínimo no puede ser mayor al máximo");
        }
        return productoRepository.findByActivoTrueAndPrecioBetween(min, max);
    }

    /**
     * Lista todos los productos activos disponibles para la sucursal solicitada.
     * La sucursal debe ser uno de los cuatro valores válidos del Set SUCURSALES; de lo contrario, lanza 404.
     */
    public List<Producto> listarDisponibles(String sucursal){
        if (sucursal == null) {
            throw new BusinessRuleException("La sucursal es obligatoria");
        }
        // Validación contra el conjunto fijo de sucursales; no existe un MS de sucursales al que llamar
        if (!SUCURSALES.contains(sucursal)) {
            throw new ResourceNotFoundException("Sucursal no encontrada");
        }
        return productoRepository.findByActivoTrue();
    }

    /**
     * Retorna el detalle de un ítem activo (producto o servicio) según el tipo indicado.
     * Si el ítem existe pero está inactivo, se lanza ResourceNotFoundException como si no existiera.
     */
    public Object getDetalle(Long itemId, String tipo){
        // Solo se permiten dos tipos de ítem; cualquier otro valor es un error del cliente
        if (!"producto".equalsIgnoreCase(tipo) && !"servicio".equalsIgnoreCase(tipo)) {
            throw new BusinessRuleException("Tipo no válido. Valores permitidos: producto, servicio");
        }
        if ("servicio".equalsIgnoreCase(tipo)) {
            // Se filtra por activo=true después de encontrar el registro para unificar el mensaje de error
            return servicioRepository.findById(itemId)
                    .filter(s -> Boolean.TRUE.equals(s.getActivo()))
                    .orElseThrow(() -> new ResourceNotFoundException("Ítem no encontrado"));
        }
        return productoRepository.findById(itemId)
                .filter(p -> Boolean.TRUE.equals(p.getActivo()))
                .orElseThrow(() -> new ResourceNotFoundException("Ítem no encontrado"));
    }
}
