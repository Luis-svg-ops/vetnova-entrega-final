package cl.vetnova.catalogo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.vetnova.catalogo.dto.ProductoResponse;
import cl.vetnova.catalogo.exception.BusinessRuleException;
import cl.vetnova.catalogo.exception.ConflictException;
import cl.vetnova.catalogo.exception.ResourceNotFoundException;
import cl.vetnova.catalogo.service.ProductoService;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
        ProductoResponse response = new ProductoResponse();
        response.setId(1L);
        response.setNombre("Antiparasitario canino");
        when(productoService.crear(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Antiparasitario canino\",\"precio\":12990}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Antiparasitario canino"));
    }

    @Test
    void testCrearProductoSinNombreResponde400() throws Exception {
        when(productoService.crear(any())).thenThrow(new BusinessRuleException("El nombre es obligatorio"));
        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"precio\":12990}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCrearProductoDuplicadoResponde409() throws Exception {
        when(productoService.crear(any())).thenThrow(new ConflictException("Ya existe un producto con ese nombre"));
        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"X\",\"precio\":5000,\"categoriaId\":1}"))
                .andExpect(status().isConflict());
    }

    @Test
    void testListarRespondeLosProductos() throws Exception {
        when(productoService.listar()).thenReturn(List.of(new ProductoResponse()));

        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk());
    }

    @Test
    void testBuscarProductoInexistenteResponde404() throws Exception {
        when(productoService.obtenerPorId(99L)).thenThrow(new ResourceNotFoundException("Producto no encontrado con id 99"));

        mockMvc.perform(get("/api/v1/productos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testEliminarProductoResponde204() throws Exception {
        mockMvc.perform(delete("/api/v1/productos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testEliminarProductoInexistenteResponde404() throws Exception {
        doThrow(new ResourceNotFoundException("Producto no encontrado con id 99"))
                .when(productoService).eliminar(99L);

        mockMvc.perform(delete("/api/v1/productos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAccionesDeProductoRespondenOk() throws Exception {
        mockMvc.perform(put("/api/v1/productos/1/activar")).andExpect(status().isOk());
        mockMvc.perform(put("/api/v1/productos/1/desactivar")).andExpect(status().isOk());
        mockMvc.perform(put("/api/v1/productos/1/precio").param("nuevoPrecio", "19990"))
                .andExpect(status().isOk());
    }

    @Test
    void testObtenerYEliminarProductoRespondenSegunContrato() throws Exception {
        mockMvc.perform(get("/api/v1/productos")).andExpect(status().isOk());
        mockMvc.perform(delete("/api/v1/productos/1")).andExpect(status().isNoContent());
    }

    @Test
    void testObtenerProductoPorIdRespondeOk() throws Exception {
        mockMvc.perform(get("/api/v1/productos/1")).andExpect(status().isOk());
    }
}
