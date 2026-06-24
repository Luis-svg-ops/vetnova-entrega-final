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

import cl.vetnova.inventario.model.Proveedor;
import cl.vetnova.inventario.model.ProveedorProducto;
import cl.vetnova.inventario.service.ProveedorService;

@WebMvcTest(ProveedorController.class)
public class ProveedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProveedorService proveedorService;

    @Test
    void testEndpointsCrud() throws Exception {
        when(proveedorService.listar()).thenReturn(List.of(new Proveedor()));
        when(proveedorService.obtenerPorId(1L)).thenReturn(new Proveedor());
        when(proveedorService.crear(any(Proveedor.class))).thenReturn(new Proveedor());
        when(proveedorService.actualizar(eq(1L), any(Proveedor.class))).thenReturn(new Proveedor());

        mockMvc.perform(get("/api/v1/proveedors")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/proveedors/1")).andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/proveedors").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/api/v1/proveedors/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void testAsociarProducto() throws Exception {
        when(proveedorService.asociarProducto(eq(1L), any())).thenReturn(new ProveedorProducto());
        mockMvc.perform(post("/api/v1/proveedors/1/productos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"productoId\":1}"))
                .andExpect(status().isOk());
    }

    @Test
    void testDesactivar() throws Exception {
        when(proveedorService.desactivar(1L)).thenReturn(new Proveedor());
        mockMvc.perform(delete("/api/v1/proveedors/1")).andExpect(status().isOk());
    }
}
