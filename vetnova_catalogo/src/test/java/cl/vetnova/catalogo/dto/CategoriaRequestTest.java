package cl.vetnova.catalogo.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class CategoriaRequestTest {

    @Test
    void testCategoriaRequestExponeSusComponentes() {
        CategoriaRequest request = new CategoriaRequest("Alimentos", "Comida para mascotas", "PRODUCTO");

        assertEquals("Alimentos", request.nombre());
        assertEquals("Comida para mascotas", request.descripcion());
        assertEquals("PRODUCTO", request.tipo());
    }
}
