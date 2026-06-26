package cl.vetnova.soporte.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class CrearTicketRequestTest {

    @Test
    void testGettersYSetters() {
        CrearTicketRequest r = new CrearTicketRequest();
        r.setClienteId(1L);
        r.setMotivo("Producto defectuoso");
        r.setDescripcion("El producto llegó roto");
        r.setSucursalId("LOS_ANGELES");
        assertEquals(1L, r.getClienteId());
        assertEquals("Producto defectuoso", r.getMotivo());
        assertEquals("El producto llegó roto", r.getDescripcion());
        assertEquals("LOS_ANGELES", r.getSucursalId());
    }
}
