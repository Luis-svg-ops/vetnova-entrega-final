package cl.vetnova.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.vetnova.auth.dto.AuditoriaResponse;
import cl.vetnova.auth.dto.CrearAuditoriaRequest;
import cl.vetnova.auth.service.AuditoriaService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuditoriaController.class)
public class AuditoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditoriaService auditoriaService;

    private AuditoriaResponse registro() {
        return new AuditoriaResponse(1L, 1L, "LOGIN", "192.168.1.1", LocalDateTime.now(), true, null);
    }

    @Test
    void testCrearRegistroResponde201() throws Exception {
        when(auditoriaService.crear(any(CrearAuditoriaRequest.class))).thenReturn(registro());

        mockMvc.perform(post("/api/auditoria").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuarioId\":1,\"accion\":\"LOGIN\",\"ip\":\"192.168.1.1\",\"exitoso\":true}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testConsultarResponde200() throws Exception {
        when(auditoriaService.consultar(any(), any())).thenReturn(List.of(registro()));

        mockMvc.perform(get("/api/auditoria").param("usuarioId", "1")).andExpect(status().isOk());
    }

    @Test
    void testModificarRegistroResponde405() throws Exception {
        mockMvc.perform(put("/api/auditoria/1")).andExpect(status().isMethodNotAllowed());
    }

    @Test
    void testEliminarRegistroResponde405() throws Exception {
        mockMvc.perform(delete("/api/auditoria/1")).andExpect(status().isMethodNotAllowed());
    }
}
