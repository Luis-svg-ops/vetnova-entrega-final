package cl.vetnova.inventario.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

public class PedidoProveedorTest {

    @Test
    void testPedidoProveedor() {
        LocalDateTime t = LocalDateTime.now();
        LocalDate d = LocalDate.of(2026, 1, 1);
        PedidoProveedor pedidoProveedor = new PedidoProveedor();
        pedidoProveedor.setId(1L);
        assertEquals(1L, pedidoProveedor.getId());
        pedidoProveedor.setProveedorId(1L);
        assertEquals(1L, pedidoProveedor.getProveedorId());
        pedidoProveedor.setSucursal("x");
        assertEquals("x", pedidoProveedor.getSucursal());
        pedidoProveedor.setEstado("x");
        assertEquals("x", pedidoProveedor.getEstado());
        pedidoProveedor.setFechaPedido(t);
        assertEquals(t, pedidoProveedor.getFechaPedido());
        pedidoProveedor.setFechaRecepcion(t);
        assertEquals(t, pedidoProveedor.getFechaRecepcion());
        pedidoProveedor.setResponsable("x");
        assertEquals("x", pedidoProveedor.getResponsable());
    }
}
