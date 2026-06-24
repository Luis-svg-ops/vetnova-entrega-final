package cl.vetnova.notificaciones.controller;

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

import cl.vetnova.notificaciones.model.CanalNotificacion;
import cl.vetnova.notificaciones.service.CanalNotificacionService;

@WebMvcTest(CanalNotificacionController.class)
public class CanalNotificacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CanalNotificacionService service;

    @Test
    void testListar() throws Exception {
        when(service.listar(1L)).thenReturn(List.of(new CanalNotificacion()));
        mockMvc.perform(get("/canales").param("usuarioId", "1")).andExpect(status().isOk());
    }

    @Test
    void testCrearActualizarDesactivar() throws Exception {
        when(service.crear(any(CanalNotificacion.class))).thenReturn(new CanalNotificacion());
        when(service.actualizar(eq(1L), any(CanalNotificacion.class))).thenReturn(new CanalNotificacion());
        when(service.desactivar(1L)).thenReturn(new CanalNotificacion());
        mockMvc.perform(post("/canales").contentType(MediaType.APPLICATION_JSON)
                .content("{\"usuarioId\":1,\"tipo\":\"SMS\",\"destino\":\"+56912345678\"}")).andExpect(status().isCreated());
        mockMvc.perform(put("/canales/1").contentType(MediaType.APPLICATION_JSON).content("{\"destino\":\"x@y.com\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/canales/1/desactivar")).andExpect(status().isOk());
    }

    @Test
    void testEliminar() throws Exception {
        mockMvc.perform(delete("/canales/1")).andExpect(status().isOk());
    }
}
