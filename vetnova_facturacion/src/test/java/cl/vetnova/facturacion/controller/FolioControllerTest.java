package cl.vetnova.facturacion.controller;

import static org.mockito.ArgumentMatchers.any;
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

import cl.vetnova.facturacion.dto.FolioRequest;
import cl.vetnova.facturacion.model.Folio;
import cl.vetnova.facturacion.service.FolioService;

@WebMvcTest(FolioController.class)
public class FolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FolioService service;

    @Test
    void testListarYBuscar() throws Exception {
        when(service.listar()).thenReturn(List.of(new Folio()));
        when(service.buscar(1L)).thenReturn(new Folio());
        mockMvc.perform(get("/folios")).andExpect(status().isOk());
        mockMvc.perform(get("/folios/1")).andExpect(status().isOk());
    }

    @Test
    void testCrear() throws Exception {
        when(service.crear(any(FolioRequest.class))).thenReturn(new Folio());
        mockMvc.perform(post("/folios").contentType(MediaType.APPLICATION_JSON)
                .content("{\"sucursal\":1,\"tipoDocumento\":\"BOLETA\",\"folioDesde\":1,\"folioHasta\":100}"))
                .andExpect(status().isCreated());
    }
}
