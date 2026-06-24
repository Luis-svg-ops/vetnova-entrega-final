package cl.vetnova.envio.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.envio.exception.BusinessRuleException;
import cl.vetnova.envio.exception.ConflictException;
import cl.vetnova.envio.exception.ResourceNotFoundException;
import cl.vetnova.envio.model.GuiaDespacho;
import cl.vetnova.envio.model.ItemDespacho;
import cl.vetnova.envio.repository.DespachoRepository;
import cl.vetnova.envio.repository.GuiaDespachoRepository;
import cl.vetnova.envio.repository.ItemDespachoRepository;

@Service
public class GuiaDespachoService {

    @Autowired
    private GuiaDespachoRepository guiaDespachoRepository;

    @Autowired
    private DespachoRepository despachoRepository;

    @Autowired
    private ItemDespachoRepository itemDespachoRepository;

    public List<GuiaDespacho> listar() {
        return guiaDespachoRepository.findAll();
    }

    public GuiaDespacho obtenerPorId(Long id) {
        return guiaDespachoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GuiaDespacho no encontrado con id " + id));
    }

    // CA-GUI-01..13: genera una guía con folio único para un despacho con ítems.
    public GuiaDespacho crear(GuiaDespacho guia) {
        if (guia.getDespachoId() == null) {
            throw new BusinessRuleException("El despachoId es obligatorio");
        }
        if (!despachoRepository.existsById(guia.getDespachoId())) {
            throw new ResourceNotFoundException("Despacho no encontrado");
        }
        if (guiaDespachoRepository.existsByDespachoId(guia.getDespachoId())) {
            throw new ConflictException("El despacho ya tiene una guía de despacho generada");
        }
        List<ItemDespacho> items = itemDespachoRepository.findByDespachoId(guia.getDespachoId());
        if (items.isEmpty()) {
            throw new BusinessRuleException("No se puede generar guía para un despacho sin ítems");
        }
        if (guia.getOrigen() == null) {
            throw new BusinessRuleException("El origen es obligatorio");
        }
        if (guia.getDestino() == null) {
            throw new BusinessRuleException("El destino es obligatorio");
        }
        if (guia.getOrigen().equals(guia.getDestino())) {
            throw new BusinessRuleException("El destino debe ser distinto al origen");
        }
        if (guia.getResponsable() == null) {
            throw new BusinessRuleException("El responsable es obligatorio");
        }
        String folio = generarFolio();
        if (guiaDespachoRepository.existsByFolio(folio)) {
            throw new ConflictException("Ya existe una guía con ese folio");
        }
        guia.setFolio(folio);
        guia.setFechaEmision(LocalDateTime.now(ZoneOffset.UTC));
        guia.setProductos(items.stream()
                .map(item -> "Producto " + item.getProductoId() + " x" + item.getCantidad())
                .toList());
        return guiaDespachoRepository.save(guia);
    }

    public GuiaDespacho actualizar(Long id, GuiaDespacho datos) {
        GuiaDespacho existente = obtenerPorId(id);
        existente.setOrigen(datos.getOrigen());
        existente.setDestino(datos.getDestino());
        existente.setResponsable(datos.getResponsable());
        return guiaDespachoRepository.save(existente);
    }

    public void eliminar(Long id) {
        if (!guiaDespachoRepository.existsById(id)) {
            throw new ResourceNotFoundException("GuiaDespacho no encontrado con id " + id);
        }
        guiaDespachoRepository.deleteById(id);
    }

    private String generarFolio() {
        return "GD-" + String.format("%04d", guiaDespachoRepository.count() + 1);
    }
}
