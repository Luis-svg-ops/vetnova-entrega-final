package cl.vetnova.ventas.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.vetnova.ventas.dto.OrdenResponse;
import cl.vetnova.ventas.exception.ResourceNotFoundException;
import cl.vetnova.ventas.service.OrdenService;
import cl.vetnova.ventas.service.PagoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrdenController.class)
public class OrdenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrdenService ordenService;
    @MockBean
    private PagoService pagoService;

    private OrdenResponse ordenPendiente() {
        OrdenResponse response = new OrdenResponse();
        response.setId(1L);
        response.setClienteId(2L);
        response.setEstado("PENDIENTE");
        response.setTotal(85656.2);
        return response;
    }

    @Test
    void testCrearOrdenValidaResponde201() throws Exception {
        when(ordenService.crearOrden(any())).thenReturn(ordenPendiente());

        String body = """
                {"clienteId":2,"sucursal":"CHILLAN",
                 "detalles":[{"productoId":1,"nombreProducto":"Alimento","cantidad":2,"precioUnitario":35990}]}
                """;
        mockMvc.perform(post("/api/v1/ordenes").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.total").value(85656.2));
    }

    @Test
    void testCrearOrdenSinDetallesResponde400() throws Exception {
        String body = """
                {"clienteId":2,"idSucursal":1,"detalles":[]}
                """;
        mockMvc.perform(post("/api/v1/ordenes").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.detalles").exists());
    }

    @Test
    void testBuscarOrdenInexistenteResponde404() throws Exception {
        when(ordenService.obtenerPorId(99L)).thenThrow(new ResourceNotFoundException("Orden no encontrada con id 99"));

        mockMvc.perform(get("/api/v1/ordenes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testExisteRespondeIdYBoolean() throws Exception {
        when(ordenService.existe(1L)).thenReturn(true);

        mockMvc.perform(get("/api/v1/ordenes/1/existe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.existe").value(true));
    }

    @Test
    void testCambiarEstadoConValorInvalidoResponde400() throws Exception {
        mockMvc.perform(put("/api/v1/ordenes/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"VOLANDO\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegistrarPagoResponde201() throws Exception {
        OrdenResponse confirmada = ordenPendiente();
        confirmada.setEstado("CONFIRMADA");
        when(pagoService.registrarPago(eq(1L), any())).thenReturn(confirmada);

        mockMvc.perform(post("/api/v1/ordenes/1/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"metodo\":\"DEBITO\",\"monto\":85656.2,\"referencia\":\"TRX-1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"));
    }

    @Test
    void testListarOrdenesRespondeOk() throws Exception {
        mockMvc.perform(get("/api/v1/ordenes")).andExpect(status().isOk());
    }

    @Test
    void testObtenerOrdenPorIdRespondeOk() throws Exception {
        mockMvc.perform(get("/api/v1/ordenes/1")).andExpect(status().isOk());
    }

    @Test
    void testCambiarEstadoDeOrdenRespondeOk() throws Exception {
        mockMvc.perform(put("/api/v1/ordenes/1/estado").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"CONFIRMADA\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testConfirmarOrdenRespondeOk() throws Exception {
        OrdenResponse confirmada = ordenPendiente();
        confirmada.setEstado("CONFIRMADA");
        when(ordenService.confirmar(1L)).thenReturn(confirmada);

        mockMvc.perform(put("/api/v1/ordenes/1/confirmar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"));
    }

    @Test
    void testCancelarOrdenRespondeOk() throws Exception {
        OrdenResponse cancelada = ordenPendiente();
        cancelada.setEstado("CANCELADA");
        when(ordenService.cancelar(1L)).thenReturn(cancelada);

        mockMvc.perform(put("/api/v1/ordenes/1/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));
    }

    @Test
    void testAgregarDetalleResponde201() throws Exception {
        when(ordenService.agregarDetalle(eq(1L), any())).thenReturn(ordenPendiente());
        mockMvc.perform(post("/api/v1/ordenes/1/detalles").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\":10,\"tipoItem\":\"PRODUCTO\",\"cantidad\":2,\"precioUnitario\":500}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testActualizarDetalleRespondeOk() throws Exception {
        when(ordenService.actualizarDetalle(eq(1L), eq(5L), any())).thenReturn(ordenPendiente());
        mockMvc.perform(put("/api/v1/ordenes/1/detalles/5").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidad\":4}"))
                .andExpect(status().isOk());
    }

    @Test
    void testEliminarDetalleRespondeOk() throws Exception {
        when(ordenService.eliminarDetalle(1L, 5L)).thenReturn(ordenPendiente());
        mockMvc.perform(delete("/api/v1/ordenes/1/detalles/5")).andExpect(status().isOk());
    }
}
