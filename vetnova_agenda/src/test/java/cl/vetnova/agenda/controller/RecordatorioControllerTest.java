package cl.vetnova.agenda.controller;

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

import cl.vetnova.agenda.dto.RecordatorioRequest;
import cl.vetnova.agenda.model.Recordatorio;
import cl.vetnova.agenda.service.RecordatorioService;

@WebMvcTest(RecordatorioController.class)
public class RecordatorioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecordatorioService recordatorioService;

    @Test
    void testLecturaYCreacion() throws Exception {
        when(recordatorioService.listar()).thenReturn(List.of(new Recordatorio()));
        when(recordatorioService.obtenerPorId(1L)).thenReturn(new Recordatorio());
        when(recordatorioService.crear(any(RecordatorioRequest.class))).thenReturn(new Recordatorio());
        when(recordatorioService.reenviar(1L)).thenReturn(new Recordatorio());

        mockMvc.perform(get("/api/v1/recordatorios")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/recordatorios/1")).andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/recordatorios").contentType(MediaType.APPLICATION_JSON)
                .content("{\"citaId\":1,\"tipo\":\"EMAIL\"}")).andExpect(status().isCreated());
        mockMvc.perform(put("/api/v1/recordatorios/1/reenviar")).andExpect(status().isOk());
    }

    @Test
    void testModificarDevuelve405() throws Exception {
        mockMvc.perform(put("/api/v1/recordatorios/1").contentType(MediaType.APPLICATION_JSON)
                .content("{\"citaId\":1,\"tipo\":\"EMAIL\"}")).andExpect(status().isMethodNotAllowed());
    }

    @Test
    void testEliminarDevuelve405() throws Exception {
        mockMvc.perform(delete("/api/v1/recordatorios/1")).andExpect(status().isMethodNotAllowed());
    }
}
