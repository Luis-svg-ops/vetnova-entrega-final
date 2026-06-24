package cl.vetnova.envio.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.envio.exception.BusinessRuleException;
import cl.vetnova.envio.exception.ConflictException;
import cl.vetnova.envio.exception.ResourceNotFoundException;
import cl.vetnova.envio.model.Despacho;
import cl.vetnova.envio.model.ItemDespacho;
import cl.vetnova.envio.repository.DespachoRepository;
import cl.vetnova.envio.repository.ItemDespachoRepository;

@Service
public class ItemDespachoService {

    private static final Set<String> NO_EDITABLES = Set.of("ENTREGADO", "CANCELADO");

    @Autowired
    private ItemDespachoRepository itemDespachoRepository;

    @Autowired
    private DespachoRepository despachoRepository;

    public List<ItemDespacho> listar() {
        return itemDespachoRepository.findAll();
    }

    public ItemDespacho obtenerPorId(Long id) {
        return itemDespachoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ItemDespacho no encontrado con id " + id));
    }

    public ItemDespacho crear(ItemDespacho itemDespacho) {
        return itemDespachoRepository.save(itemDespacho);
    }

    // CA-IDE-02..12: agrega un ítem a un despacho editable; hereda el estado del despacho.
    public ItemDespacho agregarItem(Long despachoId, ItemDespacho item) {
        Despacho despacho = despachoRepository.findById(despachoId)
                .orElseThrow(() -> new ResourceNotFoundException("Despacho no encontrado"));
        if (NO_EDITABLES.contains(despacho.getEstado())) {
            throw new BusinessRuleException("No se pueden agregar ítems a un despacho entregado o cancelado");
        }
        if (item.getProductoId() == null) {
            throw new BusinessRuleException("El productoId es obligatorio");
        }
        if (item.getCantidad() == null) {
            throw new BusinessRuleException("La cantidad es obligatoria");
        }
        if (item.getCantidad() <= 0) {
            throw new BusinessRuleException("La cantidad debe ser mayor a 0");
        }
        if (itemDespachoRepository.existsByDespachoIdAndProductoId(despachoId, item.getProductoId())) {
            throw new ConflictException("El producto ya está en este despacho");
        }
        item.setDespachoId(despachoId);
        item.setEstado(despacho.getEstado());
        return itemDespachoRepository.save(item);
    }

    // CA-IDE-13/14: elimina un ítem salvo que sea el único del despacho.
    public void eliminarItem(Long despachoId, Long itemId) {
        ItemDespacho item = obtenerPorId(itemId);
        if (itemDespachoRepository.countByDespachoId(despachoId) <= 1) {
            throw new BusinessRuleException("El despacho debe tener al menos un ítem");
        }
        itemDespachoRepository.deleteById(item.getId());
    }

    public ItemDespacho actualizar(Long id, ItemDespacho datos) {
        ItemDespacho existente = obtenerPorId(id);
        existente.setProductoId(datos.getProductoId());
        existente.setCantidad(datos.getCantidad());
        existente.setEstado(datos.getEstado());
        return itemDespachoRepository.save(existente);
    }

    public void eliminar(Long id) {
        if (!itemDespachoRepository.existsById(id)) {
            throw new ResourceNotFoundException("ItemDespacho no encontrado con id " + id);
        }
        itemDespachoRepository.deleteById(id);
    }
}
