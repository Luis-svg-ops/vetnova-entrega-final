package cl.vetnova.facturacion.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

import cl.vetnova.facturacion.dto.DocumentoTributarioRequest;
import cl.vetnova.facturacion.model.DocumentoTributario;
import cl.vetnova.facturacion.service.DocumentoTributarioService;

@WebMvcTest(DocumentoTributarioController.class)
public class DocumentoTributarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentoTributarioService service;

    @Test
    void testListarYBuscar() throws Exception {
        when(service.listar()).thenReturn(List.of(new DocumentoTributario()));
        when(service.buscarPorId(1L)).thenReturn(new DocumentoTributario());
        mockMvc.perform(get("/documentos")).andExpect(status().isOk());
        mockMvc.perform(get("/documentos/1")).andExpect(status().isOk());
    }

    @Test
    void testEmitir() throws Exception {
        when(service.emitir(any(DocumentoTributarioRequest.class))).thenReturn(new DocumentoTributario());
        mockMvc.perform(post("/documentos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"ordenId\":1,\"clienteId\":1,\"tipo\":\"BOLETA\",\"rutEmisor\":\"11.111.111-1\",\"sucursal\":1}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testAnularYEnviar() throws Exception {
        when(service.anular(eq(1L), anyString())).thenReturn(new DocumentoTributario());
        when(service.enviarAlSII(1L)).thenReturn(new DocumentoTributario());
        mockMvc.perform(put("/documentos/1/anular").contentType(MediaType.APPLICATION_JSON).content("{\"motivo\":\"x\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/documentos/1/enviar-sii")).andExpect(status().isOk());
    }
}
