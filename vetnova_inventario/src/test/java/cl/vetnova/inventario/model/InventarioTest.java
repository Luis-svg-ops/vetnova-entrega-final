package cl.vetnova.inventario.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

public class InventarioTest {

    @Test
    void testInventario() {
        LocalDateTime t = LocalDateTime.now();
        LocalDate d = LocalDate.of(2026, 1, 1);
        Inventario inventario = new Inventario();
        inventario.setId(1L);
        assertEquals(1L, inventario.getId());
        inventario.setProductoId(1L);
        assertEquals(1L, inventario.getProductoId());
        inventario.setSucursal("x");
        assertEquals("x", inventario.getSucursal());
        inventario.setStockDisponible(1);
        assertEquals(1, inventario.getStockDisponible());
        inventario.setStockMinimo(1);
        assertEquals(1, inventario.getStockMinimo());
        inventario.setStockTransito(1);
        assertEquals(1, inventario.getStockTransito());
    }
}
