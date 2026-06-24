package cl.vetnova.laboratorio.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

import cl.vetnova.laboratorio.dto.TipoExamenRequest;
import cl.vetnova.laboratorio.model.TipoExamen;
import cl.vetnova.laboratorio.service.TipoExamenService;

@WebMvcTest(TipoExamenController.class)
public class TipoExamenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TipoExamenService service;

    @Test
    void testListarYBuscar() throws Exception {
        when(service.listar()).thenReturn(List.of(new TipoExamen()));
        when(service.buscarEntidad(1L)).thenReturn(new TipoExamen());
        mockMvc.perform(get("/tipos-examen")).andExpect(status().isOk());
        mockMvc.perform(get("/tipos-examen/1")).andExpect(status().isOk());
    }

    @Test
    void testCrearYActualizar() throws Exception {
        when(service.crear(any(TipoExamenRequest.class))).thenReturn(new TipoExamen());
        when(service.actualizar(eq(1L), any(TipoExamenRequest.class))).thenReturn(new TipoExamen());
        mockMvc.perform(post("/tipos-examen").contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"Hemograma\",\"tiempoEstimadoHoras\":2,\"requiereMuestra\":true}"))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/tipos-examen/1").contentType(MediaType.APPLICATION_JSON).content("{\"tiempoEstimadoHoras\":3}"))
                .andExpect(status().isOk());
    }

    @Test
    void testEliminar() throws Exception {
        mockMvc.perform(delete("/tipos-examen/1")).andExpect(status().isOk());
    }
}
