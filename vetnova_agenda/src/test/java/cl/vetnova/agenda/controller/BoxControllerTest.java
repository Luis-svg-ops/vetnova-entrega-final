package cl.vetnova.agenda.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.vetnova.agenda.model.Box;
import cl.vetnova.agenda.service.BoxService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BoxController.class)
public class BoxControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoxService boxService;

    @Test
    void testCrudDeBoxes() throws Exception {
        when(boxService.crear(any())).thenReturn(new Box());
        when(boxService.listar()).thenReturn(List.of(new Box()));
        when(boxService.reservar(1L)).thenReturn(new Box());
        when(boxService.liberar(1L)).thenReturn(new Box());

        mockMvc.perform(post("/api/v1/boxes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Box 1\",\"sucursal\":\"Chillán\",\"disponible\":true,\"tipo\":\"CONSULTA\"}"))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/v1/boxes")).andExpect(status().isOk());
        mockMvc.perform(put("/api/v1/boxes/1/reservar")).andExpect(status().isOk());
        mockMvc.perform(put("/api/v1/boxes/1/liberar")).andExpect(status().isOk());
        mockMvc.perform(delete("/api/v1/boxes/1")).andExpect(status().isNoContent());
    }
}
