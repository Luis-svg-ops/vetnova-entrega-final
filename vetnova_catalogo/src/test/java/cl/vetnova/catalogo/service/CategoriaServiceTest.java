package cl.vetnova.catalogo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cl.vetnova.catalogo.exception.BusinessRuleException;
import cl.vetnova.catalogo.exception.ConflictException;
import cl.vetnova.catalogo.exception.ResourceNotFoundException;
import cl.vetnova.catalogo.model.Categoria;
import cl.vetnova.catalogo.repository.CategoriaRepository;
import cl.vetnova.catalogo.repository.ProductoRepository;
import cl.vetnova.catalogo.repository.ServicioRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private ServicioRepository servicioRepository;
    @InjectMocks
    private CategoriaService categoriaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Categoria categoria(String nombre, String tipo) {
        Categoria c = new Categoria();
        c.setNombre(nombre);
        c.setTipo(tipo);
        return c;
    }

    @Test
    void testCrearNombreNullLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> categoriaService.crear(categoria(null, "producto")));
        assertEquals("El nombre es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearNombreVacioLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> categoriaService.crear(categoria("   ", "producto")));
        assertEquals("El nombre no puede estar vacío", ex.getMessage());
    }

    @Test
    void testCrearNombreDuplicadoLanzaConflict() {
        when(categoriaRepository.existsByNombreIgnoreCase("Medicamentos")).thenReturn(true);
        assertThrows(ConflictException.class, () -> categoriaService.crear(categoria("Medicamentos", "producto")));
    }

    @Test
    void testCrearTipoNullLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> categoriaService.crear(categoria("Medicamentos", null)));
        assertEquals("El tipo es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearTipoInvalidoLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> categoriaService.crear(categoria("Medicamentos", "inventado")));
        assertEquals("Tipo no válido. Valores permitidos: producto, servicio", ex.getMessage());
    }

    @Test
    void testCrearTipoProductoCasoFeliz() {
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(inv -> inv.getArgument(0));
        assertEquals("Medicamentos", categoriaService.crear(categoria("Medicamentos", "producto")).getNombre());
    }

    @Test
    void testCrearTipoServicioCasoFeliz() {
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(inv -> inv.getArgument(0));
        assertEquals("servicio", categoriaService.crear(categoria("Consultas", "servicio")).getTipo());
    }

    @Test
    void testListarDevuelveLasCategorias() {
        when(categoriaRepository.findAll()).thenReturn(List.of(new Categoria()));
        assertEquals(1, categoriaService.listar().size());
    }

    @Test
    void testEliminarCategoriaInexistenteLanzaNotFound() {
        when(categoriaRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> categoriaService.eliminar(99L));
        verify(categoriaRepository, never()).deleteById(any());
    }

    @Test
    void testEliminarCategoriaConProductosLanzaBusinessRule() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(productoRepository.existsByCategoriaId(1L)).thenReturn(true);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> categoriaService.eliminar(1L));
        assertEquals("No se puede eliminar una categoría con items. Reasigne primero", ex.getMessage());
        verify(categoriaRepository, never()).deleteById(any());
    }

    @Test
    void testEliminarCategoriaConServiciosLanzaBusinessRule() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(productoRepository.existsByCategoriaId(1L)).thenReturn(false);
        when(servicioRepository.existsByCategoriaId(1L)).thenReturn(true);
        assertThrows(BusinessRuleException.class, () -> categoriaService.eliminar(1L));
    }

    @Test
    void testEliminarCategoriaSinItemsBorra() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(productoRepository.existsByCategoriaId(1L)).thenReturn(false);
        when(servicioRepository.existsByCategoriaId(1L)).thenReturn(false);
        categoriaService.eliminar(1L);
        verify(categoriaRepository).deleteById(1L);
    }
}
