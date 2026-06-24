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

import cl.vetnova.fichaclinica.model.Procedimiento;
import cl.vetnova.fichaclinica.service.ProcedimientoService;

@WebMvcTest(ProcedimientoController.class)
public class ProcedimientoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProcedimientoService procedimientoService;

    @Test
    void testCrearYListar() throws Exception {
        when(procedimientoService.crear(any(Procedimiento.class))).thenReturn(new Procedimiento());
        when(procedimientoService.listar()).thenReturn(List.of(new Procedimiento()));
        when(procedimientoService.listarPorFicha(1L)).thenReturn(List.of(new Procedimiento()));

        mockMvc.perform(post("/api/v1/procedimientos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"fichaId\":1,\"nombre\":\"Castración\",\"descripcion\":\"Ok\",\"veterinarioId\":2}"))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/v1/procedimientos")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/procedimientos").param("fichaId", "1")).andExpect(status().isOk());
    }

    @Test
    void testModificarYEliminarResponden405() throws Exception {
        mockMvc.perform(put("/api/v1/procedimientos/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(delete("/api/v1/procedimientos/1")).andExpect(status().isMethodNotAllowed());
    }
}
