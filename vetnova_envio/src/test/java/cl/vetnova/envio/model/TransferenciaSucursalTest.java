package cl.vetnova.envio.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class TransferenciaSucursalTest {

    @Test
    void testTransferenciaSucursal() {
        TransferenciaSucursal transferenciaSucursal = new TransferenciaSucursal();
        transferenciaSucursal.setId(1L);
        assertEquals(1L, transferenciaSucursal.getId());
        transferenciaSucursal.setIdProducto(1L);
        assertEquals(1L, transferenciaSucursal.getIdProducto());
        transferenciaSucursal.setIdSucursalOrigen(1L);
        assertEquals(1L, transferenciaSucursal.getIdSucursalOrigen());
        transferenciaSucursal.setIdSucursalDestino(1L);
        assertEquals(1L, transferenciaSucursal.getIdSucursalDestino());
        transferenciaSucursal.setCantidad(1);
        assertEquals(1, transferenciaSucursal.getCantidad());
        transferenciaSucursal.setEstado("x");
        assertEquals("x", transferenciaSucursal.getEstado());
        transferenciaSucursal.setObservacion("x");
        assertEquals("x", transferenciaSucursal.getObservacion());
        transferenciaSucursal.setFecha(LocalDateTime.now());
        assertNotNull(transferenciaSucursal.getFecha());
    }

}