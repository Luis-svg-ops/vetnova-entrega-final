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

import cl.vetnova.envio.model.RutaDespacho;
import cl.vetnova.envio.service.RutaDespachoService;

@WebMvcTest(RutaDespachoController.class)
public class RutaDespachoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RutaDespachoService rutaDespachoService;

    @Test
    void testEndpointsCrud() throws Exception {
        when(rutaDespachoService.listar()).thenReturn(List.of(new RutaDespacho()));
        when(rutaDespachoService.obtenerPorId(1L)).thenReturn(new RutaDespacho());
        when(rutaDespachoService.crear(any(RutaDespacho.class))).thenReturn(new RutaDespacho());
        when(rutaDespachoService.actualizar(eq(1L), any(RutaDespacho.class))).thenReturn(new RutaDespacho());

        mockMvc.perform(get("/api/v1/rutadespachos")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/rutadespachos/1")).andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/rutadespachos").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/api/v1/rutadespachos/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/v1/rutadespachos/1")).andExpect(status().isNoContent());
    }

    @Test
    void testOptimizarYTiempo() throws Exception {
        when(rutaDespachoService.optimizar("1", "2")).thenReturn(new RutaDespacho());
        when(rutaDespachoService.calcularTiempoEstimado(1L)).thenReturn(45);

        mockMvc.perform(get("/api/v1/rutadespachos/optimizar").param("origen", "1").param("destino", "2"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/rutadespachos/1/tiempo")).andExpect(status().isOk());
    }
}