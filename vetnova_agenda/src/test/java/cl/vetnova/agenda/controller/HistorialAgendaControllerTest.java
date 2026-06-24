package cl.vetnova.agenda.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.vetnova.agenda.model.HistorialAgenda;
import cl.vetnova.agenda.service.HistorialAgendaService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HistorialAgendaController.class)
public class HistorialAgendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HistorialAgendaService historialAgendaService;

    @Test
    void testCrudDeHistorial() throws Exception {
        when(historialAgendaService.crear(any())).thenReturn(new HistorialAgenda());
        when(historialAgendaService.listar()).thenReturn(List.of(new HistorialAgenda()));

        mockMvc.perform(post("/api/v1/historial").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"citaId\":1,\"mascotaId\":1,\"clienteId\":2,\"estado\":\"RESERVADA\",\"servicio\":\"Consulta\"}"))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/v1/historial")).andExpect(status().isOk());
        mockMvc.perform(delete("/api/v1/historial/1")).andExpect(status().isNoContent());
    }
}
