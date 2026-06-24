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

import cl.vetnova.inventario.dto.PedidoProveedorRequest;
import cl.vetnova.inventario.model.PedidoProveedor;
import cl.vetnova.inventario.service.PedidoProveedorService;

@WebMvcTest(PedidoProveedorController.class)
public class PedidoProveedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoProveedorService pedidoProveedorService;

    @Test
    void testEndpointsCrud() throws Exception {
        when(pedidoProveedorService.listar()).thenReturn(List.of(new PedidoProveedor()));
        when(pedidoProveedorService.obtenerPorId(1L)).thenReturn(new PedidoProveedor());
        when(pedidoProveedorService.crear(any(PedidoProveedorRequest.class))).thenReturn(new PedidoProveedor());
        when(pedidoProveedorService.actualizar(eq(1L), any(PedidoProveedor.class))).thenReturn(new PedidoProveedor());

        mockMvc.perform(get("/api/v1/pedidoproveedors")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/pedidoproveedors/1")).andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/pedidoproveedors").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/api/v1/pedidoproveedors/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/v1/pedidoproveedors/1")).andExpect(status().isNoContent());
    }

    @Test
    void testTransiciones() throws Exception {
        when(pedidoProveedorService.enviar(1L)).thenReturn(new PedidoProveedor());
        when(pedidoProveedorService.recibir(1L)).thenReturn(new PedidoProveedor());
        when(pedidoProveedorService.cancelar(1L)).thenReturn(new PedidoProveedor());

        mockMvc.perform(put("/api/v1/pedidoproveedors/1/enviar")).andExpect(status().isOk());
        mockMvc.perform(put("/api/v1/pedidoproveedors/1/recibir")).andExpect(status().isOk());
        mockMvc.perform(put("/api/v1/pedidoproveedors/1/cancelar")).andExpect(status().isOk());
    }
}
