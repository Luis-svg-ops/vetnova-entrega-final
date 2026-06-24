package cl.vetnova.ventas.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class OrdenTest {

    @Test
    void testOrden() {
        Orden orden = new Orden();
        orden.setId(1L);
        assertEquals(1L, orden.getId());
        orden.setClienteId(1L);
        assertEquals(1L, orden.getClienteId());
        orden.setIdSucursal(1L);
        assertEquals(1L, orden.getIdSucursal());
        orden.setEstado(EstadoOrden.PENDIENTE);
        assertEquals(EstadoOrden.PENDIENTE, orden.getEstado());
        orden.setSubtotal(1.0);
        assertEquals(1.0, orden.getSubtotal());
        orden.setImpuestos(1.0);
        assertEquals(1.0, orden.getImpuestos());
        orden.setTotal(1.0);
        assertEquals(1.0, orden.getTotal());
        orden.setFechaCreacion(LocalDateTime.now());
        assertNotNull(orden.getFechaCreacion());
        orden.setFechaConfirmacion(LocalDateTime.now());
        assertNotNull(orden.getFechaConfirmacion());
        orden.setDetalles(new ArrayList<>());
        assertNotNull(orden.getDetalles());
        orden.setPagos(new ArrayList<>());
        assertNotNull(orden.getPagos());
    }

}