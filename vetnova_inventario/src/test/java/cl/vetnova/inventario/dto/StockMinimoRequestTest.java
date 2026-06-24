package cl.vetnova.inventario.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class StockMinimoRequestTest {

    @Test
    void testStockMinimoRequest() {
        StockMinimoRequest request = new StockMinimoRequest();
        request.setMinimo(10);
        assertEquals(10, request.getMinimo());
    }
}
