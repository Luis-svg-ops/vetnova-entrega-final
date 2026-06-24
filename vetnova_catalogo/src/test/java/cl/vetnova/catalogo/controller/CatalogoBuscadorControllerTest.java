package cl.vetnova.catalogo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.vetnova.catalogo.model.Producto;
import cl.vetnova.catalogo.service.CatalogoBuscadorService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CatalogoBuscadorController.class)
public class CatalogoBuscadorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CatalogoBuscadorService buscadorService;

    @Test
    void testEndpointsDeBusqueda() throws Exception {
        when(buscadorService.buscarPorNombre(anyString())).thenReturn(List.of(new Producto()));
        when(buscadorService.filtrarPorCategoria(anyLong())).thenReturn(List.of(new Producto()));
        when(buscadorService.filtrarPorRango(any(), any())).thenReturn(List.of(new Producto()));
        when(buscadorService.listarDisponibles(anyString())).thenReturn(List.of(new Producto()));
        when(buscadorService.getDetalle(anyLong(), anyString())).thenReturn(new Producto());

        mockMvc.perform(get("/api/v1/catalogo/buscar").param("nombre", "amox")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/catalogo/buscar/categoria").param("categoriaId", "1")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/catalogo/buscar/rango").param("min", "1000").param("max", "5000")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/catalogo/disponibles").param("sucursal", "SANTIAGO")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/catalogo/detalle").param("itemId", "1").param("tipo", "producto")).andExpect(status().isOk());
    }
}
