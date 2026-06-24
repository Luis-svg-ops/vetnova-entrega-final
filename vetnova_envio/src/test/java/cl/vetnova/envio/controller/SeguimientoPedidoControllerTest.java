package cl.vetnova.envio.controller;

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

import cl.vetnova.envio.model.RegistroSeguimiento;
import cl.vetnova.envio.model.SeguimientoPedido;
import cl.vetnova.envio.service.SeguimientoPedidoService;

@WebMvcTest(SeguimientoPedidoController.class)
public class SeguimientoPedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SeguimientoPedidoService seguimientoPedidoService;

    @Test
    void testEndpointsCrud() throws Exception {
        when(seguimientoPedidoService.listar()).thenReturn(List.of(new SeguimientoPedido()));
        when(seguimientoPedidoService.obtenerPorId(1L)).thenReturn(new SeguimientoPedido());
        when(seguimientoPedidoService.crear(any(SeguimientoPedido.class))).thenReturn(new SeguimientoPedido());
        when(seguimientoPedidoService.actualizar(eq(1L), any(SeguimientoPedido.class))).thenReturn(new SeguimientoPedido());

        mockMvc.perform(get("/api/v1/seguimientopedidos")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/seguimientopedidos/1")).andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/seguimientopedidos").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/api/v1/seguimientopedidos/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/v1/seguimientopedidos/1")).andExpect(status().isNoContent());
    }

    @Test
    void testEstadoEHistorial() throws Exception {
        when(seguimientoPedidoService.actualizarEstado(eq(1L), any(), any())).thenReturn(new SeguimientoPedido());
        when(seguimientoPedidoService.getHistorial(1L)).thenReturn(List.of(new RegistroSeguimiento()));

        mockMvc.perform(put("/api/v1/seguimientopedidos/1/estado").contentType(MediaType.APPLICATION_JSON)
                .content("{\"estado\":\"PREPARANDO\",\"observacion\":\"Preparando\"}")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/seguimientopedidos/1/historial")).andExpect(status().isOk());
    }
}