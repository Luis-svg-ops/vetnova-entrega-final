package cl.vetnova.auth.controller;

import cl.vetnova.auth.dto.ApiResponse;
import cl.vetnova.auth.dto.AuthResponse;
import cl.vetnova.auth.dto.LoginRequest;
import cl.vetnova.auth.dto.PasswordChangeRequest;
import cl.vetnova.auth.dto.RegisterRequest;
import cl.vetnova.auth.dto.UpdatePerfilRequest;
import cl.vetnova.auth.dto.UsuarioResponse;
import cl.vetnova.auth.dto.ValidateTokenResponse;
import cl.vetnova.auth.service.AuthService;
import cl.vetnova.auth.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final UsuarioService usuarioService;

    public AuthController(AuthService authService, UsuarioService usuarioService) {
        this.authService = authService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UsuarioResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Usuario registrado", authService.registrar(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        return ResponseEntity.ok(ApiResponse.ok("Inicio de sesión correcto", authService.login(request, servletRequest)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestParam String token) {
        authService.logout(token);
        return ResponseEntity.ok(ApiResponse.ok("Sesión cerrada", null));
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<ValidateTokenResponse>> validate(@RequestParam String token) {
        return ResponseEntity.ok(ApiResponse.ok("Validación de token", authService.validarToken(token)));
    }

    @PutMapping("/usuarios/{id}/perfil")
    public ResponseEntity<ApiResponse<UsuarioResponse>> actualizarPerfil(@PathVariable Long id, @Valid @RequestBody UpdatePerfilRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Perfil actualizado", usuarioService.actualizarPerfil(id, request)));
    }

    @PutMapping("/usuarios/{id}/password")
    public ResponseEntity<ApiResponse<Void>> cambiarPassword(@PathVariable Long id, @Valid @RequestBody PasswordChangeRequest request) {
        authService.cambiarPassword(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Contraseña actualizada", null));
    }
}
