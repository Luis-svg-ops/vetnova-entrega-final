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

import cl.vetnova.inventario.model.Inventario;
import cl.vetnova.inventario.service.InventarioService;

@WebMvcTest(InventarioController.class)
public class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventarioService inventarioService;

    @Test
    void testEndpointsCrud() throws Exception {
        when(inventarioService.listar()).thenReturn(List.of(new Inventario()));
        when(inventarioService.obtenerPorId(1L)).thenReturn(new Inventario());
        when(inventarioService.crear(any(Inventario.class))).thenReturn(new Inventario());
        when(inventarioService.actualizar(eq(1L), any(Inventario.class))).thenReturn(new Inventario());

        mockMvc.perform(get("/api/v1/inventarios")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/inventarios/1")).andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/inventarios").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/api/v1/inventarios/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/v1/inventarios/1")).andExpect(status().isNoContent());
    }

    @Test
    void testRegistrarEntrada() throws Exception {
        when(inventarioService.registrarEntrada(eq(1L), any(), any())).thenReturn(new Inventario());
        mockMvc.perform(post("/api/v1/inventarios/1/entrada").contentType(MediaType.APPLICATION_JSON)
                .content("{\"cantidad\":5,\"responsable\":\"Juan\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testRegistrarSalida() throws Exception {
        when(inventarioService.registrarSalida(eq(1L), any(), any())).thenReturn(new Inventario());
        mockMvc.perform(post("/api/v1/inventarios/1/salida").contentType(MediaType.APPLICATION_JSON)
                .content("{\"cantidad\":3,\"motivo\":\"Venta\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testAjustarStockMinimo() throws Exception {
        when(inventarioService.ajustarStockMinimo(eq(1L), any())).thenReturn(new Inventario());
        mockMvc.perform(put("/api/v1/inventarios/1/stock-minimo").contentType(MediaType.APPLICATION_JSON)
                .content("{\"minimo\":10}"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetStockTotal() throws Exception {
        when(inventarioService.getStockTotal(1L)).thenReturn(13);
        mockMvc.perform(get("/api/v1/inventarios/1/stock-total")).andExpect(status().isOk());
    }
}
