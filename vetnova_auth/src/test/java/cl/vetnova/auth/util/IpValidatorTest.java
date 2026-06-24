package cl.vetnova.auth.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class IpValidatorTest {

    @Test
    void testIPv4Valida() {
        assertTrue(IpValidator.esValida("192.168.1.100"));
    }

    @Test
    void testIPv6Valida() {
        assertTrue(IpValidator.esValida("2001:0db8:85a3:0000:0000:8a2e:0370:7334"));
    }

    @Test
    void testIpInvalida() {
        assertFalse(IpValidator.esValida("999.999.999.999"));
        assertFalse(IpValidator.esValida("noesuna-ip"));
    }

    @Test
    void testIpNull() {
        assertFalse(IpValidator.esValida(null));
    }
}
