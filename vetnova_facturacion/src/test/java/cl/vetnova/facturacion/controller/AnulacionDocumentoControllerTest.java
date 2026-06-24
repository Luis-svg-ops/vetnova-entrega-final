package cl.vetnova.facturacion.controller;

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

import cl.vetnova.facturacion.dto.AnulacionRequest;
import cl.vetnova.facturacion.model.AnulacionDocumento;
import cl.vetnova.facturacion.service.AnulacionDocumentoService;

@WebMvcTest(AnulacionDocumentoController.class)
public class AnulacionDocumentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnulacionDocumentoService service;

    @Test
    void testRegistrarYNotificar() throws Exception {
        when(service.registrar(any(AnulacionRequest.class))).thenReturn(new AnulacionDocumento());
        when(service.notificarSII(1L)).thenReturn(new AnulacionDocumento());
        mockMvc.perform(post("/anulaciones").contentType(MediaType.APPLICATION_JSON)
                .content("{\"documentoId\":1,\"administradorId\":1,\"motivo\":\"x\"}"))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/anulaciones/1/notificar-sii")).andExpect(status().isOk());
    }

    @Test
    void testModificarYEliminarResponden405() throws Exception {
        mockMvc.perform(put("/anulaciones/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(delete("/anulaciones/1")).andExpect(status().isMethodNotAllowed());
    }
}
