package cl.vetnova.ventas.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import cl.vetnova.ventas.model.Pago;

public class PasarelaPagoSimuladaTest {

    private final PasarelaPagoSimulada pasarela = new PasarelaPagoSimulada();

    @Test
    void testAutorizaMontoPositivo() {
        Pago pago = new Pago();
        pago.setMonto(1000.0);
        assertTrue(pasarela.autorizar(pago));
    }

    @Test
    void testNoAutorizaMontoNuloOCero() {
        Pago sinMonto = new Pago();
        assertFalse(pasarela.autorizar(sinMonto));
        Pago cero = new Pago();
        cero.setMonto(0.0);
        assertFalse(pasarela.autorizar(cero));
    }
}
