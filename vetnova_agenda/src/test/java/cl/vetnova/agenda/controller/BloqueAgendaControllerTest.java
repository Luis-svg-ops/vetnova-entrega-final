package cl.vetnova.agenda.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.vetnova.agenda.model.BloqueAgenda;
import cl.vetnova.agenda.service.BloqueAgendaService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BloqueAgendaController.class)
public class BloqueAgendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BloqueAgendaService bloqueAgendaService;

    @Test
    void testCrudDeBloques() throws Exception {
        when(bloqueAgendaService.crear(any())).thenReturn(new BloqueAgenda());
        when(bloqueAgendaService.listar()).thenReturn(List.of(new BloqueAgenda()));
        when(bloqueAgendaService.obtenerPorId(1L)).thenReturn(new BloqueAgenda());

        mockMvc.perform(post("/api/v1/bloques").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"veterinarioId\":4,\"fechaInicio\":\"2030-07-01T09:00:00\",\"fechaFin\":\"2030-07-01T17:00:00\","
                                + "\"motivo\":\"Vacaciones\",\"creadoPor\":2}"))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/v1/bloques")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/bloques/1")).andExpect(status().isOk());
        mockMvc.perform(delete("/api/v1/bloques/1")).andExpect(status().isOk());
    }
}
