package cl.vetnova.envio.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class EnvioResponseTest {

    @Test
    void testEnvioResponse() {
        EnvioResponse envioResponse = new EnvioResponse();
        envioResponse.setId(1L);
        assertEquals(1L, envioResponse.getId());
        envioResponse.setNumeroGuia("x");
        assertEquals("x", envioResponse.getNumeroGuia());
        envioResponse.setOrdenId(1L);
        assertEquals(1L, envioResponse.getOrdenId());
        envioResponse.setTipoEnvio("x");
        assertEquals("x", envioResponse.getTipoEnvio());
        envioResponse.setIdSucursalOrigen(1L);
        assertEquals(1L, envioResponse.getIdSucursalOrigen());
        envioResponse.setDireccionEntrega("x");
        assertEquals("x", envioResponse.getDireccionEntrega());
        envioResponse.setEstadoActual("x");
        assertEquals("x", envioResponse.getEstadoActual());
        envioResponse.setFechaCreacion(LocalDateTime.now());
        assertNotNull(envioResponse.getFechaCreacion());
        envioResponse.setHistorial(new ArrayList<>());
        assertNotNull(envioResponse.getHistorial());
    }

}