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

import cl.vetnova.reportes.model.ReporteVenta;
import cl.vetnova.reportes.service.ReporteVentaService;

@WebMvcTest(ReporteVentaController.class)
public class ReporteVentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReporteVentaService service;

    @Test
    void testCrear() throws Exception {
        when(service.crear(any(ReporteVenta.class))).thenReturn(new ReporteVenta());
        mockMvc.perform(post("/reportes-venta").contentType(MediaType.APPLICATION_JSON).content("{\"reporteId\":1}"))
                .andExpect(status().isCreated());
    }
}
