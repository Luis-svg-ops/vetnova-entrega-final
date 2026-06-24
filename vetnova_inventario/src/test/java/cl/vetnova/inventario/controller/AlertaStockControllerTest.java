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

import cl.vetnova.inventario.dto.AlertaLeidaResponse;
import cl.vetnova.inventario.model.AlertaStock;
import cl.vetnova.inventario.service.AlertaStockService;

@WebMvcTest(AlertaStockController.class)
public class AlertaStockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlertaStockService alertaStockService;

    @Test
    void testEndpointsCrud() throws Exception {
        when(alertaStockService.listar()).thenReturn(List.of(new AlertaStock()));
        when(alertaStockService.obtenerPorId(1L)).thenReturn(new AlertaStock());
        when(alertaStockService.crear(any(AlertaStock.class))).thenReturn(new AlertaStock());
        when(alertaStockService.actualizar(eq(1L), any(AlertaStock.class))).thenReturn(new AlertaStock());

        mockMvc.perform(get("/api/v1/alertastocks")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/alertastocks/1")).andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/alertastocks").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/api/v1/alertastocks/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/v1/alertastocks/1")).andExpect(status().isNoContent());
    }

    @Test
    void testMarcarLeida() throws Exception {
        when(alertaStockService.marcarLeida(1L)).thenReturn(new AlertaLeidaResponse(new AlertaStock(), "Alerta marcada como leída"));
        mockMvc.perform(put("/api/v1/alertastocks/1/leer")).andExpect(status().isOk());
    }
}