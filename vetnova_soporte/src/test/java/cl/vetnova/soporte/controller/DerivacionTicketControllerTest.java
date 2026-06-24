package cl.vetnova.soporte.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import cl.vetnova.soporte.dto.DerivacionRequest;
import cl.vetnova.soporte.model.DerivacionTicket;
import cl.vetnova.soporte.service.DerivacionTicketService;

@WebMvcTest(DerivacionTicketController.class)
public class DerivacionTicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DerivacionTicketService service;

    @Test
    void testRegistrar() throws Exception {
        when(service.registrar(any(DerivacionRequest.class))).thenReturn(new DerivacionTicket());
        mockMvc.perform(post("/derivaciones").contentType(MediaType.APPLICATION_JSON)
                .content("{\"ticketId\":1,\"responsableNuevo\":3,\"motivo\":\"m\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testModificarYEliminarResponden405() throws Exception {
        mockMvc.perform(put("/derivaciones/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(delete("/derivaciones/1")).andExpect(status().isMethodNotAllowed());
    }
}
