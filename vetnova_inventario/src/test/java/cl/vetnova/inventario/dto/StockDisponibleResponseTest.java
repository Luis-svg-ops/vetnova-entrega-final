package cl.vetnova.inventario.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class StockDisponibleResponseTest {

    @Test
    void testStockDisponibleResponse() {
        StockDisponibleResponse stockDisponibleResponse = new StockDisponibleResponse();
        stockDisponibleResponse.setIdProducto(1L);
        assertEquals(1L, stockDisponibleResponse.getIdProducto());
        stockDisponibleResponse.setIdSucursal("CHILLAN");
        assertEquals("CHILLAN", stockDisponibleResponse.getIdSucursal());
        stockDisponibleResponse.setCantidadDisponible(1);
        assertEquals(1, stockDisponibleResponse.getCantidadDisponible());
        assertNotNull(new StockDisponibleResponse(1L, "CHILLAN", 1));
    }

}