package cl.vetnova.catalogo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.vetnova.catalogo.model.Oferta;
import cl.vetnova.catalogo.service.OfertaService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OfertaController.class)
public class OfertaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OfertaService ofertaService;

    @Test
    void testCrudDeOfertas() throws Exception {
        when(ofertaService.crear(any())).thenReturn(new Oferta());
        when(ofertaService.listar()).thenReturn(List.of(new Oferta()));
        when(ofertaService.activar(1L)).thenReturn(new Oferta());
        when(ofertaService.desactivar(1L)).thenReturn(new Oferta());

        mockMvc.perform(post("/api/v1/ofertas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productoId\":1,\"descuento\":20}"))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/v1/ofertas")).andExpect(status().isOk());
        mockMvc.perform(put("/api/v1/ofertas/1/activar")).andExpect(status().isOk());
        mockMvc.perform(put("/api/v1/ofertas/1/desactivar")).andExpect(status().isOk());
        mockMvc.perform(delete("/api/v1/ofertas/1")).andExpect(status().isNoContent());
    }
}
