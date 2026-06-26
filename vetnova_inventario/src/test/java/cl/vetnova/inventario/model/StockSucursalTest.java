package cl.vetnova.inventario.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class StockSucursalTest {

    @Test
    void testStockSucursal() {
        StockSucursal stockSucursal = new StockSucursal();
        stockSucursal.setId(1L);
        assertEquals(1L, stockSucursal.getId());
        stockSucursal.setProducto(new Producto());
        assertNotNull(stockSucursal.getProducto());
        stockSucursal.setIdSucursal("CHILLAN");
        assertEquals("CHILLAN", stockSucursal.getIdSucursal());
        stockSucursal.setCantidad(1);
        assertEquals(1, stockSucursal.getCantidad());
        stockSucursal.setStockMinimo(1);
        assertEquals(1, stockSucursal.getStockMinimo());
    }

}