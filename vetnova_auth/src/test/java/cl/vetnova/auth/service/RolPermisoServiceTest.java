package cl.vetnova.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cl.vetnova.auth.dto.RolRequest;
import cl.vetnova.auth.dto.RolResponse;
import cl.vetnova.auth.exception.BusinessRuleException;
import cl.vetnova.auth.exception.ConflictException;
import cl.vetnova.auth.exception.ResourceNotFoundException;
import cl.vetnova.auth.model.RolPermiso;
import cl.vetnova.auth.repository.RolPermisoRepository;
import cl.vetnova.auth.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RolPermisoServiceTest {

    @Mock
    private RolPermisoRepository repository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private RolPermisoService rolPermisoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private RolPermiso rol(String nombre, Set<String> permisos) {
        return new RolPermiso(nombre, "desc", permisos);
    }

    @Test
    void testListarDevuelveLosRoles() {
        when(repository.findAll()).thenReturn(List.of(rol("CLIENTE", Set.of("AGENDAR_CITA"))));

        List<RolResponse> roles = rolPermisoService.listar();

        assertEquals(1, roles.size());
        assertEquals("CLIENTE", roles.get(0).nombreRol());
    }

    @Test
    void testBuscarEntidadInexistenteLanzaNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> rolPermisoService.buscarEntidad(99L));
    }

    @Test
    void testBuscarPorNombreDevuelveElRol() {
        when(repository.findByNombreRolIgnoreCase("VETERINARIO")).thenReturn(Optional.of(rol("VETERINARIO", Set.of("VER_FICHA"))));
        assertEquals("VETERINARIO", rolPermisoService.buscarPorNombre("VETERINARIO").getNombreRol());
    }

    @Test
    void testBuscarPorNombreInexistenteLanzaNotFound() {
        when(repository.findByNombreRolIgnoreCase("FANTASMA")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> rolPermisoService.buscarPorNombre("FANTASMA"));
    }

    // ----- crear -----

    @Test
    void testCrearConNombreNullLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> rolPermisoService.crear(new RolRequest(null, "desc", Set.of("AGENDAR_CITA"))));
        assertEquals("El nombre del rol es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearConNombreVacioLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> rolPermisoService.crear(new RolRequest("   ", "desc", Set.of("AGENDAR_CITA"))));
        assertEquals("El nombre del rol no puede estar vacío", ex.getMessage());
    }

    @Test
    void testCrearConPermisosNullLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> rolPermisoService.crear(new RolRequest("VETERINARIO", "desc", null)));
        assertEquals("La lista de permisos es obligatoria", ex.getMessage());
    }

    @Test
    void testCrearConPermisosVaciosLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> rolPermisoService.crear(new RolRequest("VETERINARIO", "desc", Set.of())));
        assertEquals("El rol debe tener al menos un permiso", ex.getMessage());
    }

    @Test
    void testCrearConPermisoInvalidoLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> rolPermisoService.crear(new RolRequest("VETERINARIO", "desc", Set.of("VER_TODO"))));
        assertEquals("Permiso no válido: VER_TODO", ex.getMessage());
    }

    @Test
    void testCrearRolGuardaElNombreEnMayusculas() {
        when(repository.existsByNombreRolIgnoreCase("recepcionista")).thenReturn(false);
        when(repository.save(any(RolPermiso.class))).thenAnswer(inv -> inv.getArgument(0));

        RolResponse respuesta = rolPermisoService.crear(new RolRequest("recepcionista", "Rol recepción", Set.of("AGENDAR_CITA")));

        assertEquals("RECEPCIONISTA", respuesta.nombreRol());
        verify(repository).save(any(RolPermiso.class));
    }

    @Test
    void testCrearRolDuplicadoLanzaConflict() {
        when(repository.existsByNombreRolIgnoreCase("CLIENTE")).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> rolPermisoService.crear(new RolRequest("CLIENTE", "Duplicado", Set.of("AGENDAR_CITA"))));
        verify(repository, never()).save(any());
    }

    @Test
    void testActualizarRolCambiaLosDatos() {
        when(repository.findById(1L)).thenReturn(Optional.of(rol("CLIENTE", Set.of("AGENDAR_CITA"))));
        when(repository.save(any(RolPermiso.class))).thenAnswer(inv -> inv.getArgument(0));

        RolResponse respuesta = rolPermisoService.actualizar(1L,
                new RolRequest("cliente", "Rol cliente actualizado", Set.of("AGENDAR_CITA", "VER_CATALOGO")));

        assertEquals("CLIENTE", respuesta.nombreRol());
        assertEquals(2, respuesta.permisos().size());
    }

    // ----- asignarPermiso -----

    @Test
    void testAsignarPermisoInvalidoLanzaBusinessRule() {
        when(repository.findById(1L)).thenReturn(Optional.of(rol("CLIENTE", Set.of("AGENDAR_CITA"))));
        assertThrows(BusinessRuleException.class, () -> rolPermisoService.asignarPermiso(1L, "PERMISO_INVENTADO"));
    }

    @Test
    void testAsignarPermisoYaAsignadoLanzaConflict() {
        when(repository.findById(1L)).thenReturn(Optional.of(rol("CLIENTE", Set.of("VER_AGENDA"))));
        assertThrows(ConflictException.class, () -> rolPermisoService.asignarPermiso(1L, "VER_AGENDA"));
    }

    @Test
    void testAsignarPermisoCasoFeliz() {
        RolPermiso r = rol("CLIENTE", new java.util.LinkedHashSet<>(Set.of("VER_AGENDA")));
        when(repository.findById(1L)).thenReturn(Optional.of(r));
        when(repository.save(any(RolPermiso.class))).thenAnswer(inv -> inv.getArgument(0));

        RolResponse respuesta = rolPermisoService.asignarPermiso(1L, "VER_REPORTES");

        assertTrue(respuesta.permisos().contains("VER_REPORTES"));
    }

    // ----- revocarPermiso -----

    @Test
    void testRevocarPermisoNoAsignadoLanzaBusinessRule() {
        when(repository.findById(1L)).thenReturn(Optional.of(rol("CLIENTE", Set.of("VER_AGENDA"))));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> rolPermisoService.revocarPermiso(1L, "VER_STOCK"));
        assertEquals("El permiso no está asignado a este rol", ex.getMessage());
    }

    @Test
    void testRevocarUnicoPermisoLanzaBusinessRule() {
        when(repository.findById(1L)).thenReturn(Optional.of(rol("CLIENTE", new java.util.LinkedHashSet<>(Set.of("VER_AGENDA")))));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> rolPermisoService.revocarPermiso(1L, "VER_AGENDA"));
        assertEquals("No se puede revocar el único permiso del rol", ex.getMessage());
    }

    @Test
    void testRevocarPermisoCasoFeliz() {
        RolPermiso r = rol("CLIENTE", new java.util.LinkedHashSet<>(Set.of("VER_AGENDA", "VER_CATALOGO")));
        when(repository.findById(1L)).thenReturn(Optional.of(r));
        when(repository.save(any(RolPermiso.class))).thenAnswer(inv -> inv.getArgument(0));

        RolResponse respuesta = rolPermisoService.revocarPermiso(1L, "VER_CATALOGO");

        assertFalse(respuesta.permisos().contains("VER_CATALOGO"));
    }

    // ----- tienePermiso -----

    @Test
    void testTienePermisoDevuelveTrue() {
        when(repository.findById(1L)).thenReturn(Optional.of(rol("CLIENTE", Set.of("VER_AGENDA"))));
        assertTrue(rolPermisoService.tienePermiso(1L, "VER_AGENDA"));
    }

    @Test
    void testTienePermisoDevuelveFalse() {
        when(repository.findById(1L)).thenReturn(Optional.of(rol("CLIENTE", Set.of("VER_AGENDA"))));
        assertFalse(rolPermisoService.tienePermiso(1L, "VER_STOCK"));
    }

    // ----- eliminar -----

    @Test
    void testEliminarRolConUsuariosLanzaBusinessRule() {
        when(repository.findById(1L)).thenReturn(Optional.of(rol("CLIENTE", Set.of("VER_AGENDA"))));
        when(usuarioRepository.existsByRolId(1L)).thenReturn(true);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> rolPermisoService.eliminar(1L));
        assertEquals("No se puede eliminar un rol con usuarios asignados", ex.getMessage());
        verify(repository, never()).deleteById(any());
    }

    @Test
    void testEliminarRolSinUsuariosBorra() {
        when(repository.findById(1L)).thenReturn(Optional.of(rol("CLIENTE", Set.of("VER_AGENDA"))));
        when(usuarioRepository.existsByRolId(1L)).thenReturn(false);

        rolPermisoService.eliminar(1L);

        verify(repository).deleteById(1L);
    }
}
