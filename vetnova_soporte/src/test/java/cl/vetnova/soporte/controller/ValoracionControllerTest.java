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

import cl.vetnova.soporte.dto.PromedioValoracionResponse;
import cl.vetnova.soporte.model.Valoracion;
import cl.vetnova.soporte.service.ValoracionService;

@WebMvcTest(ValoracionController.class)
public class ValoracionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ValoracionService valoracionService;

    @Test
    void testCrear() throws Exception {
        when(valoracionService.crear(any(Valoracion.class))).thenReturn(new Valoracion());
        mockMvc.perform(post("/valoraciones").contentType(MediaType.APPLICATION_JSON)
                .content("{\"ticketId\":1,\"clienteId\":1,\"puntuacion\":5}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testPromedio() throws Exception {
        when(valoracionService.promedioPorSucursal(1L)).thenReturn(new PromedioValoracionResponse(1L, 4.3, 10));
        mockMvc.perform(get("/valoraciones/promedio").param("sucursalId", "1")).andExpect(status().isOk());
    }

    @Test
    void testModificarYEliminarResponden405() throws Exception {
        mockMvc.perform(put("/valoraciones/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(delete("/valoraciones/1")).andExpect(status().isMethodNotAllowed());
    }
}
