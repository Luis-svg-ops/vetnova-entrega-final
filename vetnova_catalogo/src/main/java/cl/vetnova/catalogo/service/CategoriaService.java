package cl.vetnova.catalogo.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.catalogo.exception.BusinessRuleException;
import cl.vetnova.catalogo.exception.ConflictException;
import cl.vetnova.catalogo.exception.ResourceNotFoundException;
import cl.vetnova.catalogo.model.Categoria;
import cl.vetnova.catalogo.repository.CategoriaRepository;
import cl.vetnova.catalogo.repository.ProductoRepository;
import cl.vetnova.catalogo.repository.ServicioRepository;

@Service
public class CategoriaService {
    private static final Logger log = LoggerFactory.getLogger(CategoriaService.class);

    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private ServicioRepository servicioRepository;

    public Categoria crear(Categoria categoria){
        if (categoria.getNombre() == null) {
            throw new BusinessRuleException("El nombre es obligatorio");
        }
        if (categoria.getNombre().isBlank()) {
            throw new BusinessRuleException("El nombre no puede estar vacío");
        }
        if (categoriaRepository.existsByNombreIgnoreCase(categoria.getNombre())) {
            throw new ConflictException("Ya existe una categoría con ese nombre");
        }
        if (categoria.getTipo() == null) {
            throw new BusinessRuleException("El tipo es obligatorio");
        }
        if (!"producto".equalsIgnoreCase(categoria.getTipo()) && !"servicio".equalsIgnoreCase(categoria.getTipo())) {
            throw new BusinessRuleException("Tipo no válido. Valores permitidos: producto, servicio");
        }
        log.info("event=crear_categoria nombre={}", categoria.getNombre());
        return categoriaRepository.save(categoria);
    }

    public List<Categoria> listar(){
        return categoriaRepository.findAll();
    }

    public void eliminar(Long id){
        log.info("event=eliminar_categoria categoriaId={}", id);
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoría no encontrada con id " + id);
        }
        if (productoRepository.existsByCategoriaId(id) || servicioRepository.existsByCategoriaId(id)) {
            throw new BusinessRuleException("No se puede eliminar una categoría con items. Reasigne primero");
        }
        categoriaRepository.deleteById(id);
    }
}
