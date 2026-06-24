package cl.vetnova.catalogo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.vetnova.catalogo.model.Servicio;
import cl.vetnova.catalogo.service.ServicioService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ServicioController.class)
public class ServicioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServicioService servicioService;

    @Test
    void testCrudDeServicios() throws Exception {
        when(servicioService.crear(any())).thenReturn(new Servicio());
        when(servicioService.listar()).thenReturn(List.of(new Servicio()));
        when(servicioService.activar(1L)).thenReturn(new Servicio());
        when(servicioService.desactivar(1L)).thenReturn(new Servicio());
        when(servicioService.actualizarPrecio(1L, 30000.0)).thenReturn(new Servicio());

        mockMvc.perform(post("/api/v1/servicios").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Consulta general\",\"descripcion\":\"Consulta veterinaria\",\"precio\":25000}"))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/v1/servicios")).andExpect(status().isOk());
        mockMvc.perform(put("/api/v1/servicios/1/activar")).andExpect(status().isOk());
        mockMvc.perform(put("/api/v1/servicios/1/desactivar")).andExpect(status().isOk());
        mockMvc.perform(put("/api/v1/servicios/1/precio").param("nuevoPrecio", "30000"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/v1/servicios/1")).andExpect(status().isNoContent());
    }
}
