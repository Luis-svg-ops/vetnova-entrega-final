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

import cl.vetnova.soporte.dto.RespuestaRequest;
import cl.vetnova.soporte.model.RespuestaTicket;
import cl.vetnova.soporte.service.RespuestaTicketService;

@WebMvcTest(RespuestaTicketController.class)
public class RespuestaTicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RespuestaTicketService service;

    @Test
    void testRegistrar() throws Exception {
        when(service.registrar(any(RespuestaRequest.class))).thenReturn(new RespuestaTicket());
        mockMvc.perform(post("/respuestas-ticket").contentType(MediaType.APPLICATION_JSON)
                .content("{\"ticketId\":1,\"autorId\":3,\"contenido\":\"c\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testModificarYEliminarResponden405() throws Exception {
        mockMvc.perform(put("/respuestas-ticket/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(delete("/respuestas-ticket/1")).andExpect(status().isMethodNotAllowed());
    }
}
