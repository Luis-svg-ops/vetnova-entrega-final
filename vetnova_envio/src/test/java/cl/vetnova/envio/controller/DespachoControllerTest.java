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

import cl.vetnova.envio.model.Despacho;
import cl.vetnova.envio.model.ItemDespacho;
import cl.vetnova.envio.service.DespachoService;
import cl.vetnova.envio.service.ItemDespachoService;

@WebMvcTest(DespachoController.class)
public class DespachoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DespachoService despachoService;
    @MockBean
    private ItemDespachoService itemDespachoService;

    @Test
    void testEndpointsCrud() throws Exception {
        when(despachoService.listar()).thenReturn(List.of(new Despacho()));
        when(despachoService.obtenerPorId(1L)).thenReturn(new Despacho());
        when(despachoService.crear(any(Despacho.class))).thenReturn(new Despacho());
        when(despachoService.actualizar(eq(1L), any(Despacho.class))).thenReturn(new Despacho());

        mockMvc.perform(get("/api/v1/despachos")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/despachos/1")).andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/despachos").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/api/v1/despachos/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/v1/despachos/1")).andExpect(status().isNoContent());
    }

    @Test
    void testTransiciones() throws Exception {
        when(despachoService.iniciar(1L)).thenReturn(new Despacho());
        when(despachoService.enviar(1L)).thenReturn(new Despacho());
        when(despachoService.confirmarEntrega(1L)).thenReturn(new Despacho());
        when(despachoService.cancelar(1L)).thenReturn(new Despacho());
        when(despachoService.actualizarEstado(eq(1L), any())).thenReturn(new Despacho());

        mockMvc.perform(put("/api/v1/despachos/1/iniciar")).andExpect(status().isOk());
        mockMvc.perform(put("/api/v1/despachos/1/enviar")).andExpect(status().isOk());
        mockMvc.perform(put("/api/v1/despachos/1/entrega")).andExpect(status().isOk());
        mockMvc.perform(put("/api/v1/despachos/1/cancelar")).andExpect(status().isOk());
        mockMvc.perform(put("/api/v1/despachos/1/estado").contentType(MediaType.APPLICATION_JSON)
                .content("{\"estado\":\"PREPARANDO\"}")).andExpect(status().isOk());
    }

    @Test
    void testItems() throws Exception {
        when(itemDespachoService.agregarItem(eq(1L), any(ItemDespacho.class))).thenReturn(new ItemDespacho());

        mockMvc.perform(post("/api/v1/despachos/1/items").contentType(MediaType.APPLICATION_JSON)
                .content("{\"productoId\":10,\"cantidad\":2}")).andExpect(status().isCreated());
        mockMvc.perform(delete("/api/v1/despachos/1/items/5")).andExpect(status().isOk());
    }
}