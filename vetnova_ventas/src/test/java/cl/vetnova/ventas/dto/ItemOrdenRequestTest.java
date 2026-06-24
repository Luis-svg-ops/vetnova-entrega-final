package cl.vetnova.ventas.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ItemOrdenRequestTest {

    @Test
    void testItemOrdenRequest() {
        ItemOrdenRequest request = new ItemOrdenRequest();
        request.setItemId(10L);
        assertEquals(10L, request.getItemId());
        request.setTipoItem("PRODUCTO");
        assertEquals("PRODUCTO", request.getTipoItem());
        request.setNombreProducto("Alimento");
        assertEquals("Alimento", request.getNombreProducto());
        request.setCantidad(2);
        assertEquals(2, request.getCantidad());
        request.setPrecioUnitario(500.0);
        assertEquals(500.0, request.getPrecioUnitario());
        request.setSubtotal(1000.0);
        assertEquals(1000.0, request.getSubtotal());
    }
}
