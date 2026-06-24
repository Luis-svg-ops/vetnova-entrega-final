package cl.vetnova.auth.controller;

import cl.vetnova.auth.dto.ApiResponse;
import cl.vetnova.auth.dto.AsignarPermisoRequest;
import cl.vetnova.auth.dto.RolRequest;
import cl.vetnova.auth.dto.RolResponse;
import cl.vetnova.auth.service.RolPermisoService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles")
public class RolPermisoController {
    private final RolPermisoService service;

    public RolPermisoController(RolPermisoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RolResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok("Roles encontrados", service.listar()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RolResponse>> crear(@RequestBody RolRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Rol creado", service.crear(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RolResponse>> actualizar(@PathVariable Long id, @RequestBody RolRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Rol actualizado", service.actualizar(id, request)));
    }

    @PutMapping("/{id}/permisos")
    public ResponseEntity<ApiResponse<RolResponse>> asignarPermiso(@PathVariable Long id,
                                                                   @RequestBody AsignarPermisoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Permiso asignado correctamente", service.asignarPermiso(id, request.permiso())));
    }

    @DeleteMapping("/{id}/permisos/{permiso}")
    public ResponseEntity<ApiResponse<RolResponse>> revocarPermiso(@PathVariable Long id, @PathVariable String permiso) {
        return ResponseEntity.ok(ApiResponse.ok("Permiso revocado correctamente", service.revocarPermiso(id, permiso)));
    }

    @GetMapping("/{id}/permisos/{permiso}")
    public ResponseEntity<Map<String, Object>> tienePermiso(@PathVariable Long id, @PathVariable String permiso) {
        return ResponseEntity.ok(Map.of("tienePermiso", service.tienePermiso(id, permiso)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Rol eliminado correctamente", null));
    }
}
