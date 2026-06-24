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

import cl.vetnova.reportes.model.Respaldo;
import cl.vetnova.reportes.service.RespaldoService;

@WebMvcTest(RespaldoController.class)
public class RespaldoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RespaldoService service;

    @Test
    void testEjecutar() throws Exception {
        when(service.ejecutar(any(Respaldo.class))).thenReturn(new Respaldo());
        mockMvc.perform(post("/respaldos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipo\":\"COMPLETO\",\"alcance\":\"TOTAL\",\"ejecutadoPor\":1,\"ubicacion\":\"/b\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testRestaurar() throws Exception {
        when(service.restaurar(1L)).thenReturn(new Respaldo());
        mockMvc.perform(post("/respaldos/1/restaurar")).andExpect(status().isOk());
    }
}
