package cl.vetnova.soporte.controller;

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

import cl.vetnova.soporte.dto.ClasificarTicketRequest;
import cl.vetnova.soporte.dto.CrearTicketRequest;
import cl.vetnova.soporte.dto.DerivarTicketRequest;
import cl.vetnova.soporte.dto.ResponderTicketRequest;
import cl.vetnova.soporte.dto.CerrarTicketRequest;
import cl.vetnova.soporte.model.DerivacionTicket;
import cl.vetnova.soporte.model.RespuestaTicket;
import cl.vetnova.soporte.model.Ticket;
import cl.vetnova.soporte.model.Valoracion;
import cl.vetnova.soporte.service.DerivacionTicketService;
import cl.vetnova.soporte.service.RespuestaTicketService;
import cl.vetnova.soporte.service.TicketService;
import cl.vetnova.soporte.service.ValoracionService;

@WebMvcTest(TicketController.class)
public class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private TicketService ticketService;
    @MockBean private DerivacionTicketService derivacionService;
    @MockBean private RespuestaTicketService respuestaService;
    @MockBean private ValoracionService valoracionService;

    @Test
    void testListarYBuscar() throws Exception {
        when(ticketService.listar(any())).thenReturn(List.of(new Ticket()));
        when(ticketService.buscar(1L)).thenReturn(new Ticket());
        mockMvc.perform(get("/tickets")).andExpect(status().isOk());
        mockMvc.perform(get("/tickets").param("estado", "ABIERTO")).andExpect(status().isOk());
        mockMvc.perform(get("/tickets/1")).andExpect(status().isOk());
    }

    @Test
    void testCrear() throws Exception {
        when(ticketService.crear(any(CrearTicketRequest.class))).thenReturn(new Ticket());
        mockMvc.perform(post("/tickets").contentType(MediaType.APPLICATION_JSON)
                .content("{\"clienteId\":1,\"motivo\":\"m\",\"descripcion\":\"d\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testClasificarDerivarEscalarCerrar() throws Exception {
        when(ticketService.clasificar(eq(1L), any(ClasificarTicketRequest.class))).thenReturn(new Ticket());
        when(ticketService.derivar(eq(1L), any(DerivarTicketRequest.class))).thenReturn(new Ticket());
        when(ticketService.escalar(1L)).thenReturn(new Ticket());
        when(ticketService.cerrar(eq(1L), any(CerrarTicketRequest.class))).thenReturn(new Ticket());
        mockMvc.perform(put("/tickets/1/clasificar").contentType(MediaType.APPLICATION_JSON).content("{\"categoriaId\":1,\"prioridad\":\"ALTA\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/tickets/1/derivar").contentType(MediaType.APPLICATION_JSON).content("{\"responsableId\":3}"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/tickets/1/escalar")).andExpect(status().isOk());
        mockMvc.perform(put("/tickets/1/cerrar").contentType(MediaType.APPLICATION_JSON).content("{\"resolucion\":\"ok\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testResponder() throws Exception {
        when(ticketService.responder(eq(1L), any(ResponderTicketRequest.class))).thenReturn(new RespuestaTicket());
        mockMvc.perform(post("/tickets/1/respuestas").contentType(MediaType.APPLICATION_JSON).content("{\"autorId\":3,\"contenido\":\"hola\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testSubrecursos() throws Exception {
        when(derivacionService.listarPorTicket(1L)).thenReturn(List.of(new DerivacionTicket()));
        when(respuestaService.listarPorTicket(1L)).thenReturn(List.of(new RespuestaTicket()));
        when(valoracionService.obtenerPorTicket(1L)).thenReturn(new Valoracion());
        mockMvc.perform(get("/tickets/1/derivaciones")).andExpect(status().isOk());
        mockMvc.perform(get("/tickets/1/respuestas")).andExpect(status().isOk());
        mockMvc.perform(get("/tickets/1/valoracion")).andExpect(status().isOk());
    }
}
