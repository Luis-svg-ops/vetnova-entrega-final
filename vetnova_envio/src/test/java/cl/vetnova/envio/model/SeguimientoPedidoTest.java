package cl.vetnova.envio.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class SeguimientoPedidoTest {

    @Test
    void testSeguimientoPedido() {
        SeguimientoPedido s = new SeguimientoPedido();
        s.setId(1L);
        assertEquals(1L, s.getId());
        s.setDespachoId(2L);
        assertEquals(2L, s.getDespachoId());
        s.setOrdenId(3L);
        assertEquals(3L, s.getOrdenId());
        s.setEstado("ENVIADO");
        assertEquals("ENVIADO", s.getEstado());
        LocalDateTime ahora = LocalDateTime.now();
        s.setFechaActualizacion(ahora);
        assertEquals(ahora, s.getFechaActualizacion());
        s.setDescripcion("El pedido salió a reparto");
        assertEquals("El pedido salió a reparto", s.getDescripcion());
    }
}
