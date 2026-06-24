package cl.vetnova.soporte.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import cl.vetnova.soporte.dto.CerrarEscalamientoRequest;
import cl.vetnova.soporte.dto.EscalamientoRequest;
import cl.vetnova.soporte.dto.GestionarEscalamientoRequest;
import cl.vetnova.soporte.model.EscalamientoTicket;
import cl.vetnova.soporte.service.EscalamientoTicketService;

@WebMvcTest(EscalamientoTicketController.class)
public class EscalamientoTicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EscalamientoTicketService service;

    @Test
    void testCrear() throws Exception {
        when(service.crear(any(EscalamientoRequest.class))).thenReturn(new EscalamientoTicket());
        mockMvc.perform(post("/escalamientos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"ticketId\":1,\"administradorId\":1,\"motivo\":\"m\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testGestionarYCerrar() throws Exception {
        when(service.gestionar(eq(1L), any(GestionarEscalamientoRequest.class))).thenReturn(new EscalamientoTicket());
        when(service.cerrar(eq(1L), any(CerrarEscalamientoRequest.class))).thenReturn(new EscalamientoTicket());
        mockMvc.perform(put("/escalamientos/1/gestionar").contentType(MediaType.APPLICATION_JSON).content("{\"accion\":\"x\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/escalamientos/1/cerrar").contentType(MediaType.APPLICATION_JSON).content("{\"resolucion\":\"ok\"}"))
                .andExpect(status().isOk());
    }
}
