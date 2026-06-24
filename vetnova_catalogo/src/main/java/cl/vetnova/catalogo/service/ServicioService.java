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

@Service
public class ServicioService {
    private static final Logger log = LoggerFactory.getLogger(ServicioService.class);

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    public Servicio crear(Servicio servicio){
        if (servicio.getNombre() == null) {
            throw new BusinessRuleException("El nombre es obligatorio");
        }
        if (servicio.getNombre().isBlank()) {
            throw new BusinessRuleException("El nombre no puede estar vacío");
        }
        if (servicioRepository.existsByNombreIgnoreCase(servicio.getNombre())) {
            throw new ConflictException("Ya existe un servicio con ese nombre");
        }
        validarPrecio(servicio.getPrecio());
        validarDuracion(servicio.getDuracionMinutos());
        if (servicio.getCategoriaId() == null) {
            throw new BusinessRuleException("La categoría es obligatoria");
        }
        if (!categoriaRepository.existsById(servicio.getCategoriaId())) {
            throw new ResourceNotFoundException("Categoría no encontrada");
        }
        log.info("event=crear_servicio nombre={}", servicio.getNombre());
        if (servicio.getActivo() == null) {
            servicio.setActivo(true);
        }
        return servicioRepository.save(servicio);
    }

    public List<Servicio> listar(){
        return servicioRepository.findAll();
    }

    public Servicio activar(Long id){
        log.info("event=activar_servicio servicioId={}", id);
        Servicio servicio = buscar(id);
        servicio.setActivo(true);
        return servicioRepository.save(servicio);
    }

    public Servicio desactivar(Long id){
        log.info("event=desactivar_servicio servicioId={}", id);
        Servicio servicio = buscar(id);
        servicio.setActivo(false);
        return servicioRepository.save(servicio);
    }

    public Servicio actualizarPrecio(Long id, Double nuevoPrecio){
        log.info("event=actualizar_precio_servicio servicioId={} precio={}", id, nuevoPrecio);
        Servicio servicio = buscar(id);
        validarPrecio(nuevoPrecio);
        servicio.setPrecio(nuevoPrecio);
        return servicioRepository.save(servicio);
    }

    public void eliminar(Long id){
        log.info("event=eliminar_servicio servicioId={}", id);
        buscar(id);
        servicioRepository.deleteById(id);
    }

    private void validarPrecio(Double precio){
        if (precio == null) {
            throw new BusinessRuleException("El precio es obligatorio");
        }
        if (precio <= 0) {
            throw new BusinessRuleException("El precio debe ser mayor a 0");
        }
    }

    private void validarDuracion(Integer duracionMinutos){
        if (duracionMinutos == null) {
            throw new BusinessRuleException("La duración es obligatoria");
        }
        if (duracionMinutos <= 0) {
            throw new BusinessRuleException("La duración debe ser mayor a 0 minutos");
        }
    }

    private Servicio buscar(Long id){
        return servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con id " + id));
    }
}
