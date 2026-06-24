package cl.vetnova.fichaclinica.model;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.Date;
import org.junit.jupiter.api.Test;

public class CertificadoTest {

    @Test
    void testCertificado() {
        Certificado certificado = new Certificado();
        certificado.setId(1L);
        assertEquals(1L, certificado.getId());
        certificado.setFichaId(1L);
        assertEquals(1L, certificado.getFichaId());
        certificado.setVeterinarioId(1L);
        assertEquals(1L, certificado.getVeterinarioId());
        certificado.setMascotaId(1L);
        assertEquals(1L, certificado.getMascotaId());
        certificado.setTipo("x");
        assertEquals("x", certificado.getTipo());
        certificado.setContenido("x");
        assertEquals("x", certificado.getContenido());
        certificado.setFechaEmision(Date.valueOf("2025-01-01"));
        assertEquals(Date.valueOf("2025-01-01"), certificado.getFechaEmision());
        certificado.setFechaVencimiento(Date.valueOf("2025-12-31"));
        assertEquals(Date.valueOf("2025-12-31"), certificado.getFechaVencimiento());
    }

}