package cl.vetnova.soporte.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class PromedioValoracionResponseTest {

    @Test
    void testConstructorYSetters() {
        PromedioValoracionResponse r = new PromedioValoracionResponse("CHILLAN", 4.3, 10);
        assertEquals("CHILLAN", r.getSucursalId());
        assertEquals(4.3, r.getPromedio());
        assertEquals(10, r.getTotal());

        PromedioValoracionResponse vacio = new PromedioValoracionResponse();
        vacio.setSucursalId("LOS_ANGELES");
        vacio.setPromedio(5.0);
        vacio.setTotal(3);
        assertEquals("LOS_ANGELES", vacio.getSucursalId());
        assertEquals(5.0, vacio.getPromedio());
        assertEquals(3, vacio.getTotal());
    }
}
