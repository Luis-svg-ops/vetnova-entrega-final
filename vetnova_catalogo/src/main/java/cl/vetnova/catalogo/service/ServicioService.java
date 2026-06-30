package cl.vetnova.catalogo.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.catalogo.exception.BusinessRuleException;
import cl.vetnova.catalogo.exception.ConflictException;
import cl.vetnova.catalogo.exception.ResourceNotFoundException;
import cl.vetnova.catalogo.model.Servicio;
import cl.vetnova.catalogo.repository.CategoriaRepository;
import cl.vetnova.catalogo.repository.ServicioRepository;

// Gestiona los servicios veterinarios del catálogo (baños, cirugías, consultas, etc.)
@Service
public class ServicioService {
    private static final Logger log = LoggerFactory.getLogger(ServicioService.class);

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    // Si el campo activo no viene en el request, se asume true por defecto
    public Servicio crear(Servicio servicio) {
        if (servicio.getNombre() == null) {
            throw new BusinessRuleException("El nombre es obligatorio");
        }
        if (servicio.getNombre().isBlank()) {
            throw new BusinessRuleException("El nombre no puede estar vacío");
        }
        // Evita duplicados semánticos como "Baño canino" y "baño canino"
        if (servicioRepository.existsByNombreIgnoreCase(servicio.getNombre())) {
            throw new ConflictException("Ya existe un servicio con ese nombre");
        }
        validarPrecio(servicio.getPrecio());
        validarDuracion(servicio.getDuracionMinutos());
        if (servicio.getCategoriaId() == null) {
            throw new BusinessRuleException("La categoría es obligatoria");
        }
        // Validación interna: repositorio de categorías de esta misma BD, no otro MS
        if (!categoriaRepository.existsById(servicio.getCategoriaId())) {
            throw new ResourceNotFoundException("Categoría no encontrada");
        }
        log.info("event=crear_servicio nombre={}", servicio.getNombre());
        if (servicio.getActivo() == null) {
            servicio.setActivo(true);
        }
        return servicioRepository.save(servicio);
    }

    public List<Servicio> listar() {
        return servicioRepository.findAll();
    }

    // Vuelve a ofrecerse en el catálogo sin recrearlo
    public Servicio activar(Long id) {
        log.info("event=activar_servicio servicioId={}", id);
        Servicio servicio = buscar(id);
        servicio.setActivo(true);
        return servicioRepository.save(servicio);
    }

    // Soft delete: conserva la configuración y el historial para poder reactivarlo
    public Servicio desactivar(Long id) {
        log.info("event=desactivar_servicio servicioId={}", id);
        Servicio servicio = buscar(id);
        servicio.setActivo(false);
        return servicioRepository.save(servicio);
    }

    // El nuevo precio llega como query param desde el endpoint /precio, no en el body JSON
    public Servicio actualizarPrecio(Long id, Double nuevoPrecio) {
        log.info("event=actualizar_precio_servicio servicioId={} precio={}", id, nuevoPrecio);
        Servicio servicio = buscar(id);
        validarPrecio(nuevoPrecio);
        servicio.setPrecio(nuevoPrecio);
        return servicioRepository.save(servicio);
    }

    public void eliminar(Long id) {
        log.info("event=eliminar_servicio servicioId={}", id);
        buscar(id);
        servicioRepository.deleteById(id);
    }

    // Reutilizado en crear() y actualizarPrecio()
    private void validarPrecio(Double precio) {
        if (precio == null) {
            throw new BusinessRuleException("El precio es obligatorio");
        }
        if (precio <= 0) {
            throw new BusinessRuleException("El precio debe ser mayor a 0");
        }
    }

    // Un servicio con duración cero o negativa no tiene sentido en contexto veterinario
    private void validarDuracion(Integer duracionMinutos) {
        if (duracionMinutos == null) {
            throw new BusinessRuleException("La duración es obligatoria");
        }
        if (duracionMinutos <= 0) {
            throw new BusinessRuleException("La duración debe ser mayor a 0 minutos");
        }
    }

    // Centraliza el 404 para no repetirlo en cada método público
    private Servicio buscar(Long id) {
        return servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con id " + id));
    }
}
