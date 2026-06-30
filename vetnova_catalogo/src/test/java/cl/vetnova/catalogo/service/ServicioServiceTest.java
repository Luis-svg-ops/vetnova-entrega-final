package cl.vetnova.catalogo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cl.vetnova.catalogo.exception.BusinessRuleException;
import cl.vetnova.catalogo.exception.ConflictException;
import cl.vetnova.catalogo.exception.ResourceNotFoundException;
import cl.vetnova.catalogo.model.Servicio;
import cl.vetnova.catalogo.repository.CategoriaRepository;
import cl.vetnova.catalogo.repository.ServicioRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ServicioServiceTest {

    @Mock
    private ServicioRepository servicioRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @InjectMocks
    private ServicioService servicioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Servicio servicio(String nombre, Double precio, Integer duracion, Long categoriaId) {
        Servicio s = new Servicio();
        s.setNombre(nombre);
        s.setPrecio(precio);
        s.setDuracionMinutos(duracion);
        s.setCategoriaId(categoriaId);
        return s;
    }

    private Servicio valido() {
        return servicio("Consulta General", 15000.0, 30, 1L);
    }

    @Test
    void testCrearNombreNullLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> servicioService.crear(servicio(null, 15000.0, 30, 1L)));
        assertEquals("El nombre es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearNombreVacioLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> servicioService.crear(servicio("   ", 15000.0, 30, 1L)));
        assertEquals("El nombre no puede estar vacío", ex.getMessage());
    }

    @Test
    void testCrearNombreDuplicadoLanzaConflict() {
        when(servicioRepository.existsByNombreIgnoreCase("Consulta General")).thenReturn(true);
        assertThrows(ConflictException.class, () -> servicioService.crear(valido()));
    }

    @Test
    void testCrearPrecioNullLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> servicioService.crear(servicio("Consulta", null, 30, 1L)));
        assertEquals("El precio es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearPrecioCeroLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> servicioService.crear(servicio("Consulta", 0.0, 30, 1L)));
        assertEquals("El precio debe ser mayor a 0", ex.getMessage());
    }

    @Test
    void testCrearDuracionNullLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> servicioService.crear(servicio("Consulta", 15000.0, null, 1L)));
        assertEquals("La duración es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearDuracionCeroLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> servicioService.crear(servicio("Consulta", 15000.0, 0, 1L)));
        assertEquals("La duración debe ser mayor a 0 minutos", ex.getMessage());
    }

    @Test
    void testCrearCategoriaNullLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> servicioService.crear(servicio("Consulta", 15000.0, 30, null)));
        assertEquals("La categoría es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearCategoriaInexistenteLanzaNotFound() {
        when(categoriaRepository.existsById(999L)).thenReturn(false);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> servicioService.crear(servicio("Consulta", 15000.0, 30, 999L)));
        assertEquals("Categoría no encontrada", ex.getMessage());
    }

    @Test
    void testCrearServicioCasoFelizLoDejaActivo() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(servicioRepository.save(any(Servicio.class))).thenAnswer(inv -> inv.getArgument(0));

        Servicio creado = servicioService.crear(valido());

        assertTrue(creado.getActivo());
        assertEquals("Consulta General", creado.getNombre());
    }

    @Test
    void testCrearServicioConActivoDefinidoLoRespeta() {
        Servicio s = valido();
        s.setActivo(false);
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(servicioRepository.save(any(Servicio.class))).thenAnswer(inv -> inv.getArgument(0));

        assertFalse(servicioService.crear(s).getActivo());
    }

    @Test
    void testListarServicios() {
        when(servicioRepository.findAll()).thenReturn(java.util.List.of(new Servicio()));
        assertEquals(1, servicioService.listar().size());
    }

    @Test
    void testActivarYDesactivar() {
        Servicio s = new Servicio();
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(s));
        when(servicioRepository.save(any(Servicio.class))).thenAnswer(inv -> inv.getArgument(0));

        assertTrue(servicioService.activar(1L).getActivo());
        assertFalse(servicioService.desactivar(1L).getActivo());
    }

    @Test
    void testActivarServicioInexistenteLanzaNotFound() {
        when(servicioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> servicioService.activar(99L));
    }

    @Test
    void testActualizarPrecioValido() {
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(new Servicio()));
        when(servicioRepository.save(any(Servicio.class))).thenAnswer(inv -> inv.getArgument(0));
        assertEquals(30000.0, servicioService.actualizarPrecio(1L, 30000.0).getPrecio());
    }

    @Test
    void testActualizarPrecioInvalidoLanzaBusinessRule() {
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(new Servicio()));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> servicioService.actualizarPrecio(1L, 0.0));
        assertEquals("El precio debe ser mayor a 0", ex.getMessage());
    }

    @Test
    void testEliminarServicioLlamaAlRepositorio() {
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(new Servicio()));
        servicioService.eliminar(1L);
        verify(servicioRepository).deleteById(1L);
    }

    @Test
    void testEliminarServicioInexistenteLanzaNotFound() {
        when(servicioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> servicioService.eliminar(99L));
    }

    @Test
    void testDesactivarServicioInexistenteLanzaNotFound() {
        when(servicioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> servicioService.desactivar(99L));
    }

    @Test
    void testActualizarPrecioServicioInexistenteLanzaNotFound() {
        when(servicioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> servicioService.actualizarPrecio(99L, 20000.0));
    }

    @Test
    void testActualizarPrecioPrecioNuloLanzaBusinessRule() {
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(new Servicio()));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> servicioService.actualizarPrecio(1L, null));
        assertEquals("El precio es obligatorio", ex.getMessage());
    }
}
