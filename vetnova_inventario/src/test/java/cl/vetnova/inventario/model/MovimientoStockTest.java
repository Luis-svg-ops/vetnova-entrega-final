package cl.vetnova.inventario.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class MovimientoStockTest {

    @Test
    void testMovimientoStock() {
        MovimientoStock movimientoStock = new MovimientoStock();
        movimientoStock.setId(1L);
        assertEquals(1L, movimientoStock.getId());
        movimientoStock.setInventarioId(2L);
        assertEquals(2L, movimientoStock.getInventarioId());
        movimientoStock.setTipo(TipoMovimiento.ENTRADA);
        assertEquals(TipoMovimiento.ENTRADA, movimientoStock.getTipo());
        movimientoStock.setCantidad(5);
        assertEquals(5, movimientoStock.getCantidad());
        movimientoStock.setMotivo("Compra");
        assertEquals("Compra", movimientoStock.getMotivo());
        movimientoStock.setResponsable("Juan");
        assertEquals("Juan", movimientoStock.getResponsable());
        movimientoStock.setSucursal("SANTIAGO");
        assertEquals("SANTIAGO", movimientoStock.getSucursal());
        LocalDateTime t = LocalDateTime.now();
        movimientoStock.setFecha(t);
        assertEquals(t, movimientoStock.getFecha());
    }
}
