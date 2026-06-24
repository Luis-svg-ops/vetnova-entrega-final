package cl.vetnova.reportes.controller;

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

import cl.vetnova.reportes.dto.ReporteRequest;
import cl.vetnova.reportes.model.Reporte;
import cl.vetnova.reportes.service.ReporteService;

@WebMvcTest(ReporteController.class)
public class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReporteService service;

    @Test
    void testListarYBuscar() throws Exception {
        when(service.listar()).thenReturn(List.of(new Reporte()));
        when(service.buscar(1L)).thenReturn(new Reporte());
        mockMvc.perform(get("/reportes")).andExpect(status().isOk());
        mockMvc.perform(get("/reportes/1")).andExpect(status().isOk());
    }

    @Test
    void testGenerar() throws Exception {
        when(service.generar(any(ReporteRequest.class))).thenReturn(new Reporte());
        mockMvc.perform(post("/reportes").contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipo\":\"VENTA\",\"sucursal\":1,\"desde\":\"2025-01-01\",\"hasta\":\"2025-06-01\",\"generadoPor\":1}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testExportar() throws Exception {
        when(service.exportar(eq(1L), any())).thenReturn(new Reporte());
        mockMvc.perform(get("/reportes/1/exportar").param("formato", "PDF")).andExpect(status().isOk());
    }
}
