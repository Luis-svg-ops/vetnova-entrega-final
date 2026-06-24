package cl.vetnova.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.vetnova.auth.dto.AuthResponse;
import cl.vetnova.auth.dto.UsuarioResponse;
import cl.vetnova.auth.service.AuthService;
import cl.vetnova.auth.service.RolPermisoService;
import cl.vetnova.auth.service.UsuarioService;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;
    @MockBean
    private UsuarioService usuarioService;
    @MockBean
    private RolPermisoService rolPermisoService;

    private UsuarioResponse usuarioResponse() {
        return new UsuarioResponse(2L, "Fernanda Soto", "fernanda@vetnova.cl", "+56911111111",
                "CLIENTE", Set.of("AGENDAR_CITA"), true, LocalDateTime.now());
    }

    @Test
    void testRegisterResponde201ConElUsuario() throws Exception {
        when(authService.registrar(any())).thenReturn(usuarioResponse());

        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Fernanda Soto","email":"fernanda@vetnova.cl",
                                 "telefono":"+56911111111","password":"Clave12345","rol":"CLIENTE"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email").value("fernanda@vetnova.cl"));
    }

    @Test
    void testRegisterSinEmailResponde400() throws Exception {
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Fernanda Soto","telefono":"+56911111111",
                                 "password":"Clave12345","rol":"CLIENTE"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    void testLoginDevuelveElToken() throws Exception {
        when(authService.login(any(), any()))
                .thenReturn(new AuthResponse("token-123", LocalDateTime.now().plusMinutes(120), usuarioResponse()));

        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"fernanda@vetnova.cl\",\"password\":\"Clave12345\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("token-123"));
    }

    @Test
    void testLogoutYValidateRespondenOk() throws Exception {
        mockMvc.perform(post("/api/auth/logout").param("token", "token-123"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/auth/validate").param("token", "token-123"))
                .andExpect(status().isOk());
    }

    @Test
    void testActualizarPerfilYPasswordRespondenOk() throws Exception {
        when(usuarioService.actualizarPerfil(anyLong(), any())).thenReturn(usuarioResponse());

        mockMvc.perform(put("/api/auth/usuarios/2/perfil").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Fernanda Soto M.\",\"telefono\":\"+56922222222\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/api/auth/usuarios/2/password").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"actual\":\"Clave12345\",\"nueva\":\"NuevaClave99\"}"))
                .andExpect(status().isOk());
    }
}
