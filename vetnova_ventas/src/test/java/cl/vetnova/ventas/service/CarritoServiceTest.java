package cl.vetnova.ventas.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cl.vetnova.ventas.dto.AgregarItemCarritoRequest;
import cl.vetnova.ventas.dto.CarritoItemResultado;
import cl.vetnova.ventas.exception.BusinessRuleException;
import cl.vetnova.ventas.exception.ConflictException;
import cl.vetnova.ventas.exception.ResourceNotFoundException;
import cl.vetnova.ventas.model.Carrito;
import cl.vetnova.ventas.model.ItemCarrito;
import cl.vetnova.ventas.repository.CarritoRepository;
import cl.vetnova.ventas.repository.ItemCarritoRepository;

public class CarritoServiceTest {

    @Mock
    private CarritoRepository carritoRepository;
    @Mock
    private ItemCarritoRepository itemCarritoRepository;
    @InjectMocks
    private CarritoService carritoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Carrito carrito() {
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        carrito.setClienteId(2L);
        return carrito;
    }

    private ItemCarrito item(Long itemId, double precio, int cantidad) {
        ItemCarrito item = new ItemCarrito();
        item.setCarritoId(1L);
        item.setItemId(itemId);
        item.setPrecio(precio);
        item.setCantidad(cantidad);
        item.setSubtotal(precio * cantidad);
        return item;
    }

    private AgregarItemCarritoRequest itemReq(Long itemId, String tipo, Integer cantidad, Double precio) {
        AgregarItemCarritoRequest request = new AgregarItemCarritoRequest();
        request.setItemId(itemId);
        request.setTipo(tipo);
        request.setCantidad(cantidad);
        request.setPrecio(precio);
        return request;
    }

