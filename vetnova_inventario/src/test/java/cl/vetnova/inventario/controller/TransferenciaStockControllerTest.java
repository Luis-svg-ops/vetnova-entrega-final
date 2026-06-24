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

import cl.vetnova.inventario.dto.CancelacionResponse;
import cl.vetnova.inventario.model.TransferenciaStock;
import cl.vetnova.inventario.service.TransferenciaStockService;

@WebMvcTest(TransferenciaStockController.class)
public class TransferenciaStockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransferenciaStockService transferenciaStockService;

    @Test
    void testEndpointsCrud() throws Exception {
        when(transferenciaStockService.listar()).thenReturn(List.of(new TransferenciaStock()));
        when(transferenciaStockService.obtenerPorId(1L)).thenReturn(new TransferenciaStock());
        when(transferenciaStockService.crear(any(TransferenciaStock.class))).thenReturn(new TransferenciaStock());
        when(transferenciaStockService.actualizar(eq(1L), any(TransferenciaStock.class))).thenReturn(new TransferenciaStock());

        mockMvc.perform(get("/api/v1/transferenciastocks")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/transferenciastocks/1")).andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/transferenciastocks").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/api/v1/transferenciastocks/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/v1/transferenciastocks/1")).andExpect(status().isNoContent());
    }

    @Test
    void testIniciar() throws Exception {
        when(transferenciaStockService.iniciar(1L)).thenReturn(new TransferenciaStock());
        mockMvc.perform(put("/api/v1/transferenciastocks/1/iniciar")).andExpect(status().isOk());
    }

    @Test
    void testConfirmar() throws Exception {
        when(transferenciaStockService.confirmarRecepcion(1L)).thenReturn(new TransferenciaStock());
        mockMvc.perform(put("/api/v1/transferenciastocks/1/confirmar")).andExpect(status().isOk());
    }

    @Test
    void testCancelar() throws Exception {
        when(transferenciaStockService.cancelar(1L)).thenReturn(new CancelacionResponse(new TransferenciaStock(), "Transferencia cancelada"));
        mockMvc.perform(put("/api/v1/transferenciastocks/1/cancelar")).andExpect(status().isOk());
    }
}
