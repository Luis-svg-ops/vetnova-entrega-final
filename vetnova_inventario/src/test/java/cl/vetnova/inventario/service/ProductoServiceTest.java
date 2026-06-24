package cl.vetnova.inventario.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cl.vetnova.inventario.dto.ProductoRequest;
import cl.vetnova.inventario.dto.ProductoResponse;
import cl.vetnova.inventario.exception.BusinessRuleException;
import cl.vetnova.inventario.exception.ResourceNotFoundException;
import cl.vetnova.inventario.model.Producto;
import cl.vetnova.inventario.repository.ProductoRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private ProductoRequest request() {
        ProductoRequest r = new ProductoRequest();
        r.setSku("ALI-001");
        r.setNombre("Alimento perro adulto 15kg");
        r.setPrecio(35990.0);
        return r;
    }

    @Test
    void testCrearProductoGuardaYDevuelveDatos() {
        when(productoRepository.existsBySku("ALI-001")).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductoResponse response = productoService.crear(request());

        assertEquals("ALI-001", response.getSku());
        assertTrue(response.getActivo());
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void testCrearConSkuDuplicadoLanzaExcepcion() {
        when(productoRepository.existsBySku("ALI-001")).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> productoService.crear(request()));
        verify(productoRepository, never()).save(any());
    }

    @Test
    void testObtenerPorIdInexistenteLanzaNotFound() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productoService.obtenerPorId(99L));
    }

    @Test
    void testActualizarConSkuDeOtroProductoLanzaExcepcion() {
        Producto existente = new Producto();
        existente.setId(1L);
        existente.setSku("MED-001");
        Producto otro = new Producto();
        otro.setId(2L);
        otro.setSku("ALI-001");

        when(productoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(productoRepository.findBySku("ALI-001")).thenReturn(Optional.of(otro));

        assertThrows(BusinessRuleException.class, () -> productoService.actualizar(1L, request()));
    }

    @Test
    void testDesactivarDejaElProductoInactivo() {
        Producto existente = new Producto();
        existente.setId(1L);
        existente.setSku("ALI-001");
        when(productoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        productoService.desactivar(1L);

        assertFalse(existente.getActivo());
        verify(productoRepository).save(existente);
    }
}
