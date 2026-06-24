package cl.vetnova.envio.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cl.vetnova.envio.client.InventarioClient;
import cl.vetnova.envio.dto.CrearTransferenciaRequest;
import cl.vetnova.envio.dto.TransferenciaResponse;
import cl.vetnova.envio.exception.BusinessRuleException;
import cl.vetnova.envio.model.TransferenciaSucursal;
import cl.vetnova.envio.repository.TransferenciaSucursalRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TransferenciaServiceTest {

    @Mock
    private TransferenciaSucursalRepository transferenciaRepository;
    @Mock
    private InventarioClient inventarioClient;

    @InjectMocks
    private TransferenciaService transferenciaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private CrearTransferenciaRequest request(Long origen, Long destino) {
        CrearTransferenciaRequest request = new CrearTransferenciaRequest();
        request.setIdProducto(1L);
        request.setIdSucursalOrigen(origen);
        request.setIdSucursalDestino(destino);
        request.setCantidad(5);
        return request;
    }

    @Test
    void testTransferenciaRegistraSalidaEnOrigenYEntradaEnDestino() {
        when(transferenciaRepository.save(any(TransferenciaSucursal.class))).thenAnswer(inv -> inv.getArgument(0));

        TransferenciaResponse response = transferenciaService.crearTransferencia(request(1L, 2L));

        assertEquals("COMPLETADA", response.getEstado());
        verify(inventarioClient).registrarMovimiento(eq(1L), eq(1L), eq("SALIDA"), eq(5), any());
        verify(inventarioClient).registrarMovimiento(eq(1L), eq(2L), eq("ENTRADA"), eq(5), any());
    }

    @Test
    void testTransferenciaConMismaSucursalLanzaExcepcion() {
        assertThrows(BusinessRuleException.class,
                () -> transferenciaService.crearTransferencia(request(1L, 1L)));
        verify(inventarioClient, never()).registrarMovimiento(any(), any(), any(), any(), any());
    }

    @Test
    void testListarDevuelveLasTransferenciasGuardadas() {
        TransferenciaSucursal transferencia = new TransferenciaSucursal();
        transferencia.setIdProducto(1L);
        transferencia.setIdSucursalOrigen(1L);
        transferencia.setIdSucursalDestino(2L);
        transferencia.setCantidad(5);
        when(transferenciaRepository.findAll()).thenReturn(List.of(transferencia));

        List<TransferenciaResponse> lista = transferenciaService.listar();

        assertEquals(1, lista.size());
        assertEquals(2L, lista.get(0).getIdSucursalDestino());
    }
}
