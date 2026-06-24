package cl.vetnova.reportes.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import cl.vetnova.reportes.model.IncidenteSistema;
import cl.vetnova.reportes.service.IncidenteSistemaService;

@WebMvcTest(IncidenteSistemaController.class)
public class IncidenteSistemaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IncidenteSistemaService service;

    @Test
    void testRegistrar() throws Exception {
        when(service.registrar(any(IncidenteSistema.class))).thenReturn(new IncidenteSistema());
        mockMvc.perform(post("/incidentes").contentType(MediaType.APPLICATION_JSON)
                .content("{\"microservicio\":\"MS1\",\"tipo\":\"DOWN\",\"severidad\":\"CRITICA\",\"descripcion\":\"x\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testNotificarYResolver() throws Exception {
        when(service.notificarAdministrador(1L)).thenReturn(new IncidenteSistema());
        when(service.resolver(1L)).thenReturn(new IncidenteSistema());
        mockMvc.perform(post("/incidentes/1/notificar")).andExpect(status().isOk());
        mockMvc.perform(put("/incidentes/1/resolver")).andExpect(status().isOk());
    }
}
