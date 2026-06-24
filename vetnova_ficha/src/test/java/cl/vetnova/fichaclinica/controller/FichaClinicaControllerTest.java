package cl.vetnova.fichaclinica.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.vetnova.fichaclinica.exception.BusinessRuleException;
import cl.vetnova.fichaclinica.model.FichaClinica;
import cl.vetnova.fichaclinica.service.FichaClinicaService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FichaClinicaController.class)
public class FichaClinicaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FichaClinicaService fichaClinicaService;

    @Test
    void testCrearFichaValidaResponde201() throws Exception {
        FichaClinica ficha = new FichaClinica();
        ficha.setMascotaId(1L);
        when(fichaClinicaService.crear(any())).thenReturn(ficha);

        mockMvc.perform(post("/api/v1/fichas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mascotaId\":1,\"observacionesGenerales\":\"Control al día\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mascotaId").value(1));
    }

    @Test
    void testCrearFichaSinMascotaResponde400() throws Exception {
        when(fichaClinicaService.crear(any())).thenThrow(new BusinessRuleException("El mascotaId es obligatorio"));
        mockMvc.perform(post("/api/v1/fichas").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testListarRespondeLasFichas() throws Exception {
        when(fichaClinicaService.listar()).thenReturn(List.of(new FichaClinica()));
        mockMvc.perform(get("/api/v1/fichas")).andExpect(status().isOk());
    }

    @Test
    void testObtenerPorId() throws Exception {
        when(fichaClinicaService.obtenerPorId(1L)).thenReturn(new FichaClinica());
        mockMvc.perform(get("/api/v1/fichas/1")).andExpect(status().isOk());
    }

    @Test
    void testBuscarPorMascota() throws Exception {
        when(fichaClinicaService.buscarPorMascota(1L)).thenReturn(new FichaClinica());
        mockMvc.perform(get("/api/v1/fichas").param("mascotaId", "1")).andExpect(status().isOk());
    }

    @Test
    void testEliminarResponde405() throws Exception {
        mockMvc.perform(delete("/api/v1/fichas/1")).andExpect(status().isMethodNotAllowed());
    }
}
