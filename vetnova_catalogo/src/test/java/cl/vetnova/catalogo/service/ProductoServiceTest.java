package cl.vetnova.catalogo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cl.vetnova.catalogo.dto.ProductoRequest;
import cl.vetnova.catalogo.dto.ProductoResponse;
import cl.vetnova.catalogo.exception.BusinessRuleException;
import cl.vetnova.catalogo.exception.ConflictException;
import cl.vetnova.catalogo.exception.ResourceNotFoundException;
import cl.vetnova.catalogo.model.Producto;
import cl.vetnova.catalogo.repository.CategoriaRepository;
import cl.vetnova.catalogo.repository.ProductoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @InjectMocks
    private ProductoService productoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Producto producto() {
        Producto p = new Producto();
        p.setId(1L);
        p.setNombre("Alimento perro adulto 15kg");
        p.setPrecio(35990.0);
        p.setActivo(true);
        p.setFechaActualizacion(LocalDate.now());
        return p;
    }

    private ProductoRequest request(String nombre, Double precio, Long categoriaId, String imagenUrl) {
        ProductoRequest r = new ProductoRequest();
        r.setNombre(nombre);
        r.setPrecio(precio);
        r.setCategoriaId(categoriaId);
        r.setImagenUrl(imagenUrl);
        return r;
    }

    private ProductoRequest valido() {
        return request("Amoxicilina 500mg", 5000.0, 1L, null);
    }

    @Test
    void testCrearNombreNullLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> productoService.crear(request(null, 5000.0, 1L, null)));
        assertEquals("El nombre es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearNombreVacioLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> productoService.crear(request("   ", 5000.0, 1L, null)));
        assertEquals("El nombre no puede estar vacío", ex.getMessage());
    }

    @Test
    void testCrearNombreDuplicadoLanzaConflict() {
        when(productoRepository.existsByNombreIgnoreCase("Amoxicilina 500mg")).thenReturn(true);
        assertThrows(ConflictException.class, () -> productoService.crear(valido()));
    }

    @Test
    void testCrearPrecioNullLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> productoService.crear(request("Amoxicilina", null, 1L, null)));
        assertEquals("El precio es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearPrecioCeroLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> productoService.crear(request("Amoxicilina", 0.0, 1L, null)));
        assertEquals("El precio debe ser mayor a 0", ex.getMessage());
    }

    @Test
    void testCrearCategoriaNullLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> productoService.crear(request("Amoxicilina", 5000.0, null, null)));
        assertEquals("La categoría es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearCategoriaInexistenteLanzaNotFound() {
        when(categoriaRepository.existsById(999L)).thenReturn(false);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> productoService.crear(request("Amoxicilina", 5000.0, 999L, null)));
        assertEquals("Categoría no encontrada", ex.getMessage());
    }

    @Test
    void testCrearImagenUrlInvalidaLanzaBusinessRule() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> productoService.crear(request("Amoxicilina", 5000.0, 1L, "no-es-una-url")));
        assertEquals("El formato de la URL de imagen no es válido", ex.getMessage());
    }

    @Test
    void testCrearProductoCasoFelizLoDejaActivoConFecha() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductoResponse response = productoService.crear(request("Amoxicilina 500mg", 5000.0, 1L, "https://cdn.vetnova.cl/amox.png"));

        assertTrue(response.getActivo());
        assertEquals("Amoxicilina 500mg", response.getNombre());
        assertNotNull(response.getFechaActualizacion());
    }

    @Test
    void testCrearProductoSinImagenUrlFunciona() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductoResponse response = productoService.crear(valido());

        assertTrue(response.getActivo());
        assertNull(response.getImagenUrl());
    }

    @Test
    void testListarDevuelveLosProductos() {
        when(productoRepository.findAll()).thenReturn(List.of(producto()));
        assertEquals(1, productoService.listar().size());
    }

    @Test
    void testObtenerPorIdInexistenteLanzaNotFound() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productoService.obtenerPorId(99L));
    }

    @Test
    void testObtenerPorIdExistenteDevuelveElProducto() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto()));
        assertEquals("Alimento perro adulto 15kg", productoService.obtenerPorId(1L).getNombre());
    }

    @Test
    void testDesactivarYActivarCambianElEstado() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto()));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        assertFalse(productoService.desactivar(1L).getActivo());
        assertTrue(productoService.activar(1L).getActivo());
    }

    @Test
    void testActualizarPrecioGuardaElNuevoValor() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto()));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        assertEquals(39990.0, productoService.actualizarPrecio(1L, 39990.0).getPrecio());
    }

    @Test
    void testActualizarPrecioInvalidoLanzaBusinessRule() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto()));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> productoService.actualizarPrecio(1L, 0.0));
        assertEquals("El precio debe ser mayor a 0", ex.getMessage());
    }

    @Test
    void testEliminarProductoInexistenteLanzaNotFound() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productoService.eliminar(99L));
        verify(productoRepository, never()).deleteById(any());
    }

    @Test
    void testEliminarProductoExistenteBorraEnElRepositorio() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto()));
        productoService.eliminar(1L);
        verify(productoRepository).deleteById(1L);
    }
}
