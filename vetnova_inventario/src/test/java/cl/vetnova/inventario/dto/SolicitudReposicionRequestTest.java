package cl.vetnova.inventario.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class SolicitudReposicionRequestTest {

    @Test
    void testSolicitudReposicionRequest() {
        SolicitudReposicionRequest request = new SolicitudReposicionRequest();
        request.setInventarioId(1L);
        assertEquals(1L, request.getInventarioId());
        request.setCantidadSolicitada(10);
        assertEquals(10, request.getCantidadSolicitada());
        request.setMotivo("falta stock");
        assertEquals("falta stock", request.getMotivo());
        request.setSolicitadoPor(2L);
        assertEquals(2L, request.getSolicitadoPor());
    }
}
