package cl.vetnova.ventas.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.ventas.dto.AgregarItemCarritoRequest;
import cl.vetnova.ventas.dto.CarritoItemResultado;
import cl.vetnova.ventas.exception.BusinessRuleException;
import cl.vetnova.ventas.exception.ConflictException;
import cl.vetnova.ventas.exception.ResourceNotFoundException;
import cl.vetnova.ventas.model.Carrito;
import cl.vetnova.ventas.model.ItemCarrito;
import cl.vetnova.ventas.repository.CarritoRepository;
import cl.vetnova.ventas.repository.ItemCarritoRepository;

@Service
public class CarritoService {

    private static final Set<String> TIPOS = Set.of("PRODUCTO", "SERVICIO");

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ItemCarritoRepository itemCarritoRepository;

    public List<Carrito> listar() {
        return carritoRepository.findAll();
    }

    public Carrito obtenerPorId(Long id) {
        return conItems(buscar(id));
    }

    public Carrito crear(Carrito carrito) {
        if (carrito.getClienteId() == null) {
            throw new BusinessRuleException("El clienteId es obligatorio");
        }
        if (carritoRepository.existsByClienteIdAndActivoTrue(carrito.getClienteId())) {
            throw new ConflictException("El cliente ya tiene un carrito activo");
        }
        carrito.setTotal(0.0);
        carrito.setActivo(true);
        return carritoRepository.save(carrito);
    }

    // CA-CAR-07/09/10: agrega un ítem; si ya existe en el carrito acumula la cantidad.
    public CarritoItemResultado agregarItem(Long carritoId, AgregarItemCarritoRequest request) {
        Carrito carrito = buscar(carritoId);
        if (request.getTipo() == null) {
            throw new BusinessRuleException("El tipo de ítem es obligatorio");
        }
        if (!TIPOS.contains(request.getTipo())) {
            throw new BusinessRuleException("Tipo no válido. Valores permitidos: PRODUCTO, SERVICIO");
        }
        if (request.getCantidad() == null) {
            throw new BusinessRuleException("La cantidad es obligatoria");
        }
        if (request.getCantidad() <= 0) {
            throw new BusinessRuleException("La cantidad debe ser mayor a 0");
        }
        ItemCarrito item = itemCarritoRepository.findByCarritoIdAndItemId(carritoId, request.getItemId()).orElse(null);
        boolean creado = item == null;
        if (creado) {
            item = new ItemCarrito();
            item.setCarritoId(carritoId);
            item.setItemId(request.getItemId());
            item.setTipo(request.getTipo());
            item.setNombre(request.getNombre());
            item.setPrecio(request.getPrecio());
            item.setCantidad(request.getCantidad());
        } else {
            item.setCantidad(item.getCantidad() + request.getCantidad());
        }
        item.setSubtotal(item.getPrecio() * item.getCantidad());
        itemCarritoRepository.save(item);
        return new CarritoItemResultado(refrescar(carrito), creado);
    }

    // CA-CAR-11/12: quita un ítem del carrito.
    public Carrito quitarItem(Long carritoId, Long itemId) {
        Carrito carrito = buscar(carritoId);
        ItemCarrito item = itemCarritoRepository.findByCarritoIdAndItemId(carritoId, itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem no encontrado en el carrito"));
        itemCarritoRepository.delete(item);
        return refrescar(carrito);
    }

    // CA-CAR-13/15: actualiza la cantidad de un ítem y recalcula su subtotal.
    public Carrito actualizarCantidad(Long carritoId, Long itemId, Integer cantidad) {
        Carrito carrito = buscar(carritoId);
        ItemCarrito item = itemCarritoRepository.findByCarritoIdAndItemId(carritoId, itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem no encontrado en el carrito"));
        if (cantidad == null) {
            throw new BusinessRuleException("La cantidad es obligatoria");
        }
        if (cantidad <= 0) {
            throw new BusinessRuleException("La cantidad debe ser mayor a 0");
        }
        item.setCantidad(cantidad);
        item.setSubtotal(item.getPrecio() * cantidad);
        itemCarritoRepository.save(item);
        return refrescar(carrito);
    }

    // CA-CAR-16/17: total = suma de subtotales (0 si está vacío).
    public double calcularTotal(Long carritoId) {
        buscar(carritoId);
        return itemCarritoRepository.findByCarritoId(carritoId).stream()
                .mapToDouble(ItemCarrito::getSubtotal).sum();
    }

    // CA-CAR-21/22: vacía el carrito (idempotente).
    public Carrito vaciar(Long carritoId) {
        Carrito carrito = buscar(carritoId);
        itemCarritoRepository.deleteAll(itemCarritoRepository.findByCarritoId(carritoId));
        return refrescar(carrito);
    }

    public Carrito actualizar(Long id, Carrito datos) {
        Carrito existente = buscar(id);
        existente.setClienteId(datos.getClienteId());
        existente.setTotal(datos.getTotal());
        existente.setActivo(datos.getActivo());
        return carritoRepository.save(existente);
    }

    public void eliminar(Long id) {
        if (!carritoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Carrito no encontrado con id " + id);
        }
        carritoRepository.deleteById(id);
    }

    private Carrito buscar(Long id) {
        return carritoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado con id " + id));
    }

    private Carrito conItems(Carrito carrito) {
        carrito.setItems(itemCarritoRepository.findByCarritoId(carrito.getId()));
        return carrito;
    }

    private Carrito refrescar(Carrito carrito) {
        List<ItemCarrito> items = itemCarritoRepository.findByCarritoId(carrito.getId());
        carrito.setTotal(items.stream().mapToDouble(ItemCarrito::getSubtotal).sum());
        carrito.setItems(items);
        return carritoRepository.save(carrito);
    }
}
