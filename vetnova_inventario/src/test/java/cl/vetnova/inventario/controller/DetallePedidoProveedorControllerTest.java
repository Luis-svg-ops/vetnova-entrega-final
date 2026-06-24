package cl.vetnova.inventario.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import cl.vetnova.inventario.dto.DetallePedidoRequest;
import cl.vetnova.inventario.model.DetallePedidoProveedor;
import cl.vetnova.inventario.service.DetallePedidoProveedorService;

@WebMvcTest(DetallePedidoProveedorController.class)
public class DetallePedidoProveedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DetallePedidoProveedorService detallePedidoProveedorService;

    @Test
    void testEndpointsCrud() throws Exception {
        when(detallePedidoProveedorService.listar()).thenReturn(List.of(new DetallePedidoProveedor()));
        when(detallePedidoProveedorService.obtenerPorId(1L)).thenReturn(new DetallePedidoProveedor());
        when(detallePedidoProveedorService.crear(any(DetallePedidoRequest.class))).thenReturn(new DetallePedidoProveedor());
        when(detallePedidoProveedorService.actualizar(eq(1L), any(DetallePedidoRequest.class))).thenReturn(new DetallePedidoProveedor());

        mockMvc.perform(get("/api/v1/detallepedidoproveedors")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/detallepedidoproveedors/1")).andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/detallepedidoproveedors").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/api/v1/detallepedidoproveedors/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/v1/detallepedidoproveedors/1")).andExpect(status().isOk());
    }
}
