package cl.vetnova.inventario.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class AsociarProductoRequestTest {

    @Test
    void testAsociarProductoRequest() {
        AsociarProductoRequest request = new AsociarProductoRequest();
        request.setProductoId(5L);
        assertEquals(5L, request.getProductoId());
    }
}
