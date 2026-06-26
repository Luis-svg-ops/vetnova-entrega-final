package cl.vetnova.facturacion.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class DocumentoTributarioRequestTest {

    @Test
    void testGettersYSetters() {
        DocumentoTributarioRequest r = new DocumentoTributarioRequest();
        r.setOrdenId(1L);
        r.setClienteId(2L);
        r.setTipo("BOLETA");
        r.setNeto(1000.0);
        r.setTotal(1190.0);
        r.setRutEmisor("76.000.000-0");
        r.setRutReceptor("11.111.111-1");
        r.setSucursal("CHILLAN");
        assertEquals(1L, r.getOrdenId());
        assertEquals(2L, r.getClienteId());
        assertEquals("BOLETA", r.getTipo());
        assertEquals(1000.0, r.getNeto());
        assertEquals(1190.0, r.getTotal());
        assertEquals("76.000.000-0", r.getRutEmisor());
        assertEquals("11.111.111-1", r.getRutReceptor());
        assertEquals("CHILLAN", r.getSucursal());
    }
}
