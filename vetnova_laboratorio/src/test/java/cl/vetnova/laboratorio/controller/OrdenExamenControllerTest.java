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

import cl.vetnova.laboratorio.dto.CancelarOrdenRequest;
import cl.vetnova.laboratorio.dto.CrearOrdenExamenRequest;
import cl.vetnova.laboratorio.dto.ProgramarOrdenRequest;
import cl.vetnova.laboratorio.model.OrdenExamen;
import cl.vetnova.laboratorio.service.OrdenExamenService;

@WebMvcTest(OrdenExamenController.class)
public class OrdenExamenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrdenExamenService service;

    @Test
    void testListarYBuscar() throws Exception {
        when(service.listar(any())).thenReturn(List.of(new OrdenExamen()));
        when(service.buscar(1L)).thenReturn(new OrdenExamen());
        mockMvc.perform(get("/ordenes-examen")).andExpect(status().isOk());
        mockMvc.perform(get("/ordenes-examen").param("mascotaId", "1")).andExpect(status().isOk());
        mockMvc.perform(get("/ordenes-examen/1")).andExpect(status().isOk());
    }

    @Test
    void testCrear() throws Exception {
        when(service.crear(any(CrearOrdenExamenRequest.class))).thenReturn(new OrdenExamen());
        mockMvc.perform(post("/ordenes-examen").contentType(MediaType.APPLICATION_JSON)
                .content("{\"mascotaId\":1,\"veterinarioId\":2,\"tipoExamenId\":1}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testProgramarYCancelar() throws Exception {
        when(service.programar(eq(1L), any(ProgramarOrdenRequest.class))).thenReturn(new OrdenExamen());
        when(service.cancelar(eq(1L), any(CancelarOrdenRequest.class))).thenReturn(new OrdenExamen());
        mockMvc.perform(put("/ordenes-examen/1/programar").contentType(MediaType.APPLICATION_JSON).content("{\"fechaProgramada\":\"2030-07-01T10:00:00\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/ordenes-examen/1/cancelar").contentType(MediaType.APPLICATION_JSON).content("{\"motivo\":\"x\"}"))
                .andExpect(status().isOk());
    }
}
