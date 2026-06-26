package cl.vetnova.envio.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class EnvioTest {

    @Test
    void testEnvio() {
        Envio envio = new Envio();
        envio.setId(1L);
        assertEquals(1L, envio.getId());
        envio.setNumeroGuia("x");
        assertEquals("x", envio.getNumeroGuia());
        envio.setOrdenId(1L);
        assertEquals(1L, envio.getOrdenId());
        envio.setTipoEnvio(TipoEnvio.DOMICILIO);
        assertEquals(TipoEnvio.DOMICILIO, envio.getTipoEnvio());
        envio.setIdSucursalOrigen("CHILLAN");
        assertEquals("CHILLAN", envio.getIdSucursalOrigen());
        envio.setDireccionEntrega("x");
        assertEquals("x", envio.getDireccionEntrega());
        envio.setEstadoActual(EstadoEnvio.PREPARANDO);
        assertEquals(EstadoEnvio.PREPARANDO, envio.getEstadoActual());
        envio.setFechaCreacion(LocalDateTime.now());
        assertNotNull(envio.getFechaCreacion());
        envio.setHistorial(new ArrayList<>());
        assertNotNull(envio.getHistorial());
    }

}