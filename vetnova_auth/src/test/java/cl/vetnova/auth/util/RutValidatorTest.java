package cl.vetnova.auth.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class RutValidatorTest {

    @Test
    void testRutValidoConFormatoYDigitoCorrecto() {
        assertTrue(RutValidator.esValido("11.111.111-1"));
        assertTrue(RutValidator.esValido("111111111"));
    }

    @Test
    void testRutConDigitoVerificadorK() {
        // 40.000.000-K es válido por módulo 11 (suma=12, resto=10 -> K)
        assertTrue(RutValidator.esValido("40.000.000-K"));
    }

    @Test
    void testRutConDigitoVerificadorCero() {
        // 44.444.446-0 es válido (suma=132, múltiplo de 11, resto=11 -> 0)
        assertTrue(RutValidator.esValido("44.444.446-0"));
    }

    @Test
    void testRutInvalidoPorDigitoVerificador() {
        assertFalse(RutValidator.esValido("99999999-0"));
        assertFalse(RutValidator.esValido("12.345.678-9"));
    }

    @Test
    void testRutNullOFormatoInvalido() {
        assertFalse(RutValidator.esValido(null));
        assertFalse(RutValidator.esValido("abc"));
        assertFalse(RutValidator.esValido("123"));
    }
}
