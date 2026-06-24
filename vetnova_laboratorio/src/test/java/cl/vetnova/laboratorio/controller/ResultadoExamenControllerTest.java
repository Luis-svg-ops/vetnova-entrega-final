package cl.vetnova.laboratorio.controller;

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

import cl.vetnova.laboratorio.dto.RegistrarResultadoRequest;
import cl.vetnova.laboratorio.model.ResultadoExamen;
import cl.vetnova.laboratorio.service.ResultadoExamenService;

@WebMvcTest(ResultadoExamenController.class)
public class ResultadoExamenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResultadoExamenService service;

    @Test
    void testRegistrar() throws Exception {
        when(service.registrar(any(RegistrarResultadoRequest.class))).thenReturn(new ResultadoExamen());
        mockMvc.perform(post("/resultados-examen").contentType(MediaType.APPLICATION_JSON)
                .content("{\"ordenExamenId\":1,\"tecnicoId\":3,\"resultado\":\"Normal\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testPublicar() throws Exception {
        when(service.publicar(1L)).thenReturn(new ResultadoExamen());
        mockMvc.perform(put("/resultados-examen/1/publicar")).andExpect(status().isOk());
    }
}
