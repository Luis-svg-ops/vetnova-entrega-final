package cl.vetnova.inventario.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cl.vetnova.inventario.exception.BusinessRuleException;
import cl.vetnova.inventario.exception.ConflictException;
import cl.vetnova.inventario.exception.ResourceNotFoundException;
import cl.vetnova.inventario.model.Proveedor;
import cl.vetnova.inventario.model.ProveedorProducto;
import cl.vetnova.inventario.repository.PedidoProveedorRepository;
import cl.vetnova.inventario.repository.ProductoRepository;
import cl.vetnova.inventario.repository.ProveedorProductoRepository;
import cl.vetnova.inventario.repository.ProveedorRepository;

public class ProveedorServiceTest {

    @Mock
    private ProveedorRepository proveedorRepository;
    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private ProveedorProductoRepository proveedorProductoRepository;
    @Mock
    private PedidoProveedorRepository pedidoProveedorRepository;
    @InjectMocks
    private ProveedorService proveedorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Proveedor proveedor(String nombre, String rut, String email, String telefono) {
        Proveedor p = new Proveedor();
        p.setNombre(nombre);
        p.setRut(rut);
        p.setEmail(email);
        p.setTelefono(telefono);
        return p;
    }

    private Proveedor valido() {
        return proveedor("Lab Drag", "11.111.111-1", "prov@mail.com", null);
    }

    // ---- CRUD ----

    @Test
    void testListar() {
        when(proveedorRepository.findAll()).thenReturn(List.of(new Proveedor()));
        assertEquals(1, proveedorService.listar().size());
    }

    @Test
    void testObtenerPorIdExistente() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(new Proveedor()));
        assertNotNull(proveedorService.obtenerPorId(1L));
    }

    @Test
    void testObtenerPorIdInexistenteLanzaNotFound() {
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> proveedorService.obtenerPorId(99L));
    }

    @Test
    void testActualizarExistente() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(new Proveedor()));
        when(proveedorRepository.save(any(Proveedor.class))).thenAnswer(inv -> inv.getArgument(0));
        assertNotNull(proveedorService.actualizar(1L, valido()));
    }

    @Test
    void testActualizarInexistenteLanzaNotFound() {
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> proveedorService.actualizar(99L, new Proveedor()));
    }

    // ---- crear (CA-PRV-01..13) ----

    @Test
    void testCrearNombreNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> proveedorService.crear(proveedor(null, "11.111.111-1", "prov@mail.com", null)));
        assertEquals("El nombre es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearNombreVacio() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> proveedorService.crear(proveedor("   ", "11.111.111-1", "prov@mail.com", null)));
        assertEquals("El nombre no puede estar vacío", ex.getMessage());
    }

    @Test
    void testCrearNombreDuplicado() {
        when(proveedorRepository.existsByNombre("Lab Drag")).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> proveedorService.crear(valido()));
        assertEquals("Ya existe un proveedor con ese nombre", ex.getMessage());
    }

    @Test
    void testCrearRutNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> proveedorService.crear(proveedor("Lab Drag", null, "prov@mail.com", null)));
        assertEquals("El RUT es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearRutInvalido() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> proveedorService.crear(proveedor("Lab Drag", "99999999-0", "prov@mail.com", null)));
        assertEquals("RUT inválido o dígito verificador incorrecto", ex.getMessage());
    }

    @Test
    void testCrearRutDuplicado() {
        when(proveedorRepository.existsByRut("11.111.111-1")).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> proveedorService.crear(valido()));
        assertEquals("El RUT ya está registrado", ex.getMessage());
    }

    @Test
    void testCrearEmailNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> proveedorService.crear(proveedor("Lab Drag", "11.111.111-1", null, null)));
        assertEquals("El email es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearEmailInvalido() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> proveedorService.crear(proveedor("Lab Drag", "11.111.111-1", "noesunmail", null)));
        assertEquals("El email no tiene un formato válido", ex.getMessage());
    }

    @Test
    void testCrearEmailDuplicado() {
        when(proveedorRepository.existsByEmail("prov@mail.com")).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> proveedorService.crear(valido()));
        assertEquals("El email ya está registrado", ex.getMessage());
    }

    @Test
    void testCrearTelefonoInvalido() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> proveedorService.crear(proveedor("Lab Drag", "11.111.111-1", "prov@mail.com", "abc123")));
        assertEquals("El formato de teléfono no es válido", ex.getMessage());
    }

    @Test
    void testCrearCasoFelizSinTelefono() {
        when(proveedorRepository.save(any(Proveedor.class))).thenAnswer(inv -> inv.getArgument(0));
        Proveedor creado = proveedorService.crear(valido());
        assertEquals(true, creado.getActivo());
    }

    @Test
    void testCrearCasoFelizConTelefonoValido() {
        when(proveedorRepository.save(any(Proveedor.class))).thenAnswer(inv -> inv.getArgument(0));
        Proveedor creado = proveedorService.crear(proveedor("Lab Drag", "11.111.111-1", "prov@mail.com", "+56912345678"));
        assertEquals(true, creado.getActivo());
    }

    // ---- asociarProducto (CA-PRV-14..16) ----

    @Test
    void testAsociarProductoProveedorInexistente() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> proveedorService.asociarProducto(1L, 1L));
    }

    @Test
    void testAsociarProductoInexistente() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(new Proveedor()));
        when(productoRepository.existsById(999L)).thenReturn(false);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> proveedorService.asociarProducto(1L, 999L));
        assertEquals("Producto no encontrado", ex.getMessage());
    }

    @Test
    void testAsociarProductoYaAsociado() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(new Proveedor()));
        when(productoRepository.existsById(1L)).thenReturn(true);
        when(proveedorProductoRepository.existsByProveedorIdAndProductoId(1L, 1L)).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> proveedorService.asociarProducto(1L, 1L));
        assertEquals("El producto ya está asociado", ex.getMessage());
    }

    @Test
    void testAsociarProductoCasoFeliz() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(new Proveedor()));
        when(productoRepository.existsById(1L)).thenReturn(true);
        when(proveedorProductoRepository.existsByProveedorIdAndProductoId(1L, 1L)).thenReturn(false);
        when(proveedorProductoRepository.save(any(ProveedorProducto.class))).thenAnswer(inv -> inv.getArgument(0));
        ProveedorProducto rel = proveedorService.asociarProducto(1L, 1L);
        assertEquals(1L, rel.getProveedorId());
        assertEquals(1L, rel.getProductoId());
    }

    // ---- desactivar (CA-PRV-17..18) ----

    @Test
    void testDesactivarConPedidosActivos() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(new Proveedor()));
        when(pedidoProveedorRepository.existsByProveedorIdAndEstadoIn(anyLong(), anyCollection())).thenReturn(true);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> proveedorService.desactivar(1L));
        assertEquals("No se puede desactivar con pedidos activos", ex.getMessage());
    }

    @Test
    void testDesactivarSinPedidosActivos() {
        Proveedor p = new Proveedor();
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(p));
        when(pedidoProveedorRepository.existsByProveedorIdAndEstadoIn(anyLong(), anyCollection())).thenReturn(false);
        when(proveedorRepository.save(any(Proveedor.class))).thenAnswer(inv -> inv.getArgument(0));
        Proveedor desactivado = proveedorService.desactivar(1L);
        assertEquals(false, desactivado.getActivo());
    }
}
