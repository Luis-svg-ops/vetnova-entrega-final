package cl.vetnova.reportes.controller;

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

import cl.vetnova.reportes.model.MonitorSistema;
import cl.vetnova.reportes.service.MonitorSistemaService;

@WebMvcTest(MonitorSistemaController.class)
public class MonitorSistemaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MonitorSistemaService service;

    @Test
    void testRegistrar() throws Exception {
        when(service.registrar(any(MonitorSistema.class))).thenReturn(new MonitorSistema());
        mockMvc.perform(post("/monitor").contentType(MediaType.APPLICATION_JSON).content("{\"microservicio\":\"MS1\",\"estado\":\"UP\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testHistorial() throws Exception {
        when(service.historial("MS1")).thenReturn(List.of(new MonitorSistema()));
        mockMvc.perform(get("/monitor/MS1/historial")).andExpect(status().isOk());
    }
}
