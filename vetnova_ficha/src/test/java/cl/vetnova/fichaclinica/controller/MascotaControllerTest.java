package cl.vetnova.fichaclinica.controller;

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

import cl.vetnova.fichaclinica.dto.MascotaDesactivacionResponse;
import cl.vetnova.fichaclinica.dto.MascotaResponse;
import cl.vetnova.fichaclinica.model.Mascota;
import cl.vetnova.fichaclinica.service.MascotaService;

@WebMvcTest(MascotaController.class)
public class MascotaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MascotaService mascotaService;

    @Test
    void testEndpointsCrud() throws Exception {
        MascotaResponse resp = new MascotaResponse(new Mascota(), null);
        when(mascotaService.listarConCliente()).thenReturn(List.of(resp));
        when(mascotaService.obtenerPorIdConCliente(1L)).thenReturn(resp);
        when(mascotaService.crear(any(Mascota.class))).thenReturn(new Mascota());
        when(mascotaService.actualizar(eq(1L), any(Mascota.class))).thenReturn(new Mascota());
        when(mascotaService.desactivar(1L)).thenReturn(new MascotaDesactivacionResponse(new Mascota(), "Mascota desactivada"));

        mockMvc.perform(get("/api/v1/mascotas")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/mascotas/1")).andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/mascotas").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/api/v1/mascotas/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/v1/mascotas/1")).andExpect(status().isOk());
    }
}