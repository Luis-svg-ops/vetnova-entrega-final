package cl.vetnova.envio.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.vetnova.envio.dto.EnvioResponse;
import cl.vetnova.envio.dto.TrackingResponse;
import cl.vetnova.envio.exception.ResourceNotFoundException;
import cl.vetnova.envio.service.EnvioService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EnvioController.class)
public class EnvioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EnvioService envioService;

    private EnvioResponse envio(String estado) {
        EnvioResponse response = new EnvioResponse();
        response.setId(1L);
        response.setNumeroGuia("GD-1");
        response.setOrdenId(1L);
        response.setEstadoActual(estado);
        return response;
    }

    @Test
    void testCrearEnvioValidoResponde201() throws Exception {
        when(envioService.crearEnvio(any())).thenReturn(envio("PREPARANDO"));

        String body = """
                {"ordenId":1,"tipoEnvio":"DOMICILIO","idSucursalOrigen":"CHILLAN","direccionEntrega":"Av. Libertad 123"}
                """;
        mockMvc.perform(post("/api/v1/envios").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroGuia").value("GD-1"));
    }

    @Test
    void testCrearEnvioSinOrdenIdResponde400() throws Exception {
        String body = """
                {"tipoEnvio":"DOMICILIO","idSucursalOrigen":"CHILLAN"}
                """;
        mockMvc.perform(post("/api/v1/envios").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.ordenId").exists());
    }

    @Test
    void testActualizarEstadoRespondeOk() throws Exception {
        when(envioService.actualizarEstado(eq(1L), any())).thenReturn(envio("EN_RUTA"));

        mockMvc.perform(put("/api/v1/envios/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"EN_RUTA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoActual").value("EN_RUTA"));
    }

    @Test
    void testTrackingDeEnvioInexistenteResponde404() throws Exception {
        when(envioService.obtenerTracking(99L)).thenThrow(new ResourceNotFoundException("Envío no encontrado con id 99"));

        mockMvc.perform(get("/api/v1/envios/99/tracking"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testTrackingRespondeElHistorial() throws Exception {
        TrackingResponse tracking = new TrackingResponse();
        tracking.setEstado("PREPARANDO");
        when(envioService.obtenerTracking(1L)).thenReturn(List.of(tracking));

        mockMvc.perform(get("/api/v1/envios/1/tracking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("PREPARANDO"));
    }

    @Test
    void testListarYObtenerEnvioRespondenOk() throws Exception {
        mockMvc.perform(get("/api/v1/envios")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/envios/1")).andExpect(status().isOk());
    }
}
