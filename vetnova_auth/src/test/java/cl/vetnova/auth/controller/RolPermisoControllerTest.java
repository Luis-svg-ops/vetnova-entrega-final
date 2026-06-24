package cl.vetnova.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.vetnova.auth.dto.RolResponse;
import cl.vetnova.auth.service.RolPermisoService;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RolPermisoController.class)
public class RolPermisoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RolPermisoService rolPermisoService;

    @Test
    void testCrudDeRoles() throws Exception {
        RolResponse rol = new RolResponse(1L, "CLIENTE", "Rol cliente", true, Set.of("AGENDAR_CITA"));
        when(rolPermisoService.listar()).thenReturn(List.of(rol));
        when(rolPermisoService.crear(any())).thenReturn(rol);
        when(rolPermisoService.actualizar(anyLong(), any())).thenReturn(rol);

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].nombreRol").value("CLIENTE"));
        mockMvc.perform(post("/api/roles").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreRol\":\"CLIENTE\",\"descripcion\":\"Rol cliente\",\"permisos\":[\"AGENDAR_CITA\"]}"))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/api/roles/1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreRol\":\"CLIENTE\",\"descripcion\":\"Actualizado\",\"permisos\":[\"AGENDAR_CITA\"]}"))
                .andExpect(status().isOk());
    }

    @Test
    void testEndpointsDePermisos() throws Exception {
        RolResponse rol = new RolResponse(1L, "CLIENTE", "Rol cliente", true, Set.of("VER_AGENDA"));
        when(rolPermisoService.asignarPermiso(anyLong(), any())).thenReturn(rol);
        when(rolPermisoService.revocarPermiso(anyLong(), any())).thenReturn(rol);
        when(rolPermisoService.tienePermiso(1L, "VER_AGENDA")).thenReturn(true);

        mockMvc.perform(put("/api/roles/1/permisos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"permiso\":\"VER_REPORTES\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/roles/1/permisos/VER_CATALOGO"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/roles/1/permisos/VER_AGENDA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tienePermiso").value(true));
        mockMvc.perform(delete("/api/roles/1"))
                .andExpect(status().isOk());
    }
}
