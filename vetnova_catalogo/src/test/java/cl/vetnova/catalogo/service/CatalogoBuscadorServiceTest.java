package cl.vetnova.catalogo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import cl.vetnova.catalogo.exception.BusinessRuleException;
import cl.vetnova.catalogo.exception.ResourceNotFoundException;
import cl.vetnova.catalogo.model.Producto;
import cl.vetnova.catalogo.model.Servicio;
import cl.vetnova.catalogo.repository.CategoriaRepository;
import cl.vetnova.catalogo.repository.ProductoRepository;
import cl.vetnova.catalogo.repository.ServicioRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CatalogoBuscadorServiceTest {

    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private ServicioRepository servicioRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @InjectMocks
    private CatalogoBuscadorService buscadorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Producto productoActivo() {
        Producto p = new Producto();
        p.setActivo(true);
        return p;
    }

    // ----- buscarPorNombre -----

    @Test
    void testBuscarPorNombreNullLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> buscadorService.buscarPorNombre(null));
        assertEquals("El nombre de búsqueda es obligatorio", ex.getMessage());
    }

    @Test
    void testBuscarPorNombreVacioLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> buscadorService.buscarPorNombre("   "));
        assertEquals("El nombre de búsqueda no puede estar vacío", ex.getMessage());
    }

    @Test
    void testBuscarPorNombreDevuelveActivos() {
        when(productoRepository.findByActivoTrueAndNombreContainingIgnoreCase("amox")).thenReturn(List.of(productoActivo()));
        assertEquals(1, buscadorService.buscarPorNombre("amox").size());
    }

    // ----- filtrarPorCategoria -----

    @Test
    void testFiltrarPorCategoriaInexistenteLanzaNotFound() {
        when(categoriaRepository.existsById(999L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> buscadorService.filtrarPorCategoria(999L));
    }

    @Test
    void testFiltrarPorCategoriaDevuelveActivos() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(productoRepository.findByCategoriaIdAndActivoTrue(1L)).thenReturn(List.of(productoActivo()));
        assertEquals(1, buscadorService.filtrarPorCategoria(1L).size());
    }

    // ----- filtrarPorRango -----

    @Test
    void testFiltrarPorRangoMinNegativoLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> buscadorService.filtrarPorRango(-100.0, 5000.0));
        assertEquals("El precio mínimo no puede ser negativo", ex.getMessage());
    }

    @Test
    void testFiltrarPorRangoMinMayorAMaxLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> buscadorService.filtrarPorRango(10000.0, 5000.0));
        assertEquals("El precio mínimo no puede ser mayor al máximo", ex.getMessage());
    }

    @Test
    void testFiltrarPorRangoDevuelveResultados() {
        when(productoRepository.findByActivoTrueAndPrecioBetween(1000.0, 5000.0)).thenReturn(List.of(productoActivo()));
        assertEquals(1, buscadorService.filtrarPorRango(1000.0, 5000.0).size());
    }

    // ----- listarDisponibles -----

    @Test
    void testListarDisponiblesSucursalNullLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> buscadorService.listarDisponibles(null));
        assertEquals("La sucursal es obligatoria", ex.getMessage());
    }

    @Test
    void testListarDisponiblesSucursalInexistenteLanzaNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> buscadorService.listarDisponibles("FANTASMA"));
    }

    @Test
    void testListarDisponiblesDevuelveActivos() {
        when(productoRepository.findByActivoTrue()).thenReturn(List.of(productoActivo()));
        assertEquals(1, buscadorService.listarDisponibles("SANTIAGO").size());
    }

    // ----- getDetalle -----

    @Test
    void testGetDetalleTipoInvalidoLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> buscadorService.getDetalle(1L, "inventado"));
        assertEquals("Tipo no válido. Valores permitidos: producto, servicio", ex.getMessage());
    }

    @Test
    void testGetDetalleProductoActivo() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoActivo()));
        assertNotNull(buscadorService.getDetalle(1L, "producto"));
    }

    @Test
    void testGetDetalleProductoInactivoLanzaNotFound() {
        Producto p = new Producto();
        p.setActivo(false);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(p));
        assertThrows(ResourceNotFoundException.class, () -> buscadorService.getDetalle(1L, "producto"));
    }

    @Test
    void testGetDetalleServicioActivo() {
        Servicio s = new Servicio();
        s.setActivo(true);
        when(servicioRepository.findById(5L)).thenReturn(Optional.of(s));
        assertNotNull(buscadorService.getDetalle(5L, "servicio"));
    }

    @Test
    void testGetDetalleServicioInexistenteLanzaNotFound() {
        when(servicioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> buscadorService.getDetalle(99L, "servicio"));
    }
}
