package cl.vetnova.inventario.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class StockSucursalResponseTest {

    @Test
    void testStockSucursalResponse() {
        StockSucursalResponse stockSucursalResponse = new StockSucursalResponse();
        stockSucursalResponse.setIdSucursal(1L);
        assertEquals(1L, stockSucursalResponse.getIdSucursal());
        stockSucursalResponse.setCantidad(1);
        assertEquals(1, stockSucursalResponse.getCantidad());
        stockSucursalResponse.setStockMinimo(1);
        assertEquals(1, stockSucursalResponse.getStockMinimo());
        stockSucursalResponse.setCritico(true);
        assertEquals(true, stockSucursalResponse.isCritico());
    }

}