package cl.vetnova.inventario.service;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.vetnova.inventario.exception.BusinessRuleException;
import cl.vetnova.inventario.exception.ConflictException;
import cl.vetnova.inventario.exception.ResourceNotFoundException;
import cl.vetnova.inventario.model.Proveedor;
import cl.vetnova.inventario.model.ProveedorProducto;
import cl.vetnova.inventario.repository.PedidoProveedorRepository;
import cl.vetnova.inventario.repository.ProductoRepository;
import cl.vetnova.inventario.repository.ProveedorProductoRepository;
import cl.vetnova.inventario.repository.ProveedorRepository;
import cl.vetnova.inventario.util.RutValidator;

@Service
public class ProveedorService {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern TELEFONO = Pattern.compile("^\\+?[0-9\\s().-]{6,20}$");
    private static final List<String> ESTADOS_ACTIVOS = List.of("pendiente", "enviado");

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ProveedorProductoRepository proveedorProductoRepository;

    @Autowired
    private PedidoProveedorRepository pedidoProveedorRepository;

    public List<Proveedor> listar() {
        return proveedorRepository.findAll();
    }

    public Proveedor obtenerPorId(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con id " + id));
    }

    public Proveedor crear(Proveedor proveedor) {
        if (proveedor.getNombre() == null) {
            throw new BusinessRuleException("El nombre es obligatorio");
        }
        if (proveedor.getNombre().isBlank()) {
            throw new BusinessRuleException("El nombre no puede estar vacío");
        }
        if (proveedorRepository.existsByNombre(proveedor.getNombre())) {
            throw new ConflictException("Ya existe un proveedor con ese nombre");
        }
        if (proveedor.getRut() == null) {
            throw new BusinessRuleException("El RUT es obligatorio");
        }
        if (!RutValidator.esValido(proveedor.getRut())) {
            throw new BusinessRuleException("RUT inválido o dígito verificador incorrecto");
        }
        if (proveedorRepository.existsByRut(proveedor.getRut())) {
            throw new ConflictException("El RUT ya está registrado");
        }
        if (proveedor.getEmail() == null) {
            throw new BusinessRuleException("El email es obligatorio");
        }
        if (!EMAIL.matcher(proveedor.getEmail()).matches()) {
            throw new BusinessRuleException("El email no tiene un formato válido");
        }
        if (proveedorRepository.existsByEmail(proveedor.getEmail())) {
            throw new ConflictException("El email ya está registrado");
        }
        if (proveedor.getTelefono() != null && !TELEFONO.matcher(proveedor.getTelefono()).matches()) {
            throw new BusinessRuleException("El formato de teléfono no es válido");
        }
        proveedor.setActivo(true);
        return proveedorRepository.save(proveedor);
    }

    public ProveedorProducto asociarProducto(Long proveedorId, Long productoId) {
        obtenerPorId(proveedorId);
        if (!productoRepository.existsById(productoId)) {
            throw new ResourceNotFoundException("Producto no encontrado");
        }
        if (proveedorProductoRepository.existsByProveedorIdAndProductoId(proveedorId, productoId)) {
            throw new ConflictException("El producto ya está asociado");
        }
        ProveedorProducto relacion = new ProveedorProducto();
        relacion.setProveedorId(proveedorId);
        relacion.setProductoId(productoId);
        return proveedorProductoRepository.save(relacion);
    }

    public Proveedor desactivar(Long id) {
        Proveedor proveedor = obtenerPorId(id);
        if (pedidoProveedorRepository.existsByProveedorIdAndEstadoIn(id, ESTADOS_ACTIVOS)) {
            throw new BusinessRuleException("No se puede desactivar con pedidos activos");
        }
        proveedor.setActivo(false);
        return proveedorRepository.save(proveedor);
    }

    public Proveedor actualizar(Long id, Proveedor datos) {
        Proveedor existente = obtenerPorId(id);
        existente.setRut(datos.getRut());
        existente.setNombre(datos.getNombre());
        existente.setContacto(datos.getContacto());
        existente.setTelefono(datos.getTelefono());
        existente.setEmail(datos.getEmail());
        existente.setDireccion(datos.getDireccion());
        existente.setActivo(datos.getActivo());
        return proveedorRepository.save(existente);
    }
}
