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

import cl.vetnova.notificaciones.model.Notificacion;
import cl.vetnova.notificaciones.service.NotificacionService;

@WebMvcTest(NotificacionController.class)
public class NotificacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificacionService service;

    @Test
    void testListarYContar() throws Exception {
        when(service.listar(any(), any())).thenReturn(List.of(new Notificacion()));
        when(service.contarNoLeidas(1L)).thenReturn(4L);
        mockMvc.perform(get("/notificaciones").param("usuarioId", "1")).andExpect(status().isOk());
        mockMvc.perform(get("/notificaciones").param("usuarioId", "1").param("leida", "false")).andExpect(status().isOk());
        mockMvc.perform(get("/notificaciones/no-leidas/count").param("usuarioId", "1")).andExpect(status().isOk());
    }

    @Test
    void testCrearLeerReenviar() throws Exception {
        when(service.crear(any(Notificacion.class))).thenReturn(new Notificacion());
        when(service.marcarLeida(1L)).thenReturn(new Notificacion());
        when(service.reenviar(1L)).thenReturn(new Notificacion());
        mockMvc.perform(post("/notificaciones").contentType(MediaType.APPLICATION_JSON)
                .content("{\"usuarioId\":1,\"tipo\":\"EMAIL\",\"mensaje\":\"m\"}")).andExpect(status().isCreated());
        mockMvc.perform(put("/notificaciones/1/leer")).andExpect(status().isOk());
        mockMvc.perform(post("/notificaciones/1/reenviar")).andExpect(status().isOk());
    }

    @Test
    void testModificarYEliminarResponden405() throws Exception {
        mockMvc.perform(put("/notificaciones/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(delete("/notificaciones/1")).andExpect(status().isMethodNotAllowed());
    }
}
