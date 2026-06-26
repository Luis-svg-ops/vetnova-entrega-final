package cl.vetnova.ventas.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class OrdenResponseTest {

    @Test
    void testOrdenResponse() {
        OrdenResponse ordenResponse = new OrdenResponse();
        ordenResponse.setId(1L);
        assertEquals(1L, ordenResponse.getId());
        ordenResponse.setClienteId(1L);
        assertEquals(1L, ordenResponse.getClienteId());
        ordenResponse.setSucursal("CHILLAN");
        assertEquals("CHILLAN", ordenResponse.getSucursal());
        ordenResponse.setEstado("x");
        assertEquals("x", ordenResponse.getEstado());
        ordenResponse.setSubtotal(1.0);
        assertEquals(1.0, ordenResponse.getSubtotal());
        ordenResponse.setImpuestos(1.0);
        assertEquals(1.0, ordenResponse.getImpuestos());
        ordenResponse.setTotal(1.0);
        assertEquals(1.0, ordenResponse.getTotal());
        ordenResponse.setFechaCreacion(LocalDateTime.now());
        assertNotNull(ordenResponse.getFechaCreacion());
        ordenResponse.setFechaConfirmacion(LocalDateTime.now());
        assertNotNull(ordenResponse.getFechaConfirmacion());
        ordenResponse.setDetalles(new ArrayList<>());
        assertNotNull(ordenResponse.getDetalles());
        ordenResponse.setPagos(new ArrayList<>());
        assertNotNull(ordenResponse.getPagos());
    }

}