package cl.vetnova.envio.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cl.vetnova.envio.exception.BusinessRuleException;
import cl.vetnova.envio.exception.ResourceNotFoundException;
import cl.vetnova.envio.model.Despacho;
import cl.vetnova.envio.model.RegistroSeguimiento;
import cl.vetnova.envio.model.SeguimientoPedido;
import cl.vetnova.envio.repository.DespachoRepository;
import cl.vetnova.envio.repository.RegistroSeguimientoRepository;
import cl.vetnova.envio.repository.SeguimientoPedidoRepository;

public class SeguimientoPedidoServiceTest {

    @Mock
    private SeguimientoPedidoRepository seguimientoPedidoRepository;
    @Mock
    private DespachoRepository despachoRepository;
    @Mock
    private RegistroSeguimientoRepository registroSeguimientoRepository;
    @InjectMocks
    private SeguimientoPedidoService seguimientoPedidoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private SeguimientoPedido seguimiento(Long despachoId, Long ordenId, String estado) {
        SeguimientoPedido s = new SeguimientoPedido();
        s.setId(1L);
        s.setDespachoId(despachoId);
        s.setOrdenId(ordenId);
        s.setEstado(estado);
        return s;
    }

    private Despacho despacho(Long ordenId, String estado) {
        Despacho d = new Despacho();
        d.setId(1L);
        d.setOrdenId(ordenId);
        d.setEstado(estado);
        return d;
    }

