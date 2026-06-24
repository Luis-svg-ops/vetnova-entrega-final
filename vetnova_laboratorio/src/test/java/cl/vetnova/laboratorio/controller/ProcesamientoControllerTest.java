package cl.vetnova.laboratorio.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import cl.vetnova.laboratorio.dto.CompletarProcesamientoRequest;
import cl.vetnova.laboratorio.dto.CrearProcesamientoRequest;
import cl.vetnova.laboratorio.model.Procesamiento;
import cl.vetnova.laboratorio.service.ProcesamientoService;

@WebMvcTest(ProcesamientoController.class)
public class ProcesamientoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProcesamientoService service;

    @Test
    void testCrear() throws Exception {
        when(service.crear(any(CrearProcesamientoRequest.class))).thenReturn(new Procesamiento());
        mockMvc.perform(post("/procesamientos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"muestraId\":1,\"tecnicoId\":3,\"metodologia\":\"Citometría\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testIniciarYCompletar() throws Exception {
        when(service.iniciar(1L)).thenReturn(new Procesamiento());
        when(service.completar(eq(1L), any(CompletarProcesamientoRequest.class))).thenReturn(new Procesamiento());
        mockMvc.perform(put("/procesamientos/1/iniciar")).andExpect(status().isOk());
        mockMvc.perform(put("/procesamientos/1/completar").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());
    }
}
