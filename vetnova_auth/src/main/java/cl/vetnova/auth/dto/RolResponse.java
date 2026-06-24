package cl.vetnova.auth.dto;

import cl.vetnova.auth.model.RolPermiso;
import java.util.Set;

public record RolResponse(
        Long id,
        String nombreRol,
        String descripcion,
        Boolean activo,
        Set<String> permisos
) {
    public static RolResponse from(RolPermiso rol) {
        return new RolResponse(rol.getId(), rol.getNombreRol(), rol.getDescripcion(), rol.getActivo(), rol.getPermisos());
    }
}