    private void despachoValido(String estado) {
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho(1L, estado)));
    }

    // ---- crear ----

    @Test
    void testCrearDespachoIdNull() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> seguimientoPedidoService.crear(seguimiento(null, 1L, "CREADO")));
        assertEquals("El despachoId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearDespachoInexistente() {
        when(despachoRepository.findById(999L)).thenReturn(Optional.empty());
        SeguimientoPedido s = seguimiento(999L, 1L, "CREADO");
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> seguimientoPedidoService.crear(s));
        assertEquals("Despacho no encontrado", ex.getMessage());
    }

    @Test
    void testCrearOrdenIdNull() {
        despachoValido("CREADO");
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> seguimientoPedidoService.crear(seguimiento(1L, null, "CREADO")));
        assertEquals("El ordenId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearOrdenNoCoincide() {
        despachoValido("CREADO");
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> seguimientoPedidoService.crear(seguimiento(1L, 99L, "CREADO")));
        assertEquals("La orden no corresponde al despacho indicado", ex.getMessage());
    }

    @Test
    void testCrearEstadoNull() {
        despachoValido("CREADO");
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> seguimientoPedidoService.crear(seguimiento(1L, 1L, null)));
        assertEquals("El estado es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearEstadoInvalido() {
        despachoValido("CREADO");
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> seguimientoPedidoService.crear(seguimiento(1L, 1L, "VOLANDO")));
        assertEquals("Estado no válido. Valores permitidos: CREADO, PREPARANDO, ENVIADO, ENTREGADO, CANCELADO",
                ex.getMessage());
    }

    @Test
    void testCrearEstadoAdelantadoAlDespacho() {
        despachoValido("PREPARANDO");
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> seguimientoPedidoService.crear(seguimiento(1L, 1L, "ENTREGADO")));
        assertEquals("El estado del seguimiento no puede adelantarse al estado real del despacho", ex.getMessage());
    }

    @Test
    void testCrearCasoFeliz() {
        despachoValido("ENVIADO");
        when(seguimientoPedidoRepository.save(any(SeguimientoPedido.class))).thenAnswer(inv -> inv.getArgument(0));
        SeguimientoPedido s = seguimiento(1L, 1L, "CREADO");
        s.setDescripcion(null);
        SeguimientoPedido creado = seguimientoPedidoService.crear(s);
        assertNotNull(creado.getFechaActualizacion());
    }

    // ---- actualizarEstado (CA-SEG-10/11/12) ----

    @Test
    void testActualizarEstadoIgualAlActual() {
        when(seguimientoPedidoRepository.findById(1L)).thenReturn(Optional.of(seguimiento(1L, 1L, "PREPARANDO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> seguimientoPedidoService.actualizarEstado(1L, "PREPARANDO", "sin cambios"));
        assertEquals("El estado nuevo debe ser distinto al estado actual", ex.getMessage());
    }

    @Test
    void testActualizarEstadoNuloEsInvalido() {
        when(seguimientoPedidoRepository.findById(1L)).thenReturn(Optional.of(seguimiento(1L, 1L, "PREPARANDO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> seguimientoPedidoService.actualizarEstado(1L, null, "x"));
        assertEquals("Estado no válido. Valores permitidos: CREADO, PREPARANDO, ENVIADO, ENTREGADO, CANCELADO",
                ex.getMessage());
    }

    @Test
    void testActualizarEstadoInvalidoNoNulo() {
        when(seguimientoPedidoRepository.findById(1L)).thenReturn(Optional.of(seguimiento(1L, 1L, "PREPARANDO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> seguimientoPedidoService.actualizarEstado(1L, "VOLANDO", "x"));
        assertEquals("Estado no válido. Valores permitidos: CREADO, PREPARANDO, ENVIADO, ENTREGADO, CANCELADO",
                ex.getMessage());
    }

    @Test
    void testActualizarEstadoCasoFeliz() {
        when(seguimientoPedidoRepository.findById(1L)).thenReturn(Optional.of(seguimiento(1L, 1L, "CREADO")));
        when(seguimientoPedidoRepository.save(any(SeguimientoPedido.class))).thenAnswer(inv -> inv.getArgument(0));
        when(registroSeguimientoRepository.save(any(RegistroSeguimiento.class))).thenAnswer(inv -> inv.getArgument(0));

        SeguimientoPedido actualizado = seguimientoPedidoService.actualizarEstado(1L, "PREPARANDO", "Preparando paquete");

        assertEquals("PREPARANDO", actualizado.getEstado());
        assertEquals("Preparando paquete", actualizado.getDescripcion());
        verify(registroSeguimientoRepository).save(any(RegistroSeguimiento.class));
    }

    // ---- getHistorial (CA-SEG-13/14) ----

    @Test
    void testGetHistorialVacio() {
        when(seguimientoPedidoRepository.findById(1L)).thenReturn(Optional.of(seguimiento(1L, 1L, "CREADO")));
        when(registroSeguimientoRepository.findBySeguimientoIdOrderByFechaAsc(1L)).thenReturn(List.of());
        assertTrue(seguimientoPedidoService.getHistorial(1L).isEmpty());
    }

    @Test
    void testGetHistorialConEntradas() {
        when(seguimientoPedidoRepository.findById(1L)).thenReturn(Optional.of(seguimiento(1L, 1L, "ENVIADO")));
        when(registroSeguimientoRepository.findBySeguimientoIdOrderByFechaAsc(1L))
                .thenReturn(List.of(new RegistroSeguimiento(), new RegistroSeguimiento()));
        assertEquals(2, seguimientoPedidoService.getHistorial(1L).size());
    }

    // ---- CRUD ----

    @Test
    void testListar() {
        when(seguimientoPedidoRepository.findAll()).thenReturn(List.of(new SeguimientoPedido()));
        assertEquals(1, seguimientoPedidoService.listar().size());
    }

    @Test
    void testObtenerPorIdInexistenteLanzaNotFound() {
        when(seguimientoPedidoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> seguimientoPedidoService.obtenerPorId(99L));
    }

    @Test
    void testActualizarExistente() {
        when(seguimientoPedidoRepository.findById(1L)).thenReturn(Optional.of(new SeguimientoPedido()));
        when(seguimientoPedidoRepository.save(any(SeguimientoPedido.class))).thenAnswer(inv -> inv.getArgument(0));
        assertNotNull(seguimientoPedidoService.actualizar(1L, seguimiento(1L, 1L, "CREADO")));
    }

    @Test
    void testEliminarExistente() {
        when(seguimientoPedidoRepository.existsById(1L)).thenReturn(true);
        seguimientoPedidoService.eliminar(1L);
        verify(seguimientoPedidoRepository).deleteById(1L);
    }

    @Test
    void testEliminarInexistenteLanzaNotFound() {
        when(seguimientoPedidoRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> seguimientoPedidoService.eliminar(99L));
        verify(seguimientoPedidoRepository, never()).deleteById(anyLong());
    }
}
