package cl.vetnova.inventario.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cl.vetnova.inventario.dto.ProductoRequest;
import cl.vetnova.inventario.exception.BusinessRuleException;
import cl.vetnova.inventario.exception.ResourceNotFoundException;
import cl.vetnova.inventario.model.Producto;
import cl.vetnova.inventario.repository.ProductoRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ProductoServiceCoberturaTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private ProductoRequest request() {
        ProductoRequest request = new ProductoRequest();
        request.setSku("ALM-001");
        request.setNombre("Alimento premium");
        request.setPrecio(15990.0);
        return request;
    }

    @Test
    void testCrearProductoGuardaLosDatos() {
        when(productoRepository.existsBySku("ALM-001")).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        assertEquals("ALM-001", productoService.crear(request()).getSku());
    }

    @Test
    void testCrearProductoConSkuDuplicadoLanzaBusinessRule() {
        when(productoRepository.existsBySku("ALM-001")).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> productoService.crear(request()));
        verify(productoRepository, never()).save(any());
    }

    @Test
    void testListarYObtenerProducto() {
        Producto producto = new Producto();
        producto.setSku("ALM-001");
        when(productoRepository.findAll()).thenReturn(List.of(producto));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        assertEquals(1, productoService.listar().size());
        assertEquals("ALM-001", productoService.obtenerPorId(1L).getSku());
    }

    @Test
    void testObtenerProductoInexistenteLanzaNotFound() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productoService.obtenerPorId(99L));
    }

    @Test
    void testActualizarProductoCambiaLosDatos() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(new Producto()));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        assertEquals("Alimento premium", productoService.actualizar(1L, request()).getNombre());
    }

    @Test
    void testDesactivarProductoLoDejaInactivo() {
        Producto producto = new Producto();
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        productoService.desactivar(1L);

        assertFalse(producto.getActivo());
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void testActualizarConSkuDeOtroProductoLanzaBusinessRule() {
        Producto producto = new Producto();
        producto.setId(1L);
        Producto otro = new Producto();
        otro.setId(2L);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.findBySku("ALM-001")).thenReturn(Optional.of(otro));

        assertThrows(BusinessRuleException.class, () -> productoService.actualizar(1L, request()));
        verify(productoRepository, never()).save(any());
    }

    @Test
    void testActualizarConservandoSuPropioSkuFunciona() {
        Producto producto = new Producto();
        producto.setId(1L);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.findBySku("ALM-001")).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        assertEquals("Alimento premium", productoService.actualizar(1L, request()).getNombre());
    }

    @Test
    void testObtenerProductoConStockMapeaLasSucursales() {
        Producto producto = new Producto();
        cl.vetnova.inventario.model.StockSucursal stock = new cl.vetnova.inventario.model.StockSucursal();
        stock.setIdSucursal(1L);
        stock.setCantidad(2);
        stock.setStockMinimo(5);
        producto.getStockSucursales().add(stock);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        assertEquals(1, productoService.obtenerPorId(1L).getStock().size());
    }

    @Test
    void testObtenerProductoMarcaStockCriticoYNoCritico() {
        Producto producto = new Producto();
        cl.vetnova.inventario.model.StockSucursal bajo = new cl.vetnova.inventario.model.StockSucursal();
        bajo.setIdSucursal(1L);
        bajo.setCantidad(2);
        bajo.setStockMinimo(5);
        cl.vetnova.inventario.model.StockSucursal holgado = new cl.vetnova.inventario.model.StockSucursal();
        holgado.setIdSucursal(2L);
        holgado.setCantidad(50);
        holgado.setStockMinimo(5);
        producto.getStockSucursales().add(bajo);
        producto.getStockSucursales().add(holgado);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        var stock = productoService.obtenerPorId(1L).getStock();

        assertTrue(stock.get(0).isCritico());
        assertFalse(stock.get(1).isCritico());
    }
}
