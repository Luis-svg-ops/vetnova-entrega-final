package cl.vetnova.catalogo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.vetnova.catalogo.exception.BusinessRuleException;
import cl.vetnova.catalogo.exception.ConflictException;
import cl.vetnova.catalogo.model.Categoria;
import cl.vetnova.catalogo.service.CategoriaService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CategoriaController.class)
public class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoriaService categoriaService;

    @Test
    void testCrudDeCategorias() throws Exception {
        when(categoriaService.crear(any())).thenReturn(new Categoria());
        when(categoriaService.listar()).thenReturn(List.of(new Categoria()));

        mockMvc.perform(post("/api/v1/categorias").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Alimentos\",\"descripcion\":\"Alimentos para mascotas\",\"tipo\":\"PRODUCTO\"}"))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/v1/categorias")).andExpect(status().isOk());
        mockMvc.perform(delete("/api/v1/categorias/1")).andExpect(status().isNoContent());
    }

    @Test
    void testCrearCategoriaSinNombreResponde400() throws Exception {
        when(categoriaService.crear(any())).thenThrow(new BusinessRuleException("El nombre es obligatorio"));
        mockMvc.perform(post("/api/v1/categorias").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descripcion\":\"Sin nombre\",\"tipo\":\"PRODUCTO\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCrearCategoriaDuplicadaResponde409() throws Exception {
        when(categoriaService.crear(any())).thenThrow(new ConflictException("Ya existe una categoría con ese nombre"));
        mockMvc.perform(post("/api/v1/categorias").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Medicamentos\",\"tipo\":\"producto\"}"))
                .andExpect(status().isConflict());
    }
}
