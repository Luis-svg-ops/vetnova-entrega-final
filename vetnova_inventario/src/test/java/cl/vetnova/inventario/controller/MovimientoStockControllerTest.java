package cl.vetnova.inventario.controller;

import static org.mockito.ArgumentMatchers.any;
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

import cl.vetnova.inventario.dto.MovimientoStockRequest;
import cl.vetnova.inventario.dto.MovimientoStockResponse;
import cl.vetnova.inventario.model.MovimientoStock;
import cl.vetnova.inventario.service.MovimientoStockService;

@WebMvcTest(MovimientoStockController.class)
public class MovimientoStockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovimientoStockService movimientoStockService;

    @Test
    void testRegistrarDevuelve201() throws Exception {
        when(movimientoStockService.registrar(any(MovimientoStockRequest.class))).thenReturn(new MovimientoStockResponse());
        mockMvc.perform(post("/api/v1/movimientos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"inventarioId\":1,\"tipo\":\"ENTRADA\",\"cantidad\":5,\"motivo\":\"Compra\",\"responsable\":\"Juan\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testListarYObtener() throws Exception {
        when(movimientoStockService.listar()).thenReturn(List.of(new MovimientoStock()));
        when(movimientoStockService.obtenerPorId(1L)).thenReturn(new MovimientoStock());
        mockMvc.perform(get("/api/v1/movimientos")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/movimientos/1")).andExpect(status().isOk());
    }

    @Test
    void testActualizarDevuelve405() throws Exception {
        mockMvc.perform(put("/api/v1/movimientos/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void testEliminarDevuelve405() throws Exception {
        mockMvc.perform(delete("/api/v1/movimientos/1")).andExpect(status().isMethodNotAllowed());
    }
}
