package cl.vetnova.inventario.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;

public class PedidoProveedorRequestTest {

    @Test
    void testPedidoProveedorRequest() {
        PedidoProveedorRequest request = new PedidoProveedorRequest();
        request.setProveedorId(1L);
        assertEquals(1L, request.getProveedorId());
        request.setSucursal("SANTIAGO");
        assertEquals("SANTIAGO", request.getSucursal());
        request.setResponsable("Ana");
        assertEquals("Ana", request.getResponsable());
        request.setDetalles(List.of(new DetallePedidoRequest()));
        assertEquals(1, request.getDetalles().size());
    }
}
