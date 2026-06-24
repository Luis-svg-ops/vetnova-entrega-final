package cl.vetnova.notificaciones.controller;

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

import cl.vetnova.notificaciones.model.HistorialMensaje;
import cl.vetnova.notificaciones.service.HistorialMensajeService;

@WebMvcTest(HistorialMensajeController.class)
public class HistorialMensajeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HistorialMensajeService service;

    @Test
    void testListarYCrear() throws Exception {
        when(service.listar(any(), any(), any())).thenReturn(List.of(new HistorialMensaje()));
        when(service.crear(any(HistorialMensaje.class))).thenReturn(new HistorialMensaje());
        mockMvc.perform(get("/historial-mensajes").param("notificacionId", "1")).andExpect(status().isOk());
        mockMvc.perform(post("/historial-mensajes").contentType(MediaType.APPLICATION_JSON)
                .content("{\"notificacionId\":1,\"canalId\":1,\"estado\":\"ENVIADO\"}")).andExpect(status().isCreated());
    }

    @Test
    void testModificarYEliminarResponden405() throws Exception {
        mockMvc.perform(put("/historial-mensajes/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(delete("/historial-mensajes/1")).andExpect(status().isMethodNotAllowed());
    }
}
