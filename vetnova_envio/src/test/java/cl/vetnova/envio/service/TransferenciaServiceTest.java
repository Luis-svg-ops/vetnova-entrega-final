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

    private CrearTransferenciaRequest request(String origen, String destino) {
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

        TransferenciaResponse response = transferenciaService.crearTransferencia(request("CHILLAN", "LOS_ANGELES"));

        assertEquals("COMPLETADA", response.getEstado());
        verify(inventarioClient).registrarMovimiento(eq(1L), eq("CHILLAN"), eq("SALIDA"), eq(5), any());
        verify(inventarioClient).registrarMovimiento(eq(1L), eq("LOS_ANGELES"), eq("ENTRADA"), eq(5), any());
    }

    @Test
    void testTransferenciaConMismaSucursalLanzaExcepcion() {
        assertThrows(BusinessRuleException.class,
                () -> transferenciaService.crearTransferencia(request("CHILLAN", "CHILLAN")));
        verify(inventarioClient, never()).registrarMovimiento(any(), any(), any(), any(), any());
    }

    @Test
    void testListarDevuelveLasTransferenciasGuardadas() {
        TransferenciaSucursal transferencia = new TransferenciaSucursal();
        transferencia.setIdProducto(1L);
        transferencia.setIdSucursalOrigen("CHILLAN");
        transferencia.setIdSucursalDestino("LOS_ANGELES");
        transferencia.setCantidad(5);
        when(transferenciaRepository.findAll()).thenReturn(List.of(transferencia));

        List<TransferenciaResponse> lista = transferenciaService.listar();

        assertEquals(1, lista.size());
        assertEquals("LOS_ANGELES", lista.get(0).getIdSucursalDestino());
    }
}
