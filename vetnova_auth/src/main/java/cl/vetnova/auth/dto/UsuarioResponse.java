package cl.vetnova.auth.dto;

import cl.vetnova.auth.model.Usuario;
import java.time.LocalDateTime;
import java.util.Set;

public record UsuarioResponse(
        Long id,
        String nombre,
        String email,
        String telefono,
        String rol,
        Set<String> permisos,
        Boolean activo,
        LocalDateTime fechaCreacion
) {
    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getRol().getNombreRol(),
                usuario.getRol().getPermisos(),
                usuario.getActivo(),
                usuario.getFechaCreacion()
        );
    }
}
