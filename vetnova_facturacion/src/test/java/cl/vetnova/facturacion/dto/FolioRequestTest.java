package cl.vetnova.facturacion.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class FolioRequestTest {

    @Test
    void testGettersYSetters() {
        FolioRequest r = new FolioRequest();
        r.setSucursal(1L);
        r.setTipoDocumento("BOLETA");
        r.setFolioDesde(1);
        r.setFolioHasta(100);
        assertEquals(1L, r.getSucursal());
        assertEquals("BOLETA", r.getTipoDocumento());
        assertEquals(1, r.getFolioDesde());
        assertEquals(100, r.getFolioHasta());
    }
}
