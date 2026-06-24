package cl.vetnova.inventario.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class RutValidatorTest {

    @Test
    void testRutNullEsInvalido() {
        assertFalse(RutValidator.esValido(null));
    }

    @Test
    void testRutDemasiadoCortoEsInvalido() {
        assertFalse(RutValidator.esValido("1"));
    }

    @Test
    void testRutConCuerpoNoNumericoEsInvalido() {
        assertFalse(RutValidator.esValido("ABC.DEF-9"));
    }

    @Test
    void testRutValidoConDvNumerico() {
        assertTrue(RutValidator.esValido("11.111.111-1"));
    }

    @Test
    void testRutValidoConDvK() {
        assertTrue(RutValidator.esValido("40.000.000-K"));
    }

    @Test
    void testRutValidoConDvCero() {
        assertTrue(RutValidator.esValido("44.444.446-0"));
    }

    @Test
    void testRutDigitoVerificadorIncorrectoEsInvalido() {
        assertFalse(RutValidator.esValido("99999999-0"));
    }
}
