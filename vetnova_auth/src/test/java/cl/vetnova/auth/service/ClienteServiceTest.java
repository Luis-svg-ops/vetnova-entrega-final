package cl.vetnova.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cl.vetnova.auth.dto.ActualizarClienteRequest;
import cl.vetnova.auth.dto.CrearClienteRequest;
import cl.vetnova.auth.exception.BusinessRuleException;
import cl.vetnova.auth.exception.ConflictException;
import cl.vetnova.auth.exception.ResourceNotFoundException;
import cl.vetnova.auth.model.Cliente;
import cl.vetnova.auth.model.RolPermiso;
import cl.vetnova.auth.model.Usuario;
import cl.vetnova.auth.repository.ClienteRepository;
import cl.vetnova.auth.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @InjectMocks
    private ClienteService clienteService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usuario = new Usuario("Juan", "juan@mail.com", "+56911111111", "hash",
                new RolPermiso("CLIENTE", "desc", Set.of("VER_PERFIL")));
    }

    private CrearClienteRequest request(Long usuarioId, String rut, String nombre, String apellido,
                                        String email, String telefono) {
        return new CrearClienteRequest(usuarioId, rut, nombre, apellido, email, telefono, "Calle 123");
    }

    private CrearClienteRequest valido() {
        return request(1L, "11.111.111-1", "Juan", "Pérez", "juan@mail.com", "+56912345678");
    }

    private void usuarioExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
    }

    @Test
    void testListarYObtenerPorId() {
        when(clienteRepository.findAll()).thenReturn(List.of(new Cliente()));
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(new Cliente()));
        assertEquals(1, clienteService.listar().size());
        assertNotNull(clienteService.obtenerPorId(1L));
    }

    @Test
    void testObtenerPorIdInexistenteLanzaNotFound() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> clienteService.obtenerPorId(99L));
    }

    @Test
    void testCrearUsuarioIdNullLanzaBusinessRule() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> clienteService.crear(request(null, "11.111.111-1", "Juan", "Pérez", "juan@mail.com", null)));
        assertEquals("El usuarioId es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearUsuarioInexistenteLanzaNotFound() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> clienteService.crear(request(99L, "11.111.111-1", "Juan", "Pérez", "juan@mail.com", null)));
    }

    @Test
    void testCrearUsuarioSinRolClienteLanzaBusinessRule() {
        Usuario vet = new Usuario("Vet", "vet@mail.com", "+569", "hash", new RolPermiso("VETERINARIO", "d", Set.of("VER_FICHA")));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(vet));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> clienteService.crear(valido()));
        assertEquals("El usuario debe tener rol CLIENTE", ex.getMessage());
    }

    @Test
    void testCrearUsuarioYaTienePerfilLanzaConflict() {
        usuarioExiste();
        when(clienteRepository.existsByUsuarioId(1L)).thenReturn(true);
        assertThrows(ConflictException.class, () -> clienteService.crear(valido()));
    }

    @Test
    void testCrearRutNullLanzaBusinessRule() {
        usuarioExiste();
        when(clienteRepository.existsByUsuarioId(1L)).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> clienteService.crear(request(1L, null, "Juan", "Pérez", "juan@mail.com", null)));
        assertEquals("El RUT es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearRutInvalidoLanzaBusinessRule() {
        usuarioExiste();
        when(clienteRepository.existsByUsuarioId(1L)).thenReturn(false);
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> clienteService.crear(request(1L, "99999999-0", "Juan", "Pérez", "juan@mail.com", null)));
        assertEquals("El RUT no tiene un formato válido o el dígito verificador es incorrecto", ex.getMessage());
    }

    @Test
    void testCrearRutDuplicadoLanzaConflict() {
        usuarioExiste();
        when(clienteRepository.existsByUsuarioId(1L)).thenReturn(false);
        when(clienteRepository.existsByRut("11.111.111-1")).thenReturn(true);
        assertThrows(ConflictException.class, () -> clienteService.crear(valido()));
    }

    @Test
    void testCrearNombreNullLanzaBusinessRule() {
        usuarioExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> clienteService.crear(request(1L, "11.111.111-1", null, "Pérez", "juan@mail.com", null)));
        assertEquals("El nombre es obligatorio", ex.getMessage());
    }

    @Test
    void testCrearNombreVacioLanzaBusinessRule() {
        usuarioExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> clienteService.crear(request(1L, "11.111.111-1", "   ", "Pérez", "juan@mail.com", null)));
        assertEquals("El nombre no puede estar vacío", ex.getMessage());
    }

    @Test
    void testCrearApellidoNullLanzaBusinessRule() {
        usuarioExiste();
        assertThrows(BusinessRuleException.class,
                () -> clienteService.crear(request(1L, "11.111.111-1", "Juan", null, "juan@mail.com", null)));
    }

    @Test
    void testCrearApellidoVacioLanzaBusinessRule() {
        usuarioExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> clienteService.crear(request(1L, "11.111.111-1", "Juan", "  ", "juan@mail.com", null)));
        assertEquals("El apellido no puede estar vacío", ex.getMessage());
    }

    @Test
    void testCrearEmailFormatoInvalidoLanzaBusinessRule() {
        usuarioExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> clienteService.crear(request(1L, "11.111.111-1", "Juan", "Pérez", "juanmail.com", null)));
        assertEquals("El email no tiene un formato válido", ex.getMessage());
    }

    @Test
    void testCrearEmailNullLanzaCoherencia() {
        usuarioExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> clienteService.crear(request(1L, "11.111.111-1", "Juan", "Pérez", null, null)));
        assertEquals("El email debe coincidir con el del Usuario asociado", ex.getMessage());
    }

    @Test
    void testCrearEmailNoCoherenteLanzaBusinessRule() {
        usuarioExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> clienteService.crear(request(1L, "11.111.111-1", "Juan", "Pérez", "otro@mail.com", null)));
        assertEquals("El email debe coincidir con el del Usuario asociado", ex.getMessage());
    }

    @Test
    void testCrearTelefonoInvalidoLanzaBusinessRule() {
        usuarioExiste();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> clienteService.crear(request(1L, "11.111.111-1", "Juan", "Pérez", "juan@mail.com", "abc123")));
        assertEquals("El formato de teléfono no es válido", ex.getMessage());
    }

    @Test
    void testCrearClienteCasoFelizConTelefono() {
        usuarioExiste();
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));
        Cliente creado = clienteService.crear(valido());
        assertTrue(creado.getActivo());
        assertNotNull(creado.getFechaRegistro());
        assertEquals("11.111.111-1", creado.getRut());
    }

    @Test
    void testCrearClienteCasoFelizSinTelefono() {
        usuarioExiste();
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));
        Cliente creado = clienteService.crear(request(1L, "11.111.111-1", "Juan", "Pérez", "juan@mail.com", null));
        assertTrue(creado.getActivo());
        assertNull(creado.getTelefono());
    }

    @Test
    void testActualizarTelefonoValido() {
        Cliente cliente = new Cliente();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));
        Cliente r = clienteService.actualizarDatos(1L, new ActualizarClienteRequest("+56987654321", null));
        assertEquals("+56987654321", r.getTelefono());
    }

    @Test
    void testActualizarTelefonoInvalidoLanzaBusinessRule() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(new Cliente()));
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> clienteService.actualizarDatos(1L, new ActualizarClienteRequest("noestelefono", null)));
        assertEquals("El formato de teléfono no es válido", ex.getMessage());
    }

    @Test
    void testActualizarSoloDireccion() {
        Cliente cliente = new Cliente();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));
        Cliente r = clienteService.actualizarDatos(1L, new ActualizarClienteRequest(null, "Calle Nueva 123"));
        assertEquals("Calle Nueva 123", r.getDireccion());
    }

    @Test
    void testDesactivarCliente() {
        Cliente cliente = new Cliente();
        cliente.setActivo(true);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));
        assertFalse(clienteService.desactivar(1L).getActivo());
    }
}
