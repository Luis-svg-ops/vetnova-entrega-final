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

import cl.vetnova.notificaciones.model.ConfiguracionAlerta;
import cl.vetnova.notificaciones.service.ConfiguracionAlertaService;

@WebMvcTest(ConfiguracionAlertaController.class)
public class ConfiguracionAlertaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConfiguracionAlertaService service;

    @Test
    void testListarYCrear() throws Exception {
        when(service.listar(1L)).thenReturn(List.of(new ConfiguracionAlerta()));
        when(service.crear(any(ConfiguracionAlerta.class))).thenReturn(new ConfiguracionAlerta());
        mockMvc.perform(get("/configuraciones-alerta").param("usuarioId", "1")).andExpect(status().isOk());
        mockMvc.perform(post("/configuraciones-alerta").contentType(MediaType.APPLICATION_JSON)
                .content("{\"usuarioId\":1,\"tipoEvento\":\"CITA_CONFIRMADA\",\"canal\":\"EMAIL\"}")).andExpect(status().isCreated());
    }

    @Test
    void testDesactivarActivarEliminar() throws Exception {
        when(service.desactivar(1L)).thenReturn(new ConfiguracionAlerta());
        when(service.activar(1L)).thenReturn(new ConfiguracionAlerta());
        mockMvc.perform(put("/configuraciones-alerta/1/desactivar")).andExpect(status().isOk());
        mockMvc.perform(put("/configuraciones-alerta/1/activar")).andExpect(status().isOk());
        mockMvc.perform(delete("/configuraciones-alerta/1")).andExpect(status().isOk());
    }
}
