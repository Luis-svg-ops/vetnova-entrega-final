package cl.vetnova.facturacion.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class RutValidatorTest {

    @Test
    void testRutValidoConDigitoVerificador() {
        assertTrue(RutValidator.esValido("11.111.111-1"));
    }

    @Test
    void testRutValidoConDvK() {
        assertTrue(RutValidator.esValido("12.345.670-K"));
    }

    @Test
    void testRutValidoConDvCero() {
        assertTrue(RutValidator.esValido("76.000.000-0"));
    }

    @Test
    void testRutNulo() {
        assertFalse(RutValidator.esValido(null));
    }

    @Test
    void testRutDemasiadoCorto() {
        assertFalse(RutValidator.esValido("5"));
    }

    @Test
    void testRutConCuerpoNoNumerico() {
        assertFalse(RutValidator.esValido("ABCD-1"));
    }

    @Test
    void testRutConDigitoVerificadorIncorrecto() {
        assertFalse(RutValidator.esValido("12345678-X"));
    }
}
