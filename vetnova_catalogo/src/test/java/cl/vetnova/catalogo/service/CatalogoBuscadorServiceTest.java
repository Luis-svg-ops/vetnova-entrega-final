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

    // @Mock: repositorios simulados — CatalogoBuscadorService no llama a ningún MS externo,
    // así que todos los mocks son de BD local (sin AuthClient ni clientes HTTP)
    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ServicioRepository servicioRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    // @InjectMocks: crea CatalogoBuscadorService real con los tres mocks inyectados
    @InjectMocks
    private CatalogoBuscadorService buscadorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Helper: producto activo listo para ser retornado por los mocks
    private Producto productoActivo() {
        Producto p = new Producto();
        p.setActivo(true);
        return p;
    }

    // ═══════════════════════════════════════════════════════
    //  BUSCAR POR NOMBRE
    // ═══════════════════════════════════════════════════════

    /**
     * Regla: el nombre de búsqueda es obligatorio — no tiene sentido buscar sin criterio.
     *
     * GIVEN: nombre = null
     * WHEN:  buscarPorNombre(null)
     * THEN:  BusinessRuleException "El nombre de búsqueda es obligatorio"
     *        (falla antes de consultar la BD)
     */
    @Test
    void testBuscarPorNombreNullLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> buscadorService.buscarPorNombre(null));
        assertEquals("El nombre de búsqueda es obligatorio", ex.getMessage());
    }

    /**
     * Regla: el nombre no puede ser solo espacios en blanco — isBlank() detecta esto.
     * Por qué este caso: "   " pasa el null-check pero falla en isBlank() — son dos validaciones distintas.
     *
     * GIVEN: nombre = "   " (solo espacios)
     * WHEN:  buscarPorNombre("   ")
     * THEN:  BusinessRuleException "El nombre de búsqueda no puede estar vacío"
     */
    @Test
    void testBuscarPorNombreVacioLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> buscadorService.buscarPorNombre("   "));
        assertEquals("El nombre de búsqueda no puede estar vacío", ex.getMessage());
    }

    /**
     * Regla: la búsqueda es parcial e insensible a mayúsculas (ContainingIgnoreCase).
     *        Solo devuelve productos con activo=true.
     *
     * GIVEN: productoRepository.findByActivoTrueAndNombreContainingIgnoreCase("amox") → 1 producto activo
     * WHEN:  buscarPorNombre("amox")
     * THEN:  lista de tamaño 1 — el filtro activo=true está implícito en el query
     */
    @Test
    void testBuscarPorNombreDevuelveActivos() {
        when(productoRepository.findByActivoTrueAndNombreContainingIgnoreCase("amox")).thenReturn(List.of(productoActivo()));
        assertEquals(1, buscadorService.buscarPorNombre("amox").size());
    }

    // ═══════════════════════════════════════════════════════
    //  FILTRAR POR CATEGORÍA
    // ═══════════════════════════════════════════════════════

    /**
     * Regla: antes de filtrar productos, se verifica que la categoría exista en la BD local.
     *        Si no existe, lanza 404 — no tiene sentido filtrar por una categoría fantasma.
     * Por qué este caso: categoriaId=999 no existe → confirma que la validación previa al query funciona.
     *
     * GIVEN: categoriaRepository.existsById(999L) → false
     * WHEN:  filtrarPorCategoria(999L)
     * THEN:  ResourceNotFoundException
     *        (no se llega a llamar a productoRepository — la validación aborta antes)
     */
    @Test
    void testFiltrarPorCategoriaInexistenteLanzaNotFound() {
        when(categoriaRepository.existsById(999L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> buscadorService.filtrarPorCategoria(999L));
    }

    /**
     * Regla: si la categoría existe, se retornan los productos activos de esa categoría.
     *
     * GIVEN: categoriaRepository.existsById(1L) → true
     *        productoRepository.findByCategoriaIdAndActivoTrue(1L) → 1 producto activo
     * WHEN:  filtrarPorCategoria(1L)
     * THEN:  lista de tamaño 1 — solo productos activos de esa categoría
     */
    @Test
    void testFiltrarPorCategoriaDevuelveActivos() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(productoRepository.findByCategoriaIdAndActivoTrue(1L)).thenReturn(List.of(productoActivo()));
        assertEquals(1, buscadorService.filtrarPorCategoria(1L).size());
    }

    // ═══════════════════════════════════════════════════════
    //  FILTRAR POR RANGO DE PRECIO
    // ═══════════════════════════════════════════════════════

    /**
     * Regla: el precio mínimo no puede ser negativo — un precio negativo no tiene sentido en comercio.
     *
     * GIVEN: min = -100.0, max = 5000.0
     * WHEN:  filtrarPorRango(-100.0, 5000.0)
     * THEN:  BusinessRuleException "El precio mínimo no puede ser negativo"
     */
    @Test
    void testFiltrarPorRangoMinNegativoLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> buscadorService.filtrarPorRango(-100.0, 5000.0));
        assertEquals("El precio mínimo no puede ser negativo", ex.getMessage());
    }

    /**
     * Regla: el mínimo no puede ser mayor al máximo — el rango sería vacío o invertido.
     * Por qué este caso: min=10000 > max=5000 es el límite más claro del rango inválido.
     *
     * GIVEN: min = 10000.0, max = 5000.0 (rango invertido)
     * WHEN:  filtrarPorRango(10000.0, 5000.0)
     * THEN:  BusinessRuleException "El precio mínimo no puede ser mayor al máximo"
     */
    @Test
    void testFiltrarPorRangoMinMayorAMaxLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> buscadorService.filtrarPorRango(10000.0, 5000.0));
        assertEquals("El precio mínimo no puede ser mayor al máximo", ex.getMessage());
    }

    /**
     * Regla: con rango válido, se retornan los productos activos dentro del rango de precio.
     *
     * GIVEN: productoRepository.findByActivoTrueAndPrecioBetween(1000.0, 5000.0) → 1 producto
     * WHEN:  filtrarPorRango(1000.0, 5000.0)
     * THEN:  lista de tamaño 1 — el rango y el filtro activo=true se aplican juntos en el query
     */
    @Test
    void testFiltrarPorRangoDevuelveResultados() {
        when(productoRepository.findByActivoTrueAndPrecioBetween(1000.0, 5000.0)).thenReturn(List.of(productoActivo()));
        assertEquals(1, buscadorService.filtrarPorRango(1000.0, 5000.0).size());
    }

    // ═══════════════════════════════════════════════════════
    //  LISTAR DISPONIBLES POR SUCURSAL
    // ═══════════════════════════════════════════════════════

    /**
     * Regla: la sucursal es obligatoria para filtrar productos disponibles.
     *
     * GIVEN: sucursal = null
     * WHEN:  listarDisponibles(null)
     * THEN:  BusinessRuleException "La sucursal es obligatoria"
     */
    @Test
    void testListarDisponiblesSucursalNullLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> buscadorService.listarDisponibles(null));
        assertEquals("La sucursal es obligatoria", ex.getMessage());
    }

    /**
     * Regla: solo se aceptan las sucursales del sistema (CHILLAN, LOS_ANGELES, TALCA, SANTIAGO).
     *        "FANTASMA" no está en el Set → 404.
     * Por qué este caso: confirma que la validación contra el Set fijo rechaza valores arbitrarios.
     *
     * GIVEN: sucursal = "FANTASMA"
     * WHEN:  listarDisponibles("FANTASMA")
     * THEN:  ResourceNotFoundException "Sucursal no encontrada"
     *        (no se llama a productoRepository — la validación aborta antes)
     */
    @Test
    void testListarDisponiblesSucursalInexistenteLanzaNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> buscadorService.listarDisponibles("FANTASMA"));
    }

    /**
     * Regla: con sucursal válida, retorna todos los productos activos (sin filtro de stock por sucursal,
     *        el catálogo es global — el stock por ubicación lo maneja inventario).
     *
     * GIVEN: productoRepository.findByActivoTrue() → 1 producto activo
     * WHEN:  listarDisponibles("SANTIAGO")
     * THEN:  lista de tamaño 1
     */
    @Test
    void testListarDisponiblesDevuelveActivos() {
        when(productoRepository.findByActivoTrue()).thenReturn(List.of(productoActivo()));
        assertEquals(1, buscadorService.listarDisponibles("SANTIAGO").size());
    }

    // ═══════════════════════════════════════════════════════
    //  GET DETALLE DE ÍTEM
    // ═══════════════════════════════════════════════════════

    /**
     * Regla: el tipo solo puede ser "producto" o "servicio" — cualquier otro valor es error del cliente.
     *
     * GIVEN: tipo = "inventado"
     * WHEN:  getDetalle(1L, "inventado")
     * THEN:  BusinessRuleException "Tipo no válido. Valores permitidos: producto, servicio"
     */
    @Test
    void testGetDetalleTipoInvalidoLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> buscadorService.getDetalle(1L, "inventado"));
        assertEquals("Tipo no válido. Valores permitidos: producto, servicio", ex.getMessage());
    }

    /**
     * Regla: un producto activo se puede consultar con getDetalle.
     *
     * GIVEN: productoRepository.findById(1L) → producto activo
     * WHEN:  getDetalle(1L, "producto")
     * THEN:  objeto no nulo retornado
     */
    @Test
    void testGetDetalleProductoActivo() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoActivo()));
        assertNotNull(buscadorService.getDetalle(1L, "producto"));
    }

    /**
     * Regla: un producto inactivo se trata como si no existiera — no se expone al cliente.
     * Por qué este caso: soft delete significa que el registro está en BD pero activo=false;
     *        getDetalle debe comportarse igual que si no existiera (404).
     *
     * GIVEN: productoRepository.findById(1L) → producto con activo=false
     * WHEN:  getDetalle(1L, "producto")
     * THEN:  ResourceNotFoundException — el cliente recibe 404, no 200 con datos de un producto desactivado
     */
    @Test
    void testGetDetalleProductoInactivoLanzaNotFound() {
        Producto p = new Producto();
        p.setActivo(false);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(p));
        assertThrows(ResourceNotFoundException.class, () -> buscadorService.getDetalle(1L, "producto"));
    }

    /**
     * Regla: un servicio activo también se puede consultar.
     *
     * GIVEN: servicioRepository.findById(5L) → servicio con activo=true
     * WHEN:  getDetalle(5L, "servicio")
     * THEN:  objeto no nulo retornado
     */
    @Test
    void testGetDetalleServicioActivo() {
        Servicio s = new Servicio();
        s.setActivo(true);
        when(servicioRepository.findById(5L)).thenReturn(Optional.of(s));
        assertNotNull(buscadorService.getDetalle(5L, "servicio"));
    }

    /**
     * Regla: un servicio inexistente en BD lanza 404.
     *
     * GIVEN: servicioRepository.findById(99L) → Optional.empty()
     * WHEN:  getDetalle(99L, "servicio")
     * THEN:  ResourceNotFoundException
     */
    @Test
    void testGetDetalleServicioInexistenteLanzaNotFound() {
        when(servicioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> buscadorService.getDetalle(99L, "servicio"));
    }

    /**
     * Regla: un servicio inactivo también se trata como si no existiera (mismo comportamiento que producto).
     *
     * GIVEN: servicioRepository.findById(1L) → servicio con activo=false
     * WHEN:  getDetalle(1L, "servicio")
     * THEN:  ResourceNotFoundException
     */
    @Test
    void testGetDetalleServicioInactivoLanzaNotFound() {
        Servicio s = new Servicio();
        s.setActivo(false);
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(s));
        assertThrows(ResourceNotFoundException.class, () -> buscadorService.getDetalle(1L, "servicio"));
    }

    /**
     * Regla: un producto inexistente en BD lanza 404.
     *
     * GIVEN: productoRepository.findById(99L) → Optional.empty()
     * WHEN:  getDetalle(99L, "producto")
     * THEN:  ResourceNotFoundException
     */
    @Test
    void testGetDetalleProductoInexistenteLanzaNotFound() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> buscadorService.getDetalle(99L, "producto"));
    }
}
