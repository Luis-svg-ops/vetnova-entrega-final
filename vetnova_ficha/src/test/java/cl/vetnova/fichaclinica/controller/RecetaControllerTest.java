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

import cl.vetnova.fichaclinica.dto.RecetaRequest;
import cl.vetnova.fichaclinica.model.Receta;
import cl.vetnova.fichaclinica.service.RecetaService;

@WebMvcTest(RecetaController.class)
public class RecetaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecetaService recetaService;

    @Test
    void testCrearYListar() throws Exception {
        when(recetaService.crear(any(RecetaRequest.class))).thenReturn(new Receta());
        when(recetaService.listar()).thenReturn(List.of(new Receta()));
        when(recetaService.listarPorFicha(1L)).thenReturn(List.of(new Receta()));

        mockMvc.perform(post("/api/v1/recetas").contentType(MediaType.APPLICATION_JSON)
                .content("{\"fichaId\":1,\"veterinarioId\":2,\"medicamentos\":[{\"nombre\":\"Amox\",\"dosis\":\"1\",\"frecuencia\":\"8h\"}]}"))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/v1/recetas")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/recetas").param("fichaId", "1")).andExpect(status().isOk());
    }

    @Test
    void testModificarYEliminarResponden405() throws Exception {
        mockMvc.perform(put("/api/v1/recetas/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(delete("/api/v1/recetas/1")).andExpect(status().isMethodNotAllowed());
    }
}
