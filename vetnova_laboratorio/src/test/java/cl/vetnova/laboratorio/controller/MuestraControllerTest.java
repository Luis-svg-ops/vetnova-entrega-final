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

import cl.vetnova.laboratorio.dto.ActualizarEstadoMuestraRequest;
import cl.vetnova.laboratorio.dto.RecepcionMuestraRequest;
import cl.vetnova.laboratorio.dto.RegistrarMuestraRequest;
import cl.vetnova.laboratorio.model.Muestra;
import cl.vetnova.laboratorio.service.MuestraService;

@WebMvcTest(MuestraController.class)
public class MuestraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MuestraService service;

    @Test
    void testCrear() throws Exception {
        when(service.crear(any(RegistrarMuestraRequest.class))).thenReturn(new Muestra());
        mockMvc.perform(post("/muestras").contentType(MediaType.APPLICATION_JSON)
                .content("{\"ordenExamenId\":1,\"tipo\":\"SANGRE\",\"codigoMuestra\":\"M-001\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testRecepcionYEstado() throws Exception {
        when(service.registrarRecepcion(eq(1L), any(RecepcionMuestraRequest.class))).thenReturn(new Muestra());
        when(service.actualizarEstado(eq(1L), any(ActualizarEstadoMuestraRequest.class))).thenReturn(new Muestra());
        mockMvc.perform(put("/muestras/1/recepcion").contentType(MediaType.APPLICATION_JSON).content("{\"responsableId\":3}"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/muestras/1/estado").contentType(MediaType.APPLICATION_JSON).content("{\"estado\":\"EN_PROCESO\"}"))
                .andExpect(status().isOk());
    }
}
