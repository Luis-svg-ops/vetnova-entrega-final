package cl.vetnova.catalogo.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ServicioRequestTest {

    @Test
    void testServicioRequestExponeSusComponentes() {
        ServicioRequest request = new ServicioRequest("Consulta general", "Atención veterinaria", 25000.0, 30, true, 2L);

        assertEquals("Consulta general", request.nombre());
        assertEquals("Atención veterinaria", request.descripcion());
        assertEquals(25000.0, request.precio());
        assertEquals(30, request.duracionMinutos());
        assertTrue(request.activo());
        assertEquals(2L, request.categoriaId());
    }
}
