package cl.vetnova.ventas.controller;

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

import cl.vetnova.ventas.dto.CarritoItemResultado;
import cl.vetnova.ventas.model.Carrito;
import cl.vetnova.ventas.service.CarritoService;

@WebMvcTest(CarritoController.class)
public class CarritoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarritoService carritoService;

    private static final String ITEM_BODY = "{\"itemId\":10,\"tipo\":\"PRODUCTO\",\"cantidad\":2,\"precio\":500}";

    @Test
    void testEndpointsBasicos() throws Exception {
        when(carritoService.listar()).thenReturn(List.of(new Carrito()));
        when(carritoService.obtenerPorId(1L)).thenReturn(new Carrito());
        when(carritoService.crear(any(Carrito.class))).thenReturn(new Carrito());
        when(carritoService.actualizar(eq(1L), any(Carrito.class))).thenReturn(new Carrito());

        mockMvc.perform(get("/api/v1/carritos")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/carritos/1")).andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/carritos").contentType(MediaType.APPLICATION_JSON).content("{\"clienteId\":2}"))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/api/v1/carritos/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/v1/carritos/1")).andExpect(status().isNoContent());
    }

    @Test
    void testAgregarItemNuevoResponde201() throws Exception {
        when(carritoService.agregarItem(eq(1L), any())).thenReturn(new CarritoItemResultado(new Carrito(), true));
        mockMvc.perform(post("/api/v1/carritos/1/items").contentType(MediaType.APPLICATION_JSON).content(ITEM_BODY))
                .andExpect(status().isCreated());
    }

    @Test
    void testAgregarItemExistenteResponde200() throws Exception {
        when(carritoService.agregarItem(eq(1L), any())).thenReturn(new CarritoItemResultado(new Carrito(), false));
        mockMvc.perform(post("/api/v1/carritos/1/items").contentType(MediaType.APPLICATION_JSON).content(ITEM_BODY))
                .andExpect(status().isOk());
    }

    @Test
    void testActualizarCantidadResponde200() throws Exception {
        when(carritoService.actualizarCantidad(eq(1L), eq(10L), any())).thenReturn(new Carrito());
        mockMvc.perform(put("/api/v1/carritos/1/items/10").contentType(MediaType.APPLICATION_JSON)
                .content("{\"cantidad\":3}")).andExpect(status().isOk());
    }

    @Test
    void testQuitarItemResponde200() throws Exception {
        when(carritoService.quitarItem(1L, 10L)).thenReturn(new Carrito());
        mockMvc.perform(delete("/api/v1/carritos/1/items/10")).andExpect(status().isOk());
    }

    @Test
    void testCalcularTotalResponde200() throws Exception {
        when(carritoService.calcularTotal(1L)).thenReturn(1500.0);
        mockMvc.perform(get("/api/v1/carritos/1/total")).andExpect(status().isOk());
    }

    @Test
    void testVaciarResponde200() throws Exception {
        when(carritoService.vaciar(1L)).thenReturn(new Carrito());
        mockMvc.perform(delete("/api/v1/carritos/1/items")).andExpect(status().isOk());
    }
}
