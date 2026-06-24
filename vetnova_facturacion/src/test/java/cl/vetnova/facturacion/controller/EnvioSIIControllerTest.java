package cl.vetnova.facturacion.controller;

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

import cl.vetnova.facturacion.dto.EnvioSIIRequest;
import cl.vetnova.facturacion.dto.ProcesarRespuestaSiiRequest;
import cl.vetnova.facturacion.model.EnvioSII;
import cl.vetnova.facturacion.service.EnvioSIIService;

@WebMvcTest(EnvioSIIController.class)
public class EnvioSIIControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EnvioSIIService service;

    @Test
    void testEnviar() throws Exception {
        when(service.enviar(any(EnvioSIIRequest.class))).thenReturn(new EnvioSII());
        mockMvc.perform(post("/envios-sii").contentType(MediaType.APPLICATION_JSON).content("{\"documentoId\":1}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testProcesarRespuestaYReintentar() throws Exception {
        when(service.procesarRespuesta(eq(1L), any(ProcesarRespuestaSiiRequest.class))).thenReturn(new EnvioSII());
        when(service.reintentar(1L)).thenReturn(new EnvioSII());
        mockMvc.perform(put("/envios-sii/1/procesar-respuesta").contentType(MediaType.APPLICATION_JSON).content("{\"codigo\":\"ACEPTADO\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/envios-sii/1/reintentar")).andExpect(status().isOk());
    }
}
