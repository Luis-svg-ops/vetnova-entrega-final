package cl.vetnova.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.vetnova.auth.dto.CrearUsuarioRequest;
import cl.vetnova.auth.dto.UsuarioResponse;
import cl.vetnova.auth.exception.BusinessRuleException;
import cl.vetnova.auth.exception.ConflictException;
import cl.vetnova.auth.exception.ResourceNotFoundException;
import cl.vetnova.auth.service.UsuarioService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UsuarioController.class)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    private UsuarioResponse usuario() {
        return new UsuarioResponse(1L, "Camila Rojas", "cliente@vetnova.cl", "+56911111111",
                "CLIENTE", Set.of("VER_PERFIL"), true, LocalDateTime.now());
    }

    @Test
    void testCrearUsuarioValidoResponde201() throws Exception {
        when(usuarioService.crear(any(CrearUsuarioRequest.class))).thenReturn(usuario());

        String body = """
                {"nombre":"Camila Rojas","email":"cliente@vetnova.cl","telefono":"+56911111111",
                 "password":"Pass1234!","nombreRol":"CLIENTE"}
                """;
        mockMvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email").value("cliente@vetnova.cl"));
    }

    @Test
    void testCrearUsuarioInvalidoResponde400() throws Exception {
        when(usuarioService.crear(any(CrearUsuarioRequest.class)))
                .thenThrow(new BusinessRuleException("El email no tiene un formato válido"));

        mockMvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"juanmail.com\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCrearUsuarioConEmailDuplicadoResponde409() throws Exception {
        when(usuarioService.crear(any(CrearUsuarioRequest.class)))
                .thenThrow(new ConflictException("El email ya está registrado"));

        mockMvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"juan@mail.com\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void testCambiarPasswordResponde200() throws Exception {
        String body = "{\"passwordActual\":\"Pass1234!\",\"passwordNuevo\":\"NuevaClave9!\"}";
        mockMvc.perform(put("/api/usuarios/1/password").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password actualizado correctamente"));
    }

    @Test
    void testEliminarDesactivaUsuarioResponde200() throws Exception {
        UsuarioResponse desactivado = new UsuarioResponse(1L, "Camila Rojas", "cliente@vetnova.cl",
                "+56911111111", "CLIENTE", Set.of(), false, LocalDateTime.now());
        when(usuarioService.desactivar(1L)).thenReturn(desactivado);

        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.activo").value(false));
    }

    @Test
    void testListarRespondeLosUsuarios() throws Exception {
        when(usuarioService.listar()).thenReturn(List.of(usuario()));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].email").value("cliente@vetnova.cl"));
    }

    @Test
    void testBuscarUsuarioExistenteRespondeOk() throws Exception {
        when(usuarioService.buscar(1L)).thenReturn(usuario());

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nombre").value("Camila Rojas"));
    }

    @Test
    void testBuscarUsuarioInexistenteResponde404() throws Exception {
        when(usuarioService.buscar(99L)).thenThrow(new ResourceNotFoundException("Usuario no encontrado: 99"));

        mockMvc.perform(get("/api/usuarios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testExisteRespondeElBooleanQueConsumenOtrosServicios() throws Exception {
        when(usuarioService.existe(2L)).thenReturn(true);

        mockMvc.perform(get("/api/usuarios/2/existe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.existe").value(true));
    }

    @Test
    void testDesactivarRespondeElUsuarioActualizado() throws Exception {
        UsuarioResponse desactivado = new UsuarioResponse(1L, "Camila Rojas", "cliente@vetnova.cl",
                "+56911111111", "CLIENTE", Set.of(), false, LocalDateTime.now());
        when(usuarioService.desactivar(1L)).thenReturn(desactivado);

        mockMvc.perform(patch("/api/usuarios/1/desactivar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.activo").value(false));
    }

    @Test
    void testRolDelUsuarioRespondeOk() throws Exception {
        when(usuarioService.buscar(2L)).thenReturn(new UsuarioResponse(2L, "Fernanda Soto", "fernanda@vetnova.cl",
                "+56911111111", "CLIENTE", java.util.Set.of("AGENDAR_CITA"), true, java.time.LocalDateTime.now()));

        mockMvc.perform(get("/api/usuarios/2/rol"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("CLIENTE"));
    }

    @Test
    void testActivarUsuarioRespondeOk() throws Exception {
        mockMvc.perform(patch("/api/usuarios/2/activar")).andExpect(status().isOk());
    }
}
