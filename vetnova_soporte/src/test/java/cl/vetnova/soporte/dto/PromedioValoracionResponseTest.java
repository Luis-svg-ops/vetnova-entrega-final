package cl.vetnova.soporte.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class PromedioValoracionResponseTest {

    @Test
    void testConstructorYSetters() {
        PromedioValoracionResponse r = new PromedioValoracionResponse(1L, 4.3, 10);
        assertEquals(1L, r.getSucursalId());
        assertEquals(4.3, r.getPromedio());
        assertEquals(10, r.getTotal());

        PromedioValoracionResponse vacio = new PromedioValoracionResponse();
        vacio.setSucursalId(2L);
        vacio.setPromedio(5.0);
        vacio.setTotal(3);
        assertEquals(2L, vacio.getSucursalId());
        assertEquals(5.0, vacio.getPromedio());
        assertEquals(3, vacio.getTotal());
    }
}