    private void guardaCarrito() {
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    // ---- crear ----

    @Test
    void testCrearClienteNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> carritoService.crear(new Carrito()));
        assertEquals("El clienteId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearClienteYaTieneCarritoActivo() {
        when(carritoRepository.existsByClienteIdAndActivoTrue(2L)).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> carritoService.crear(carrito()));
        assertEquals("El cliente ya tiene un carrito activo", ex.getMessage());
    }

    @Test
    void testCrearCasoFeliz() {
        when(carritoRepository.existsByClienteIdAndActivoTrue(2L)).thenReturn(false);
        guardaCarrito();
        Carrito creado = carritoService.crear(carrito());
        assertEquals(0.0, creado.getTotal());
        assertEquals(true, creado.getActivo());
    }

    // ---- listar / obtener / actualizar / eliminar ----

    @Test
    void testListar() {
        when(carritoRepository.findAll()).thenReturn(List.of(new Carrito()));
        assertEquals(1, carritoService.listar().size());
    }

    @Test
    void testObtenerPorIdCargaItems() {
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito()));
        when(itemCarritoRepository.findByCarritoId(1L)).thenReturn(List.of(item(10L, 500.0, 2)));
        assertEquals(1, carritoService.obtenerPorId(1L).getItems().size());
    }

    @Test
    void testObtenerPorIdInexistenteLanzaNotFound() {
        when(carritoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> carritoService.obtenerPorId(99L));
    }

    @Test
    void testActualizar() {
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito()));
        guardaCarrito();
        Carrito datos = new Carrito();
        datos.setClienteId(3L);
        assertEquals(3L, carritoService.actualizar(1L, datos).getClienteId());
    }

    @Test
    void testEliminarExistente() {
        when(carritoRepository.existsById(1L)).thenReturn(true);
        carritoService.eliminar(1L);
        verify(carritoRepository).deleteById(1L);
    }

    @Test
    void testEliminarInexistenteLanzaNotFound() {
        when(carritoRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> carritoService.eliminar(99L));
        verify(carritoRepository, never()).deleteById(any());
    }

    // ---- agregarItem (CA-CAR-07/09/10) ----

    @Test
    void testAgregarItemTipoNull() {
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito()));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> carritoService.agregarItem(1L, itemReq(10L, null, 2, 500.0)));
        assertEquals("El tipo de ítem es obligatorio", ex.getMessage());
    }

    @Test
    void testAgregarItemTipoInvalido() {
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito()));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> carritoService.agregarItem(1L, itemReq(10L, "PAQUETE", 2, 500.0)));
        assertEquals("Tipo no válido. Valores permitidos: PRODUCTO, SERVICIO", ex.getMessage());
    }

    @Test
    void testAgregarItemCantidadNull() {
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito()));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> carritoService.agregarItem(1L, itemReq(10L, "PRODUCTO", null, 500.0)));
        assertEquals("La cantidad es obligatoria", ex.getMessage());
    }

    @Test
    void testAgregarItemCantidadNoPositiva() {
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito()));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> carritoService.agregarItem(1L, itemReq(10L, "PRODUCTO", 0, 500.0)));
        assertEquals("La cantidad debe ser mayor a 0", ex.getMessage());
    }

    @Test
    void testAgregarItemNuevo() {
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito()));
        when(itemCarritoRepository.findByCarritoIdAndItemId(1L, 10L)).thenReturn(Optional.empty());
        when(itemCarritoRepository.save(any(ItemCarrito.class))).thenAnswer(inv -> inv.getArgument(0));
        when(itemCarritoRepository.findByCarritoId(1L)).thenReturn(List.of(item(10L, 500.0, 2)));
        guardaCarrito();

        CarritoItemResultado resultado = carritoService.agregarItem(1L, itemReq(10L, "PRODUCTO", 2, 500.0));

        assertTrue(resultado.creado());
        assertEquals(1000.0, resultado.carrito().getTotal());
    }

    @Test
    void testAgregarItemExistenteAcumula() {
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito()));
        ItemCarrito existente = item(10L, 500.0, 2);
        when(itemCarritoRepository.findByCarritoIdAndItemId(1L, 10L)).thenReturn(Optional.of(existente));
        when(itemCarritoRepository.save(any(ItemCarrito.class))).thenAnswer(inv -> inv.getArgument(0));
        when(itemCarritoRepository.findByCarritoId(1L)).thenReturn(List.of(existente));
        guardaCarrito();

        CarritoItemResultado resultado = carritoService.agregarItem(1L, itemReq(10L, "PRODUCTO", 1, 500.0));

        assertFalse(resultado.creado());
        assertEquals(3, existente.getCantidad());
        assertEquals(1500.0, existente.getSubtotal());
    }

    // ---- quitarItem (CA-CAR-11/12) ----

    @Test
    void testQuitarItemNoEstaEnCarrito() {
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito()));
        when(itemCarritoRepository.findByCarritoIdAndItemId(1L, 99L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> carritoService.quitarItem(1L, 99L));
        assertEquals("Ítem no encontrado en el carrito", ex.getMessage());
    }

    @Test
    void testQuitarItemCasoFeliz() {
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito()));
        when(itemCarritoRepository.findByCarritoIdAndItemId(1L, 10L)).thenReturn(Optional.of(item(10L, 500.0, 2)));
        when(itemCarritoRepository.findByCarritoId(1L)).thenReturn(List.of());
        guardaCarrito();
        Carrito resultado = carritoService.quitarItem(1L, 10L);
        assertEquals(0.0, resultado.getTotal());
        verify(itemCarritoRepository).delete(any(ItemCarrito.class));
    }

    // ---- actualizarCantidad (CA-CAR-13/15) ----

    @Test
    void testActualizarCantidadNula() {
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito()));
        when(itemCarritoRepository.findByCarritoIdAndItemId(1L, 10L)).thenReturn(Optional.of(item(10L, 500.0, 1)));
        assertThrows(BusinessRuleException.class, () -> carritoService.actualizarCantidad(1L, 10L, null));
    }

    @Test
    void testActualizarCantidadNoPositiva() {
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito()));
        when(itemCarritoRepository.findByCarritoIdAndItemId(1L, 10L)).thenReturn(Optional.of(item(10L, 500.0, 1)));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> carritoService.actualizarCantidad(1L, 10L, 0));
        assertEquals("La cantidad debe ser mayor a 0", ex.getMessage());
    }

    @Test
    void testActualizarCantidadItemNoEncontrado() {
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito()));
        when(itemCarritoRepository.findByCarritoIdAndItemId(1L, 99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> carritoService.actualizarCantidad(1L, 99L, 3));
    }

    @Test
    void testActualizarCantidadCasoFeliz() {
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito()));
        ItemCarrito existente = item(10L, 500.0, 1);
        when(itemCarritoRepository.findByCarritoIdAndItemId(1L, 10L)).thenReturn(Optional.of(existente));
        when(itemCarritoRepository.save(any(ItemCarrito.class))).thenAnswer(inv -> inv.getArgument(0));
        when(itemCarritoRepository.findByCarritoId(1L)).thenReturn(List.of(existente));
        guardaCarrito();

        Carrito resultado = carritoService.actualizarCantidad(1L, 10L, 3);

        assertEquals(1500.0, existente.getSubtotal());
        assertEquals(1500.0, resultado.getTotal());
    }

    // ---- calcularTotal (CA-CAR-16/17) ----

    @Test
    void testCalcularTotalConItems() {
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito()));
        when(itemCarritoRepository.findByCarritoId(1L)).thenReturn(List.of(item(10L, 1000.0, 1), item(11L, 500.0, 1)));
        assertEquals(1500.0, carritoService.calcularTotal(1L));
    }

    @Test
    void testCalcularTotalCarritoVacio() {
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito()));
        when(itemCarritoRepository.findByCarritoId(1L)).thenReturn(List.of());
        assertEquals(0.0, carritoService.calcularTotal(1L));
    }

    // ---- vaciar (CA-CAR-21/22) ----

    @Test
    void testVaciar() {
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito()));
        when(itemCarritoRepository.findByCarritoId(1L)).thenReturn(List.of());
        guardaCarrito();
        Carrito resultado = carritoService.vaciar(1L);
        assertEquals(0.0, resultado.getTotal());
        verify(itemCarritoRepository).deleteAll(any());
    }
}
