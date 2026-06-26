package cl.vetnova.envio.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import cl.vetnova.envio.client.NotificacionesClient;
import cl.vetnova.envio.client.VentasClient;
import cl.vetnova.envio.dto.ActualizarEstadoRequest;
import cl.vetnova.envio.dto.CrearEnvioRequest;
import cl.vetnova.envio.dto.EnvioResponse;
import cl.vetnova.envio.exception.BusinessRuleException;
import cl.vetnova.envio.exception.ResourceNotFoundException;
import cl.vetnova.envio.model.Envio;
import cl.vetnova.envio.model.EstadoEnvio;
import cl.vetnova.envio.model.TipoEnvio;
import cl.vetnova.envio.repository.EnvioRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class EnvioServiceTest {

    @Mock
    private EnvioRepository envioRepository;
    @Mock
    private VentasClient ventasClient;
    @Mock
    private NotificacionesClient notificacionesClient;

    @InjectMocks
    private EnvioService envioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private CrearEnvioRequest requestDomicilio() {
        CrearEnvioRequest request = new CrearEnvioRequest();
        request.setOrdenId(1L);
        request.setTipoEnvio("DOMICILIO");
        request.setIdSucursalOrigen("CHILLAN");
        request.setDireccionEntrega("Av. Libertad 123, Chillán");
        return request;
    }

    private Envio envioEnEstado(EstadoEnvio estado) {
        Envio envio = new Envio();
        envio.setOrdenId(1L);
        envio.setTipoEnvio(TipoEnvio.DOMICILIO);
        envio.setIdSucursalOrigen("CHILLAN");
        envio.setNumeroGuia("GD-1");
        envio.setEstadoActual(estado);
        return envio;
    }

    @Test
    void testCrearEnvioConOrdenValidaGeneraGuiaYTrackingInicial() {
        when(ventasClient.ordenExiste(1L)).thenReturn(true);
        when(envioRepository.save(any(Envio.class))).thenAnswer(inv -> inv.getArgument(0));

        EnvioResponse response = envioService.crearEnvio(requestDomicilio());

        assertEquals("PREPARANDO", response.getEstadoActual());
        assertTrue(response.getNumeroGuia().startsWith("GD-"));
        assertEquals(1, response.getHistorial().size());
    }

    @Test
    void testCrearEnvioConOrdenInexistenteLanzaExcepcion() {
        when(ventasClient.ordenExiste(1L)).thenReturn(false);

        assertThrows(BusinessRuleException.class, () -> envioService.crearEnvio(requestDomicilio()));
        verify(envioRepository, never()).save(any());
    }

    @Test
    void testEnvioADomicilioSinDireccionLanzaExcepcion() {
        when(ventasClient.ordenExiste(1L)).thenReturn(true);
        CrearEnvioRequest request = requestDomicilio();
        request.setDireccionEntrega("  ");

        assertThrows(BusinessRuleException.class, () -> envioService.crearEnvio(request));
    }

    @Test
    void testRetiroEnTiendaNoExigeDireccion() {
        when(ventasClient.ordenExiste(1L)).thenReturn(true);
        when(envioRepository.save(any(Envio.class))).thenAnswer(inv -> inv.getArgument(0));
        CrearEnvioRequest request = requestDomicilio();
        request.setTipoEnvio("RETIRO_TIENDA");
        request.setDireccionEntrega(null);

        EnvioResponse response = envioService.crearEnvio(request);

        assertEquals("RETIRO_TIENDA", response.getTipoEnvio());
    }

    @Test
    void testActualizarEstadoAgregaTrackingYAvisaANotificaciones() {
        Envio envio = envioEnEstado(EstadoEnvio.PREPARANDO);
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));
        when(envioRepository.save(any(Envio.class))).thenAnswer(inv -> inv.getArgument(0));

        ActualizarEstadoRequest request = new ActualizarEstadoRequest();
        request.setEstado("EN_RUTA");
        request.setObservacion("Salió de bodega");
        EnvioResponse response = envioService.actualizarEstado(1L, request);

        assertEquals("EN_RUTA", response.getEstadoActual());
        assertEquals(1, response.getHistorial().size());
        verify(notificacionesClient).avisarCambioEstado(anyLong(), any(), anyString());
    }

    @Test
    void testEnvioEntregadoNoSePuedeModificar() {
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envioEnEstado(EstadoEnvio.ENTREGADO)));

        ActualizarEstadoRequest request = new ActualizarEstadoRequest();
        request.setEstado("CANCELADO");

        assertThrows(BusinessRuleException.class, () -> envioService.actualizarEstado(1L, request));
    }

    @Test
    void testSoloUnEnvioEnRutaPuedeQuedarEntregado() {
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envioEnEstado(EstadoEnvio.PREPARANDO)));

        ActualizarEstadoRequest request = new ActualizarEstadoRequest();
        request.setEstado("ENTREGADO");

        assertThrows(BusinessRuleException.class, () -> envioService.actualizarEstado(1L, request));
        verify(notificacionesClient, never()).avisarCambioEstado(anyLong(), any(), anyString());
    }

    @Test
    void testBuscarEnvioInexistenteLanzaNotFound() {
        when(envioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> envioService.obtenerPorId(99L));
    }

    @Test
    void testCrearEnvioADomicilioSinDireccionLanzaBusinessRule() {
        when(ventasClient.ordenExiste(anyLong())).thenReturn(true);
        CrearEnvioRequest request = requestDomicilio();
        request.setDireccionEntrega(null);

        assertThrows(BusinessRuleException.class, () -> envioService.crearEnvio(request));
        verify(envioRepository, never()).save(any());
    }

    @Test
    void testListarSinEnviosDevuelveListaVacia() {
        when(envioRepository.findAll()).thenReturn(List.of());

        assertTrue(envioService.listar().isEmpty());
    }

    @Test
    void testObtenerPorIdYTrackingDeEnvioExistente() {
        Envio envio = new Envio();
        envio.setEstadoActual(EstadoEnvio.PREPARANDO);
        envio.setTipoEnvio(TipoEnvio.DOMICILIO);
        when(envioRepository.findById(5L)).thenReturn(Optional.of(envio));

        assertNotNull(envioService.obtenerPorId(5L));
        assertTrue(envioService.obtenerTracking(5L).isEmpty());
    }

    @Test
    void testActualizarEstadoDeEnvioFinalLanzaBusinessRule() {
        Envio envio = new Envio();
        envio.setEstadoActual(EstadoEnvio.ENTREGADO);
        when(envioRepository.findById(5L)).thenReturn(Optional.of(envio));
        ActualizarEstadoRequest request = new ActualizarEstadoRequest();
        request.setEstado("EN_RUTA");

        assertThrows(BusinessRuleException.class, () -> envioService.actualizarEstado(5L, request));
    }

    @Test
    void testEntregarEnvioQueNoEstaEnRutaLanzaBusinessRule() {
        Envio envio = new Envio();
        envio.setEstadoActual(EstadoEnvio.PREPARANDO);
        when(envioRepository.findById(5L)).thenReturn(Optional.of(envio));
        ActualizarEstadoRequest request = new ActualizarEstadoRequest();
        request.setEstado("ENTREGADO");

        assertThrows(BusinessRuleException.class, () -> envioService.actualizarEstado(5L, request));
    }

    @Test
    void testActualizarEstadoDeEnvioCanceladoLanzaBusinessRule() {
        Envio envio = new Envio();
        envio.setEstadoActual(EstadoEnvio.CANCELADO);
        when(envioRepository.findById(5L)).thenReturn(Optional.of(envio));
        ActualizarEstadoRequest request = new ActualizarEstadoRequest();
        request.setEstado("EN_RUTA");

        assertThrows(BusinessRuleException.class, () -> envioService.actualizarEstado(5L, request));
    }

    @Test
    void testEntregarEnvioEnRutaFunciona() {
        Envio envio = new Envio();
        envio.setEstadoActual(EstadoEnvio.EN_RUTA);
        envio.setTipoEnvio(TipoEnvio.DOMICILIO);
        when(envioRepository.findById(5L)).thenReturn(Optional.of(envio));
        when(envioRepository.save(any(Envio.class))).thenAnswer(inv -> inv.getArgument(0));
        ActualizarEstadoRequest request = new ActualizarEstadoRequest();
        request.setEstado("ENTREGADO");

        assertNotNull(envioService.actualizarEstado(5L, request));
        assertEquals(EstadoEnvio.ENTREGADO, envio.getEstadoActual());
    }
}
