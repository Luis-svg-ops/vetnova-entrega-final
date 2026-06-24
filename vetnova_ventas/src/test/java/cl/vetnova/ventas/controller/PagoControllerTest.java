package cl.vetnova.ventas.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import cl.vetnova.ventas.dto.CrearPagoRequest;
import cl.vetnova.ventas.dto.PagoResponse;
import cl.vetnova.ventas.service.PagoService;

@WebMvcTest(PagoController.class)
public class PagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PagoService pagoService;

    private PagoResponse pago(String estado) {
        PagoResponse response = new PagoResponse();
        response.setId(1L);
        response.setEstado(estado);
        return response;
    }

    @Test
    void testCrearPagoResponde201() throws Exception {
        when(pagoService.crearPago(any(CrearPagoRequest.class))).thenReturn(pago("PENDIENTE"));
        mockMvc.perform(post("/api/v1/pagos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"ordenId\":1,\"metodo\":\"TARJETA\",\"monto\":1500,\"referencia\":\"TXN-1\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testProcesarConfirmarReembolsar() throws Exception {
        when(pagoService.procesar(1L)).thenReturn(pago("APROBADO"));
        when(pagoService.confirmar(1L)).thenReturn(pago("APROBADO"));
        when(pagoService.reembolsar(1L)).thenReturn(pago("REEMBOLSADO"));

        mockMvc.perform(put("/api/v1/pagos/1/procesar")).andExpect(status().isOk());
        mockMvc.perform(put("/api/v1/pagos/1/confirmar")).andExpect(status().isOk());
        mockMvc.perform(put("/api/v1/pagos/1/reembolsar")).andExpect(status().isOk());
    }

    @Test
    void testRechazar() throws Exception {
        when(pagoService.rechazar(eq(1L), any())).thenReturn(pago("RECHAZADO"));
        mockMvc.perform(put("/api/v1/pagos/1/rechazar").contentType(MediaType.APPLICATION_JSON)
                .content("{\"motivo\":\"fondos insuficientes\"}"))
                .andExpect(status().isOk());
    }
}
