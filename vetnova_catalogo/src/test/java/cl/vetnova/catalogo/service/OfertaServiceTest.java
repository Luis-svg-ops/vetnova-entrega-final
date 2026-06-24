package cl.vetnova.catalogo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import cl.vetnova.catalogo.exception.BusinessRuleException;
import cl.vetnova.catalogo.exception.ConflictException;
import cl.vetnova.catalogo.exception.ResourceNotFoundException;
import cl.vetnova.catalogo.model.Oferta;
import cl.vetnova.catalogo.model.Producto;
import cl.vetnova.catalogo.repository.OfertaRepository;
import cl.vetnova.catalogo.repository.ProductoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class OfertaServiceTest {

    @Mock
    private OfertaRepository ofertaRepository;
    @Mock
    private ProductoRepository productoRepository;
    @InjectMocks
    private OfertaService ofertaService;

    private final LocalDate inicio = LocalDate.now().plusDays(5);
    private final LocalDate fin = LocalDate.now().plusDays(15);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Oferta oferta(Long productoId, Double descuento, LocalDate ini, LocalDate f) {
        Oferta o = new Oferta();
        o.setProductoId(productoId);
        o.setDescuento(descuento);
        o.setFechaInicio(ini);
        o.setFechaFin(f);
        return o;
    }

    private void productoActivo() {
        Producto p = new Producto();
        p.setActivo(true);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(p));
    }

    @Test
    void testCrearProductoIdNullLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> ofertaService.crear(oferta(null, 10.0, inicio, fin)));
        assertEquals("El productoId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearProductoInexistenteLanzaNotFound() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> ofertaService.crear(oferta(99L, 10.0, inicio, fin)));
    }

    @Test
    void testCrearProductoInactivoLanzaBusinessRule() {
        Producto p = new Producto();
        p.setActivo(false);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(p));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> ofertaService.crear(oferta(1L, 10.0, inicio, fin)));
        assertEquals("No se puede crear oferta para producto inactivo", ex.getMessage());
    }

    @Test
    void testCrearDescuentoNullLanzaBusinessRule() {
        productoActivo();
        assertThrows(BusinessRuleException.class, () -> ofertaService.crear(oferta(1L, null, inicio, fin)));
    }

    @Test
    void testCrearDescuentoCeroLanzaBusinessRule() {
        productoActivo();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> ofertaService.crear(oferta(1L, 0.0, inicio, fin)));
        assertEquals("El descuento debe ser mayor a 0", ex.getMessage());
    }

    @Test
    void testCrearDescuentoMayorA100LanzaBusinessRule() {
        productoActivo();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> ofertaService.crear(oferta(1L, 110.0, inicio, fin)));
        assertEquals("El descuento porcentual no puede ser mayor a 100%", ex.getMessage());
    }

    @Test
    void testCrearFechaInicioNullLanzaBusinessRule() {
        productoActivo();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> ofertaService.crear(oferta(1L, 15.0, null, fin)));
        assertEquals("La fecha de inicio es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearFechaFinNullLanzaBusinessRule() {
        productoActivo();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> ofertaService.crear(oferta(1L, 15.0, inicio, null)));
        assertEquals("La fecha de fin es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearFechaFinAntesDeInicioLanzaBusinessRule() {
        productoActivo();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> ofertaService.crear(oferta(1L, 15.0, fin, inicio)));
        assertEquals("La fecha de fin debe ser igual o posterior a la fecha de inicio", ex.getMessage());
    }

    @Test
    void testCrearFechaInicioPasadaLanzaBusinessRule() {
        productoActivo();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> ofertaService.crear(oferta(1L, 15.0, LocalDate.now().minusDays(10), fin)));
        assertEquals("La fecha de inicio no puede ser en el pasado", ex.getMessage());
    }

    @Test
    void testCrearOfertaSolapadaLanzaConflict() {
        productoActivo();
        Oferta existente = oferta(1L, 20.0, inicio.minusDays(2), fin.plusDays(2));
        when(ofertaRepository.findByProductoIdAndActivaTrue(1L)).thenReturn(List.of(existente));
        assertThrows(ConflictException.class, () -> ofertaService.crear(oferta(1L, 15.0, inicio, fin)));
    }

    @Test
    void testCrearConOfertaExistentePosteriorNoSolapa() {
        productoActivo();
        Oferta existente = oferta(1L, 20.0, fin.plusDays(5), fin.plusDays(20));
        when(ofertaRepository.findByProductoIdAndActivaTrue(1L)).thenReturn(List.of(existente));
        when(ofertaRepository.save(any(Oferta.class))).thenAnswer(i -> i.getArgument(0));
        assertTrue(ofertaService.crear(oferta(1L, 15.0, inicio, fin)).getActiva());
    }

    @Test
    void testCrearConOfertaExistenteAnteriorNoSolapa() {
        productoActivo();
        Oferta existente = oferta(1L, 20.0, inicio.minusDays(20), inicio.minusDays(5));
        when(ofertaRepository.findByProductoIdAndActivaTrue(1L)).thenReturn(List.of(existente));
        when(ofertaRepository.save(any(Oferta.class))).thenAnswer(i -> i.getArgument(0));
        assertTrue(ofertaService.crear(oferta(1L, 15.0, inicio, fin)).getActiva());
    }

    @Test
    void testCrearOfertaCasoFeliz() {
        productoActivo();
        when(ofertaRepository.findByProductoIdAndActivaTrue(1L)).thenReturn(List.of());
        when(ofertaRepository.save(any(Oferta.class))).thenAnswer(i -> i.getArgument(0));
        assertTrue(ofertaService.crear(oferta(1L, 15.0, inicio, fin)).getActiva());
    }

    @Test
    void testCrearConActivaDefinidaLaRespeta() {
        productoActivo();
        when(ofertaRepository.findByProductoIdAndActivaTrue(1L)).thenReturn(List.of());
        when(ofertaRepository.save(any(Oferta.class))).thenAnswer(i -> i.getArgument(0));
        Oferta o = oferta(1L, 15.0, inicio, fin);
        o.setActiva(false);
        assertFalse(ofertaService.crear(o).getActiva());
    }

    @Test
    void testListarYActivarYDesactivarYEliminar() {
        when(ofertaRepository.findAll()).thenReturn(List.of(new Oferta()));
        when(ofertaRepository.findById(1L)).thenReturn(Optional.of(new Oferta()));
        when(ofertaRepository.save(any(Oferta.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals(1, ofertaService.listar().size());
        assertTrue(ofertaService.activar(1L).getActiva());
        assertFalse(ofertaService.desactivar(1L).getActiva());
        ofertaService.eliminar(1L);
        verify(ofertaRepository).deleteById(1L);
    }

    @Test
    void testEliminarOfertaInexistenteLanzaNotFound() {
        when(ofertaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> ofertaService.eliminar(99L));
    }
}
