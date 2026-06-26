package cl.vetnova.facturacion.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class ReporteTributarioTest {

    @Test
    void testGettersYSetters() {
        ReporteTributario r = new ReporteTributario();
        r.setId(1L);
        assertEquals(1L, r.getId());
        r.setSucursal("CHILLAN");
        assertEquals("CHILLAN", r.getSucursal());
        r.setPeriodo("2025-06");
        assertEquals("2025-06", r.getPeriodo());
        r.setTotalDocumentos(10);
        assertEquals(10, r.getTotalDocumentos());
        r.setMontoNeto(3500.0);
        assertEquals(3500.0, r.getMontoNeto());
        r.setMontoIva(665.0);
        assertEquals(665.0, r.getMontoIva());
        r.setMontoTotal(4165.0);
        assertEquals(4165.0, r.getMontoTotal());
        LocalDateTime ahora = LocalDateTime.now();
        r.setGeneradoEn(ahora);
        assertEquals(ahora, r.getGeneradoEn());
    }
}
