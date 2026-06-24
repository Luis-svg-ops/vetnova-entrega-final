package cl.vetnova.inventario.controller;

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

import cl.vetnova.inventario.dto.ResolucionRequest;
import cl.vetnova.inventario.dto.SolicitudReposicionRequest;
import cl.vetnova.inventario.model.SolicitudReposicion;
import cl.vetnova.inventario.service.SolicitudReposicionService;

@WebMvcTest(SolicitudReposicionController.class)
public class SolicitudReposicionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SolicitudReposicionService solicitudReposicionService;

    @Test
    void testEndpointsBasicos() throws Exception {
        when(solicitudReposicionService.listar()).thenReturn(List.of(new SolicitudReposicion()));
        when(solicitudReposicionService.obtenerPorId(1L)).thenReturn(new SolicitudReposicion());
        when(solicitudReposicionService.crear(any(SolicitudReposicionRequest.class))).thenReturn(new SolicitudReposicion());
        when(solicitudReposicionService.actualizar(eq(1L), any(SolicitudReposicion.class))).thenReturn(new SolicitudReposicion());

        mockMvc.perform(get("/api/v1/solicitudreposicions")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/solicitudreposicions/1")).andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/solicitudreposicions").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/api/v1/solicitudreposicions/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void testAprobarYRechazar() throws Exception {
        when(solicitudReposicionService.aprobar(eq(1L), any(ResolucionRequest.class))).thenReturn(new SolicitudReposicion());
        when(solicitudReposicionService.rechazar(eq(1L), any(ResolucionRequest.class))).thenReturn(new SolicitudReposicion());

        mockMvc.perform(put("/api/v1/solicitudreposicions/1/aprobar").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/api/v1/solicitudreposicions/1/rechazar").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void testEliminarDevuelve405() throws Exception {
        mockMvc.perform(delete("/api/v1/solicitudreposicions/1")).andExpect(status().isMethodNotAllowed());
    }
}
