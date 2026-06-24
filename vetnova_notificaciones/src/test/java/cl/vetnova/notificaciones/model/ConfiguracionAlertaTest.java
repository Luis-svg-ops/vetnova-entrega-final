package cl.vetnova.notificaciones.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ConfiguracionAlertaTest {

    @Test
    void testGettersYSetters() {
        ConfiguracionAlerta c = new ConfiguracionAlerta();
        c.setId(1L);
        assertEquals(1L, c.getId());
        c.setUsuarioId(2L);
        assertEquals(2L, c.getUsuarioId());
        c.setTipoEvento("STOCK_CRITICO");
        assertEquals("STOCK_CRITICO", c.getTipoEvento());
        c.setCanal("EMAIL");
        assertEquals("EMAIL", c.getCanal());
        c.setActiva(true);
        assertTrue(c.getActiva());
    }
}
