package cl.vetnova.envio.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cl.vetnova.envio.client.VentasClient;
import cl.vetnova.envio.exception.BusinessRuleException;
import cl.vetnova.envio.exception.ConflictException;
import cl.vetnova.envio.exception.ResourceNotFoundException;
import cl.vetnova.envio.model.Despacho;
import cl.vetnova.envio.repository.DespachoRepository;

public class DespachoServiceTest {

    @Mock
    private DespachoRepository despachoRepository;
    @Mock
    private VentasClient ventasClient;
    @InjectMocks
    private DespachoService despachoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Despacho despacho(String tipo) {
        Despacho d = new Despacho();
        d.setOrdenId(1L);
        d.setTipo(tipo);
        d.setResponsable("Pedro");
        return d;
    }

    private Despacho conEstado(String estado) {
        Despacho d = new Despacho();
        d.setId(1L);
        d.setOrdenId(1L);
        d.setEstado(estado);
        return d;
    }

    private void ordenValida() {
        when(ventasClient.ordenExiste(1L)).thenReturn(true);
        when(despachoRepository.existsByOrdenId(1L)).thenReturn(false);
    }

    private void guarda() {
        when(despachoRepository.save(any(Despacho.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    // ---- crear ----

    @Test
    void testCrearOrdenIdNull() {
        Despacho d = despacho("DOMICILIO");
        d.setOrdenId(null);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> despachoService.crear(d));
        assertEquals("El ordenId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearOrdenInexistente() {
        when(ventasClient.ordenExiste(1L)).thenReturn(false);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> despachoService.crear(despacho("DOMICILIO")));
        assertEquals("Orden no encontrada", ex.getMessage());
    }

    @Test
    void testCrearOrdenYaTieneDespacho() {
        when(ventasClient.ordenExiste(1L)).thenReturn(true);
        when(despachoRepository.existsByOrdenId(1L)).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class, () -> despachoService.crear(despacho("DOMICILIO")));
        assertEquals("La orden ya tiene un despacho asociado", ex.getMessage());
    }

    @Test
    void testCrearTipoNull() {
        ordenValida();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> despachoService.crear(despacho(null)));
        assertEquals("El tipo de despacho es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearTipoInvalido() {
        ordenValida();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> despachoService.crear(despacho("DRONE")));
        assertEquals("Tipo no válido. Valores permitidos: DOMICILIO, RETIRO, TRANSFERENCIA", ex.getMessage());
    }

    @Test
    void testCrearTransferenciaSinOrigen() {
        ordenValida();
        Despacho d = despacho("TRANSFERENCIA");
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> despachoService.crear(d));
        assertEquals("La sucursal de origen es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearTransferenciaOrigenIgualDestino() {
        ordenValida();
        Despacho d = despacho("TRANSFERENCIA");
        d.setSucursalOrigen("1");
        d.setSucursalDestino("1");
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> despachoService.crear(d));
        assertEquals("La sucursal de destino debe ser distinta a la de origen", ex.getMessage());
    }

