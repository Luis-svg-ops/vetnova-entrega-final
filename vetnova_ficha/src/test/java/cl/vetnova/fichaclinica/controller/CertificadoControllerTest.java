package cl.vetnova.fichaclinica.controller;

import static org.mockito.ArgumentMatchers.any;
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

import cl.vetnova.fichaclinica.model.Certificado;
import cl.vetnova.fichaclinica.service.CertificadoService;

@WebMvcTest(CertificadoController.class)
public class CertificadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CertificadoService certificadoService;

    @Test
    void testCrearYListar() throws Exception {
        when(certificadoService.crear(any(Certificado.class))).thenReturn(new Certificado());
        when(certificadoService.listar()).thenReturn(List.of(new Certificado()));
        when(certificadoService.listarPorFicha(1L)).thenReturn(List.of(new Certificado()));

        mockMvc.perform(post("/api/v1/certificados").contentType(MediaType.APPLICATION_JSON)
                .content("{\"fichaId\":1,\"veterinarioId\":2,\"tipo\":\"SALUD\"}"))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/v1/certificados")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/certificados").param("fichaId", "1")).andExpect(status().isOk());
    }

    @Test
    void testModificarYEliminarResponden405() throws Exception {
        mockMvc.perform(put("/api/v1/certificados/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(delete("/api/v1/certificados/1")).andExpect(status().isMethodNotAllowed());
    }
}
