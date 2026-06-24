package cl.vetnova.inventario.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class StockTotalResponseTest {

    @Test
    void testConstructorYSetter() {
        StockTotalResponse response = new StockTotalResponse(13);
        assertEquals(13, response.getStockTotal());
        StockTotalResponse vacio = new StockTotalResponse();
        vacio.setStockTotal(20);
        assertEquals(20, vacio.getStockTotal());
    }
}