    @Test
    void testCrearDomicilioSinResponsable() {
        ordenValida();
        Despacho d = despacho("DOMICILIO");
        d.setResponsable(null);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> despachoService.crear(d));
        assertEquals("El responsable es obligatorio para despachos a domicilio", ex.getMessage());
    }

    @Test
    void testCrearFechaEstimadaAnterior() {
        ordenValida();
        Despacho d = despacho("DOMICILIO");
        d.setFechaEstimada(LocalDateTime.of(2020, 1, 1, 0, 0));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> despachoService.crear(d));
        assertEquals("La fecha estimada no puede ser anterior a la fecha de creación", ex.getMessage());
    }

    @Test
    void testCrearDomicilioCasoFeliz() {
        ordenValida();
        guarda();
        Despacho d = despacho("DOMICILIO");
        d.setFechaEstimada(LocalDateTime.now().plusDays(2));
        Despacho creado = despachoService.crear(d);
        assertEquals("CREADO", creado.getEstado());
        assertNotNull(creado.getFechaCreacion());
    }

    @Test
    void testCrearTransferenciaCasoFeliz() {
        ordenValida();
        guarda();
        Despacho d = despacho("TRANSFERENCIA");
        d.setSucursalOrigen("1");
        d.setSucursalDestino("2");
        assertEquals("CREADO", despachoService.crear(d).getEstado());
    }

    @Test
    void testCrearRetiroCasoFeliz() {
        ordenValida();
        guarda();
        assertEquals("CREADO", despachoService.crear(despacho("RETIRO")).getEstado());
    }

    // ---- transiciones ----

    @Test
    void testIniciarYaEnviado() {
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(conEstado("ENVIADO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> despachoService.iniciar(1L));
        assertEquals("No se puede iniciar un despacho que ya fue enviado", ex.getMessage());
    }

    @Test
    void testIniciarCasoFeliz() {
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(conEstado("CREADO")));
        guarda();
        assertEquals("PREPARANDO", despachoService.iniciar(1L).getEstado());
    }

    @Test
    void testEnviarEstadoInvalido() {
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(conEstado("CREADO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> despachoService.enviar(1L));
        assertEquals("El despacho debe estar en estado PREPARANDO para ser enviado", ex.getMessage());
    }

    @Test
    void testEnviarCasoFeliz() {
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(conEstado("PREPARANDO")));
        guarda();
        assertEquals("ENVIADO", despachoService.enviar(1L).getEstado());
    }

    @Test
    void testConfirmarEntregaEstadoInvalido() {
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(conEstado("PREPARANDO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> despachoService.confirmarEntrega(1L));
        assertEquals("El despacho debe estar en estado ENVIADO para confirmar entrega", ex.getMessage());
    }

    @Test
    void testConfirmarEntregaCasoFeliz() {
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(conEstado("ENVIADO")));
        guarda();
        Despacho entregado = despachoService.confirmarEntrega(1L);
        assertEquals("ENTREGADO", entregado.getEstado());
        assertNotNull(entregado.getFechaEntrega());
    }

    @Test
    void testCancelarEntregadoImpedido() {
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(conEstado("ENTREGADO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> despachoService.cancelar(1L));
        assertEquals("No se puede cancelar un despacho ya entregado", ex.getMessage());
    }

    @Test
    void testCancelarCasoFeliz() {
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(conEstado("PREPARANDO")));
        guarda();
        assertEquals("CANCELADO", despachoService.cancelar(1L).getEstado());
    }

    @Test
    void testActualizarEstadoTransicionInvalida() {
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(conEstado("ENTREGADO")));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> despachoService.actualizarEstado(1L, "CREADO"));
        assertEquals("Transición de estado no permitida: ENTREGADO → CREADO", ex.getMessage());
    }

    @Test
    void testActualizarEstadoCasoFeliz() {
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(conEstado("CREADO")));
        guarda();
        assertEquals("PREPARANDO", despachoService.actualizarEstado(1L, "PREPARANDO").getEstado());
    }

    // ---- CRUD ----

    @Test
    void testListar() {
        when(despachoRepository.findAll()).thenReturn(List.of(new Despacho()));
        assertEquals(1, despachoService.listar().size());
    }

    @Test
    void testObtenerPorIdInexistenteLanzaNotFound() {
        when(despachoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> despachoService.obtenerPorId(99L));
    }

    @Test
    void testActualizarExistente() {
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(new Despacho()));
        guarda();
        assertNotNull(despachoService.actualizar(1L, despacho("RETIRO")));
    }

    @Test
    void testEliminarExistente() {
        when(despachoRepository.existsById(1L)).thenReturn(true);
        despachoService.eliminar(1L);
        verify(despachoRepository).deleteById(1L);
    }

    @Test
    void testEliminarInexistenteLanzaNotFound() {
        when(despachoRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> despachoService.eliminar(99L));
        verify(despachoRepository, never()).deleteById(anyLong());
    }
}
