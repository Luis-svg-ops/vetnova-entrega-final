package cl.vetnova.facturacion.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class FolioTest {

    @Test
    void testGettersYSetters() {
        Folio f = new Folio();
        f.setId(1L);
        assertEquals(1L, f.getId());
        f.setSucursal(1L);
        assertEquals(1L, f.getSucursal());
        f.setTipoDocumento("BOLETA");
        assertEquals("BOLETA", f.getTipoDocumento());
        f.setFolioDesde(1);
        assertEquals(1, f.getFolioDesde());
        f.setFolioHasta(100);
        assertEquals(100, f.getFolioHasta());
        f.setFolioActual(1);
        assertEquals(1, f.getFolioActual());
        f.setFoliosRestantes(100);
        assertEquals(100, f.getFoliosRestantes());
        f.setActivo(true);
        assertTrue(f.getActivo());
        f.setUmbral(10);
        assertEquals(10, f.getUmbral());
    }
}
