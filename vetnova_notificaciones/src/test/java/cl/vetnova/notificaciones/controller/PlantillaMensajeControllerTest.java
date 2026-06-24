package cl.vetnova.notificaciones.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import cl.vetnova.notificaciones.model.PlantillaMensaje;
import cl.vetnova.notificaciones.service.PlantillaMensajeService;

@WebMvcTest(PlantillaMensajeController.class)
public class PlantillaMensajeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlantillaMensajeService service;

    @Test
    void testCrearActualizar() throws Exception {
        when(service.crear(any(PlantillaMensaje.class))).thenReturn(new PlantillaMensaje());
        when(service.actualizar(eq(1L), any(PlantillaMensaje.class))).thenReturn(new PlantillaMensaje());
        mockMvc.perform(post("/plantillas").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Test\",\"tipo\":\"EMAIL\",\"contenido\":\"Hola {{nombre}}\"}")).andExpect(status().isCreated());
        mockMvc.perform(put("/plantillas/1").contentType(MediaType.APPLICATION_JSON).content("{\"contenido\":\"x\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testRenderizarYEliminar() throws Exception {
        when(service.renderizar(eq(1L), any())).thenReturn("Hola Juan");
        mockMvc.perform(post("/plantillas/1/renderizar").contentType(MediaType.APPLICATION_JSON)
                .content("{\"valores\":{\"nombre\":\"Juan\"}}")).andExpect(status().isOk());
        mockMvc.perform(delete("/plantillas/1")).andExpect(status().isOk());
    }
}
