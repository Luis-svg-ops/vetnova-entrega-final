package cl.vetnova.facturacion.controller;

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

import cl.vetnova.facturacion.dto.ReporteRequest;
import cl.vetnova.facturacion.model.ReporteTributario;
import cl.vetnova.facturacion.service.ReporteTributarioService;

@WebMvcTest(ReporteTributarioController.class)
public class ReporteTributarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReporteTributarioService service;

    @Test
    void testListarYBuscar() throws Exception {
        when(service.listar()).thenReturn(List.of(new ReporteTributario()));
        when(service.buscar(1L)).thenReturn(new ReporteTributario());
        mockMvc.perform(get("/reportes-tributarios")).andExpect(status().isOk());
        mockMvc.perform(get("/reportes-tributarios/1")).andExpect(status().isOk());
    }

    @Test
    void testGenerar() throws Exception {
        when(service.generar(any(ReporteRequest.class))).thenReturn(new ReporteTributario());
        mockMvc.perform(post("/reportes-tributarios").contentType(MediaType.APPLICATION_JSON)
                .content("{\"sucursal\":1,\"periodo\":\"2025-06\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testExportar() throws Exception {
        when(service.exportar(eq(1L), any())).thenReturn(new ReporteTributario());
        mockMvc.perform(get("/reportes-tributarios/1/exportar").param("formato", "PDF")).andExpect(status().isOk());
    }
}
