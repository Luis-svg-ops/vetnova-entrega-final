package cl.vetnova.soporte.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class CategoriaTicketRequestTest {

    @Test
    void testGettersYSetters() {
        CategoriaTicketRequest r = new CategoriaTicketRequest();
        r.setNombre("Facturación");
        r.setDescripcion("Problemas de cobro");
        r.setAreaPorDefecto("Finanzas");
        r.setPrioridadDefault("ALTA");
        assertEquals("Facturación", r.getNombre());
        assertEquals("Problemas de cobro", r.getDescripcion());
        assertEquals("Finanzas", r.getAreaPorDefecto());
        assertEquals("ALTA", r.getPrioridadDefault());
    }
}
