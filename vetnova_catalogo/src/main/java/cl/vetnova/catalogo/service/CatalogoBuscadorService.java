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

@Service
public class CatalogoBuscadorService {

    private static final Set<String> SUCURSALES = Set.of("SANTIAGO", "CHILLAN", "TALCA", "LOS_ANGELES");

    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private ServicioRepository servicioRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Producto> buscarPorNombre(String nombre){
        if (nombre == null) {
            throw new BusinessRuleException("El nombre de búsqueda es obligatorio");
        }
        if (nombre.isBlank()) {
            throw new BusinessRuleException("El nombre de búsqueda no puede estar vacío");
        }
        return productoRepository.findByActivoTrueAndNombreContainingIgnoreCase(nombre);
    }

    public List<Producto> filtrarPorCategoria(Long categoriaId){
        if (!categoriaRepository.existsById(categoriaId)) {
            throw new ResourceNotFoundException("Categoría no encontrada");
        }
        return productoRepository.findByCategoriaIdAndActivoTrue(categoriaId);
    }

    public List<Producto> filtrarPorRango(Double min, Double max){
        if (min < 0) {
            throw new BusinessRuleException("El precio mínimo no puede ser negativo");
        }
        if (min > max) {
            throw new BusinessRuleException("El precio mínimo no puede ser mayor al máximo");
        }
        return productoRepository.findByActivoTrueAndPrecioBetween(min, max);
    }

    public List<Producto> listarDisponibles(String sucursal){
        if (sucursal == null) {
            throw new BusinessRuleException("La sucursal es obligatoria");
        }
        if (!SUCURSALES.contains(sucursal)) {
            throw new ResourceNotFoundException("Sucursal no encontrada");
        }
        return productoRepository.findByActivoTrue();
    }

    public Object getDetalle(Long itemId, String tipo){
        if (!"producto".equalsIgnoreCase(tipo) && !"servicio".equalsIgnoreCase(tipo)) {
            throw new BusinessRuleException("Tipo no válido. Valores permitidos: producto, servicio");
        }
        if ("servicio".equalsIgnoreCase(tipo)) {
            return servicioRepository.findById(itemId)
                    .filter(s -> Boolean.TRUE.equals(s.getActivo()))
                    .orElseThrow(() -> new ResourceNotFoundException("Ítem no encontrado"));
        }
        return productoRepository.findById(itemId)
                .filter(p -> Boolean.TRUE.equals(p.getActivo()))
                .orElseThrow(() -> new ResourceNotFoundException("Ítem no encontrado"));
    }
}
