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

import cl.vetnova.reportes.model.ReporteAtencion;
import cl.vetnova.reportes.service.ReporteAtencionService;

@WebMvcTest(ReporteAtencionController.class)
public class ReporteAtencionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReporteAtencionService service;

    @Test
    void testCrear() throws Exception {
        when(service.crear(any(ReporteAtencion.class))).thenReturn(new ReporteAtencion());
        mockMvc.perform(post("/reportes-atencion").contentType(MediaType.APPLICATION_JSON).content("{\"reporteId\":1}"))
                .andExpect(status().isCreated());
    }
}
