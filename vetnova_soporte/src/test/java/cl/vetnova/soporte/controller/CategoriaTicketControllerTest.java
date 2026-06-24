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

import cl.vetnova.soporte.dto.CategoriaTicketRequest;
import cl.vetnova.soporte.model.CategoriaTicket;
import cl.vetnova.soporte.service.CategoriaTicketService;

@WebMvcTest(CategoriaTicketController.class)
public class CategoriaTicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoriaTicketService service;

    @Test
    void testListarYBuscar() throws Exception {
        when(service.listar()).thenReturn(List.of(new CategoriaTicket()));
        when(service.buscarEntidad(1L)).thenReturn(new CategoriaTicket());
        mockMvc.perform(get("/categorias-ticket")).andExpect(status().isOk());
        mockMvc.perform(get("/categorias-ticket/1")).andExpect(status().isOk());
    }

    @Test
    void testCrearYActualizar() throws Exception {
        when(service.crear(any(CategoriaTicketRequest.class))).thenReturn(new CategoriaTicket());
        when(service.actualizar(eq(1L), any(CategoriaTicketRequest.class))).thenReturn(new CategoriaTicket());
        mockMvc.perform(post("/categorias-ticket").contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"Facturación\"}"))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/categorias-ticket/1").contentType(MediaType.APPLICATION_JSON).content("{\"descripcion\":\"x\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testEliminar() throws Exception {
        mockMvc.perform(delete("/categorias-ticket/1")).andExpect(status().isOk());
    }
}
