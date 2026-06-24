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

import cl.vetnova.fichaclinica.model.Evolucion;
import cl.vetnova.fichaclinica.service.EvolucionService;

@WebMvcTest(EvolucionController.class)
public class EvolucionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EvolucionService evolucionService;

    @Test
    void testCrearYListar() throws Exception {
        when(evolucionService.crear(any(Evolucion.class))).thenReturn(new Evolucion());
        when(evolucionService.listar()).thenReturn(List.of(new Evolucion()));
        when(evolucionService.listarPorFicha(1L)).thenReturn(List.of(new Evolucion()));

        mockMvc.perform(post("/api/v1/evoluciones").contentType(MediaType.APPLICATION_JSON)
                .content("{\"fichaId\":1,\"veterinarioId\":2,\"descripcion\":\"Estable\"}"))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/v1/evoluciones")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/evoluciones").param("fichaId", "1")).andExpect(status().isOk());
    }

    @Test
    void testModificarYEliminarResponden405() throws Exception {
        mockMvc.perform(put("/api/v1/evoluciones/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(delete("/api/v1/evoluciones/1")).andExpect(status().isMethodNotAllowed());
    }
}
