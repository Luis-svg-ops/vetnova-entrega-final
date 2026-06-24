package cl.vetnova.inventario.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.vetnova.inventario.exception.ResourceNotFoundException;
import cl.vetnova.inventario.service.ProductoService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductoController.class)
public class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @Test
    void testCrearProductoValidoResponde201() throws Exception {
        mockMvc.perform(post("/api/v1/inventario/productos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sku\":\"ALM-001\",\"nombre\":\"Alimento premium\",\"precio\":15990}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testCrearProductoSinSkuResponde400() throws Exception {
        mockMvc.perform(post("/api/v1/inventario/productos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Alimento premium\",\"precio\":15990}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testListarYActualizarRespondenOk() throws Exception {
        when(productoService.listar()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/inventario/productos")).andExpect(status().isOk());
        mockMvc.perform(put("/api/v1/inventario/productos/1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sku\":\"ALM-001\",\"nombre\":\"Alimento renovado\",\"precio\":17990}"))
                .andExpect(status().isOk());
    }

    @Test
    void testObtenerProductoInexistenteResponde404() throws Exception {
        when(productoService.obtenerPorId(99L)).thenThrow(new ResourceNotFoundException("Producto no encontrado"));

        mockMvc.perform(get("/api/v1/inventario/productos/99")).andExpect(status().isNotFound());
    }

    @Test
    void testDesactivarProductoResponde204() throws Exception {
        mockMvc.perform(delete("/api/v1/inventario/productos/1")).andExpect(status().isNoContent());
    }

    @Test
    void testObtenerProductoExistenteRespondeOk() throws Exception {
        mockMvc.perform(get("/api/v1/inventario/productos/1")).andExpect(status().isOk());
    }
}
