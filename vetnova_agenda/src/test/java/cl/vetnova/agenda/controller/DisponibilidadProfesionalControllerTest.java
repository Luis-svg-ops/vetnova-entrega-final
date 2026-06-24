package cl.vetnova.agenda.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.vetnova.agenda.model.DisponibilidadProfesional;
import cl.vetnova.agenda.service.DisponibilidadProfesionalService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DisponibilidadProfesionalController.class)
public class DisponibilidadProfesionalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DisponibilidadProfesionalService disponibilidadService;

    @Test
    void testCrudDeDisponibilidad() throws Exception {
        when(disponibilidadService.crear(any())).thenReturn(new DisponibilidadProfesional());
        when(disponibilidadService.listar()).thenReturn(List.of(new DisponibilidadProfesional()));
        when(disponibilidadService.obtenerPorId(1L)).thenReturn(new DisponibilidadProfesional());
        when(disponibilidadService.actualizar(eq(1L), any())).thenReturn(new DisponibilidadProfesional());
        when(disponibilidadService.activar(1L)).thenReturn(new DisponibilidadProfesional());
        when(disponibilidadService.desactivar(1L)).thenReturn(new DisponibilidadProfesional());

        mockMvc.perform(post("/api/v1/disponibilidad").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"veterinarioId\":4,\"diaSemana\":\"LUNES\",\"horaInicio\":\"09:00\",\"horaFin\":\"18:00\",\"sucursal\":\"SANTIAGO\"}"))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/v1/disponibilidad")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/disponibilidad/1")).andExpect(status().isOk());
        mockMvc.perform(put("/api/v1/disponibilidad/1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"horaInicio\":\"11:00\"}")).andExpect(status().isOk());
        mockMvc.perform(put("/api/v1/disponibilidad/1/activar")).andExpect(status().isOk());
        mockMvc.perform(put("/api/v1/disponibilidad/1/desactivar")).andExpect(status().isOk());
        mockMvc.perform(delete("/api/v1/disponibilidad/1")).andExpect(status().isOk());
    }
}
