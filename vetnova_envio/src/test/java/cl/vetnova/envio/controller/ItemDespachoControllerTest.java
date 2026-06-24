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

import cl.vetnova.envio.model.ItemDespacho;
import cl.vetnova.envio.service.ItemDespachoService;

@WebMvcTest(ItemDespachoController.class)
public class ItemDespachoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemDespachoService itemDespachoService;

    @Test
    void testEndpointsCrud() throws Exception {
        when(itemDespachoService.listar()).thenReturn(List.of(new ItemDespacho()));
        when(itemDespachoService.obtenerPorId(1L)).thenReturn(new ItemDespacho());
        when(itemDespachoService.crear(any(ItemDespacho.class))).thenReturn(new ItemDespacho());
        when(itemDespachoService.actualizar(eq(1L), any(ItemDespacho.class))).thenReturn(new ItemDespacho());

        mockMvc.perform(get("/api/v1/itemdespachos")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/itemdespachos/1")).andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/itemdespachos").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/api/v1/itemdespachos/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/v1/itemdespachos/1")).andExpect(status().isNoContent());
    }
}