package cl.vetnova.envio.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.vetnova.envio.service.TransferenciaService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TransferenciaController.class)
public class TransferenciaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransferenciaService transferenciaService;

    @Test
    void testCrearTransferenciaValidaResponde201() throws Exception {
        mockMvc.perform(post("/api/v1/transferencias").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idProducto\":1,\"idSucursalOrigen\":1,\"idSucursalDestino\":2,\"cantidad\":5}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testCrearTransferenciaSinProductoResponde400() throws Exception {
        mockMvc.perform(post("/api/v1/transferencias").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idSucursalOrigen\":1,\"idSucursalDestino\":2,\"cantidad\":5}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testListarTransferenciasRespondeOk() throws Exception {
        when(transferenciaService.listar()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/transferencias")).andExpect(status().isOk());
    }
}
