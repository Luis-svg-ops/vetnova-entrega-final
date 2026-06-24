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

import cl.vetnova.fichaclinica.model.Vacuna;
import cl.vetnova.fichaclinica.service.VacunaService;

@WebMvcTest(VacunaController.class)
public class VacunaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VacunaService vacunaService;

    @Test
    void testCrearYListar() throws Exception {
        when(vacunaService.crear(any(Vacuna.class))).thenReturn(new Vacuna());
        when(vacunaService.listar()).thenReturn(List.of(new Vacuna()));
        when(vacunaService.listarPorFicha(1L)).thenReturn(List.of(new Vacuna()));

        mockMvc.perform(post("/api/v1/vacunas").contentType(MediaType.APPLICATION_JSON)
                .content("{\"fichaId\":1,\"nombre\":\"Rabia\",\"fechaAplicacion\":\"2025-06-01\"}"))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/v1/vacunas")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/vacunas").param("fichaId", "1")).andExpect(status().isOk());
    }

    @Test
    void testModificarYEliminarResponden405() throws Exception {
        mockMvc.perform(put("/api/v1/vacunas/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(delete("/api/v1/vacunas/1")).andExpect(status().isMethodNotAllowed());
    }
}
