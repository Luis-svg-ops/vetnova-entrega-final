package cl.vetnova.facturacion.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class DocumentoTributarioTest {

    @Test
    void testGettersYSetters() {
        DocumentoTributario d = new DocumentoTributario();
        d.setId(1L);
        assertEquals(1L, d.getId());
        d.setOrdenId(2L);
        assertEquals(2L, d.getOrdenId());
        d.setClienteId(3L);
        assertEquals(3L, d.getClienteId());
        d.setTipo("BOLETA");
        assertEquals("BOLETA", d.getTipo());
        d.setFolio("5");
        assertEquals("5", d.getFolio());
        d.setNeto(1000.0);
        assertEquals(1000.0, d.getNeto());
        d.setIva(190.0);
        assertEquals(190.0, d.getIva());
        d.setTotal(1190.0);
        assertEquals(1190.0, d.getTotal());
        d.setEstadoSII("EMITIDO");
        assertEquals("EMITIDO", d.getEstadoSII());
        d.setRutEmisor("76.000.000-0");
        assertEquals("76.000.000-0", d.getRutEmisor());
        d.setRutReceptor("11.111.111-1");
        assertEquals("11.111.111-1", d.getRutReceptor());
        d.setSucursal("CHILLAN");
        assertEquals("CHILLAN", d.getSucursal());
        LocalDateTime ahora = LocalDateTime.now();
        d.setFechaEmision(ahora);
        assertEquals(ahora, d.getFechaEmision());
    }
}
