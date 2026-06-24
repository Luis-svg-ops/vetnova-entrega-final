package cl.vetnova.inventario.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class SolicitudReposicionTest {

    @Test
    void testSolicitudReposicion() {
        LocalDateTime t = LocalDateTime.now();
        SolicitudReposicion solicitud = new SolicitudReposicion();
        solicitud.setId(1L);
        assertEquals(1L, solicitud.getId());
        solicitud.setInventarioId(2L);
        assertEquals(2L, solicitud.getInventarioId());
        solicitud.setCantidadSolicitada(10);
        assertEquals(10, solicitud.getCantidadSolicitada());
        solicitud.setMotivo("reposición");
        assertEquals("reposición", solicitud.getMotivo());
        solicitud.setEstado("pendiente");
        assertEquals("pendiente", solicitud.getEstado());
        solicitud.setSolicitadoPor(3L);
        assertEquals(3L, solicitud.getSolicitadoPor());
        solicitud.setAprobadoPor(4L);
        assertEquals(4L, solicitud.getAprobadoPor());
        solicitud.setMotivoRechazo("presupuesto");
        assertEquals("presupuesto", solicitud.getMotivoRechazo());
        solicitud.setFechaSolicitud(t);
        assertEquals(t, solicitud.getFechaSolicitud());
        solicitud.setFechaResolucion(t);
        assertEquals(t, solicitud.getFechaResolucion());
    }
}
