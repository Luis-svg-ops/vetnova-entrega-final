package cl.vetnova.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.vetnova.auth.dto.ActualizarClienteRequest;
import cl.vetnova.auth.dto.CrearClienteRequest;
import cl.vetnova.auth.model.Cliente;
import cl.vetnova.auth.service.ClienteService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ClienteController.class)
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;

    @Test
    void testEndpointsDeClientes() throws Exception {
        when(clienteService.listar()).thenReturn(List.of(new Cliente()));
        when(clienteService.obtenerPorId(1L)).thenReturn(new Cliente());
        when(clienteService.crear(any(CrearClienteRequest.class))).thenReturn(new Cliente());
        when(clienteService.actualizarDatos(eq(1L), any(ActualizarClienteRequest.class))).thenReturn(new Cliente());
        when(clienteService.desactivar(1L)).thenReturn(new Cliente());

        mockMvc.perform(get("/api/v1/clientes")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/clientes/1")).andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/clientes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuarioId\":1,\"rut\":\"11.111.111-1\",\"nombre\":\"Juan\",\"apellido\":\"Pérez\",\"email\":\"juan@mail.com\"}"))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/api/v1/clientes/1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"telefono\":\"+56987654321\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/v1/clientes/1")).andExpect(status().isOk());
    }
}
