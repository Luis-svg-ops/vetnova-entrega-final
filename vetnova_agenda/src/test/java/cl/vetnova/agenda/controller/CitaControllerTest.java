package cl.vetnova.agenda.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import cl.vetnova.agenda.dto.CitaResponse;
import cl.vetnova.agenda.exception.BusinessRuleException;
import cl.vetnova.agenda.exception.ResourceNotFoundException;
import cl.vetnova.agenda.model.Cita;
import cl.vetnova.agenda.service.CitaService;

@WebMvcTest(CitaController.class)
public class CitaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CitaService citaService;

    private Cita cita(String estado) {
        Cita c = new Cita();
        c.setClienteId(2L);
        c.setVeterinarioId(4L);
        c.setServicioId(3L);
        c.setEstado(estado);
        return c;
    }

    private CitaResponse response(String estado) {
        return new CitaResponse(cita(estado), null, null, null);
    }

    @Test
    void testCrearCitaValidaResponde201() throws Exception {
        when(citaService.crear(any())).thenReturn(cita("pendiente"));
        String body = "{\"clienteId\":2,\"veterinarioId\":4,\"servicioId\":3,\"sucursal\":\"SANTIAGO\","
                + "\"fechaHora\":\"2030-07-01T10:00:00\",\"duracionMinutos\":30}";
        mockMvc.perform(post("/api/v1/citas").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("pendiente"));
    }

    @Test
    void testCrearCitaInvalidaResponde400() throws Exception {
        when(citaService.crear(any())).thenThrow(new BusinessRuleException("El clienteId es obligatorio"));
        mockMvc.perform(post("/api/v1/citas").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testListarRespondeLasCitas() throws Exception {
        when(citaService.listarConNombres()).thenReturn(List.of(response("pendiente")));
        mockMvc.perform(get("/api/v1/citas")).andExpect(status().isOk());
    }

    @Test
    void testObtenerPorId() throws Exception {
        when(citaService.obtenerConNombres(1L)).thenReturn(response("pendiente"));
        mockMvc.perform(get("/api/v1/citas/1")).andExpect(status().isOk());
    }

    @Test
    void testTransiciones() throws Exception {
        when(citaService.confirmar(1L)).thenReturn(cita("confirmada"));
        when(citaService.iniciar(1L)).thenReturn(cita("en curso"));
        when(citaService.completar(1L)).thenReturn(cita("completada"));
        mockMvc.perform(put("/api/v1/citas/1/confirmar")).andExpect(status().isOk());
        mockMvc.perform(put("/api/v1/citas/1/iniciar")).andExpect(status().isOk());
        mockMvc.perform(put("/api/v1/citas/1/completar")).andExpect(status().isOk());
    }

    @Test
    void testCancelar() throws Exception {
        when(citaService.cancelar(eq(1L), any())).thenReturn(cita("cancelada"));
        mockMvc.perform(put("/api/v1/citas/1/cancelar").contentType(MediaType.APPLICATION_JSON)
                .content("{\"motivoCancelacion\":\"Emergencia\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testConfirmarCitaInexistenteResponde404() throws Exception {
        when(citaService.confirmar(anyLong())).thenThrow(new ResourceNotFoundException("Cita no encontrada con id 99"));
        mockMvc.perform(put("/api/v1/citas/99/confirmar")).andExpect(status().isNotFound());
    }
}
