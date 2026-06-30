package cl.vetnova.catalogo.service;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.catalogo.exception.BusinessRuleException;
import cl.vetnova.catalogo.exception.ConflictException;
import cl.vetnova.catalogo.exception.ResourceNotFoundException;
import cl.vetnova.catalogo.model.Oferta;
import cl.vetnova.catalogo.model.Producto;
import cl.vetnova.catalogo.repository.OfertaRepository;
import cl.vetnova.catalogo.repository.ProductoRepository;

// Gestiona descuentos por período sobre productos; valida que no se solapan con otras ofertas activas
@Service
public class OfertaService {
    private static final Logger log = LoggerFactory.getLogger(OfertaService.class);

    @Autowired
    private OfertaRepository ofertaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    public Oferta crear(Oferta oferta) {
        if (oferta.getProductoId() == null) {
            throw new BusinessRuleException("El productoId es obligatorio");
        }
        Producto producto = productoRepository.findById(oferta.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        // No tiene sentido una oferta sobre un producto que no es visible en el catálogo
        if (!Boolean.TRUE.equals(producto.getActivo())) {
            throw new BusinessRuleException("No se puede crear oferta para producto inactivo");
        }
        validarDescuento(oferta.getDescuento());
        validarFechas(oferta.getFechaInicio(), oferta.getFechaFin());
        // Se revisan todas las ofertas activas del mismo producto para detectar solapamiento de períodos
        for (Oferta existente : ofertaRepository.findByProductoIdAndActivaTrue(oferta.getProductoId())) {
            if (seSolapan(oferta.getFechaInicio(), oferta.getFechaFin(),
                    existente.getFechaInicio(), existente.getFechaFin())) {
                throw new ConflictException("Ya existe una oferta vigente para este producto en ese período");
            }
        }
        log.info("event=crear_oferta productoId={}", oferta.getProductoId());
        if (oferta.getActiva() == null) {
            oferta.setActiva(true);
        }
        return ofertaRepository.save(oferta);
    }

    public List<Oferta> listar() {
        return ofertaRepository.findAll();
    }

    public Oferta activar(Long id) {
        log.info("event=activar_oferta ofertaId={}", id);
        Oferta oferta = buscar(id);
        oferta.setActiva(true);
        return ofertaRepository.save(oferta);
    }

    // Soft delete: conserva el registro histórico de promociones para consulta futura
    public Oferta desactivar(Long id) {
        log.info("event=desactivar_oferta ofertaId={}", id);
        Oferta oferta = buscar(id);
        oferta.setActiva(false);
        return ofertaRepository.save(oferta);
    }

    public void eliminar(Long id) {
        log.info("event=eliminar_oferta ofertaId={}", id);
        buscar(id);
        ofertaRepository.deleteById(id);
    }

    // Descuento 0 no tiene efecto; mayor a 100 resultaría en precio negativo
    private void validarDescuento(Double descuento) {
        if (descuento == null) {
            throw new BusinessRuleException("El descuento es obligatorio");
        }
        if (descuento <= 0) {
            throw new BusinessRuleException("El descuento debe ser mayor a 0");
        }
        if (descuento > 100) {
            throw new BusinessRuleException("El descuento porcentual no puede ser mayor a 100%");
        }
    }

    // Las ofertas siempre deben ser vigentes al momento de su creación — no se permiten retroactivas
    private void validarFechas(LocalDate inicio, LocalDate fin) {
        if (inicio == null) {
            throw new BusinessRuleException("La fecha de inicio es obligatoria");
        }
        if (fin == null) {
            throw new BusinessRuleException("La fecha de fin es obligatoria");
        }
        if (fin.isBefore(inicio)) {
            throw new BusinessRuleException("La fecha de fin debe ser igual o posterior a la fecha de inicio");
        }
        if (inicio.isBefore(LocalDate.now())) {
            throw new BusinessRuleException("La fecha de inicio no puede ser en el pasado");
        }
    }

    // Fórmula clásica de solapamiento: [A,B] y [C,D] se solapan si A <= D y C <= B
    private boolean seSolapan(LocalDate inicioA, LocalDate finA, LocalDate inicioB, LocalDate finB) {
        return !inicioA.isAfter(finB) && !inicioB.isAfter(finA);
    }

    // Centraliza el 404 para no repetirlo en cada método público
    private Oferta buscar(Long id) {
        return ofertaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oferta no encontrada con id " + id));
    }
}
