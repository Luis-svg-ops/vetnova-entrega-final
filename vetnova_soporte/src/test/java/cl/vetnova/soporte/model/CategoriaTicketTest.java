package cl.vetnova.soporte.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class CategoriaTicketTest {

    @Test
    void testGettersYSetters() {
        CategoriaTicket c = new CategoriaTicket();
        c.setId(1L);
        assertEquals(1L, c.getId());
        c.setNombre("Facturación");
        assertEquals("Facturación", c.getNombre());
        c.setDescripcion("Problemas de cobro");
        assertEquals("Problemas de cobro", c.getDescripcion());
        c.setAreaPorDefecto("Finanzas");
        assertEquals("Finanzas", c.getAreaPorDefecto());
        c.setPrioridadDefault("ALTA");
        assertEquals("ALTA", c.getPrioridadDefault());
    }
}
