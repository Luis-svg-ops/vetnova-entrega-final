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

import cl.vetnova.envio.model.GuiaDespacho;
import cl.vetnova.envio.service.GuiaDespachoService;

@WebMvcTest(GuiaDespachoController.class)
public class GuiaDespachoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GuiaDespachoService guiaDespachoService;

    @Test
    void testEndpointsCrud() throws Exception {
        when(guiaDespachoService.listar()).thenReturn(List.of(new GuiaDespacho()));
        when(guiaDespachoService.obtenerPorId(1L)).thenReturn(new GuiaDespacho());
        when(guiaDespachoService.crear(any(GuiaDespacho.class))).thenReturn(new GuiaDespacho());
        when(guiaDespachoService.actualizar(eq(1L), any(GuiaDespacho.class))).thenReturn(new GuiaDespacho());

        mockMvc.perform(get("/api/v1/guiadespachos")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/guiadespachos/1")).andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/guiadespachos").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/api/v1/guiadespachos/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/v1/guiadespachos/1")).andExpect(status().isNoContent());
    }
}