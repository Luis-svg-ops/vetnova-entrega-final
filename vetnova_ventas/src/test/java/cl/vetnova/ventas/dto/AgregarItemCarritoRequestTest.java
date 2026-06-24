package cl.vetnova.ventas.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class AgregarItemCarritoRequestTest {

    @Test
    void testAgregarItemCarritoRequest() {
        AgregarItemCarritoRequest request = new AgregarItemCarritoRequest();
        request.setItemId(10L);
        assertEquals(10L, request.getItemId());
        request.setTipo("PRODUCTO");
        assertEquals("PRODUCTO", request.getTipo());
        request.setNombre("Shampoo");
        assertEquals("Shampoo", request.getNombre());
        request.setCantidad(2);
        assertEquals(2, request.getCantidad());
        request.setPrecio(500.0);
        assertEquals(500.0, request.getPrecio());
    }
}
