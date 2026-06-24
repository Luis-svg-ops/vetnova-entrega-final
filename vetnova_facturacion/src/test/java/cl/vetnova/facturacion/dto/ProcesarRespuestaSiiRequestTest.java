package cl.vetnova.facturacion.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ProcesarRespuestaSiiRequestTest {

    @Test
    void testGettersYSetters() {
        ProcesarRespuestaSiiRequest r = new ProcesarRespuestaSiiRequest();
        r.setCodigo("ACEPTADO");
        r.setDescripcion("Documento aceptado");
        assertEquals("ACEPTADO", r.getCodigo());
        assertEquals("Documento aceptado", r.getDescripcion());
    }
}
