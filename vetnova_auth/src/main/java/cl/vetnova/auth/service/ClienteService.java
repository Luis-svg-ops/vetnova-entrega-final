package cl.vetnova.auth.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.vetnova.auth.dto.ActualizarClienteRequest;
import cl.vetnova.auth.dto.CrearClienteRequest;
import cl.vetnova.auth.exception.BusinessRuleException;
import cl.vetnova.auth.exception.ConflictException;
import cl.vetnova.auth.exception.ResourceNotFoundException;
import cl.vetnova.auth.model.Cliente;
import cl.vetnova.auth.model.Usuario;
import cl.vetnova.auth.repository.ClienteRepository;
import cl.vetnova.auth.repository.UsuarioRepository;
import cl.vetnova.auth.util.RutValidator;

@Service
public class ClienteService {
    private static final Logger log = LoggerFactory.getLogger(ClienteService.class);
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern TELEFONO = Pattern.compile("^\\+?\\d{8,15}$");

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    public ClienteService(ClienteRepository clienteRepository, UsuarioRepository usuarioRepository) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    public Cliente obtenerPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id " + id));
    }

    @Transactional
    public Cliente crear(CrearClienteRequest request) {
        if (request.usuarioId() == null) {
            throw new BusinessRuleException("El usuarioId es obligatorio");
        }
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        if (!"CLIENTE".equals(usuario.getRol().getNombreRol())) {
            throw new BusinessRuleException("El usuario debe tener rol CLIENTE");
        }
        if (clienteRepository.existsByUsuarioId(request.usuarioId())) {
            throw new ConflictException("Este usuario ya tiene un perfil de cliente");
        }
        if (request.rut() == null) {
            throw new BusinessRuleException("El RUT es obligatorio");
        }
        if (!RutValidator.esValido(request.rut())) {
            throw new BusinessRuleException("El RUT no tiene un formato válido o el dígito verificador es incorrecto");
        }
        if (clienteRepository.existsByRut(request.rut())) {
            throw new ConflictException("El RUT ya está registrado");
        }
        validarNombre(request.nombre());
        validarApellido(request.apellido());
        if (request.email() != null && !EMAIL.matcher(request.email()).matches()) {
            throw new BusinessRuleException("El email no tiene un formato válido");
        }
        if (!usuario.getEmail().equalsIgnoreCase(request.email())) {
            throw new BusinessRuleException("El email debe coincidir con el del Usuario asociado");
        }
        if (request.telefono() != null && !TELEFONO.matcher(request.telefono()).matches()) {
            throw new BusinessRuleException("El formato de teléfono no es válido");
        }
        Cliente cliente = new Cliente();
        cliente.setUsuarioId(request.usuarioId());
        cliente.setRut(request.rut());
        cliente.setNombre(request.nombre());
        cliente.setApellido(request.apellido());
        cliente.setEmail(request.email());
        cliente.setTelefono(request.telefono());
        cliente.setDireccion(request.direccion());
        cliente.setActivo(true);
        cliente.setFechaRegistro(LocalDateTime.now());
        Cliente guardado = clienteRepository.save(cliente);
        log.info("event=cliente_creado clienteId={} usuarioId={}", guardado.getId(), guardado.getUsuarioId());
        return guardado;
    }

    @Transactional
    public Cliente actualizarDatos(Long id, ActualizarClienteRequest request) {
        Cliente cliente = obtenerPorId(id);
        if (request.telefono() != null) {
            if (!TELEFONO.matcher(request.telefono()).matches()) {
                throw new BusinessRuleException("El formato de teléfono no es válido");
            }
            cliente.setTelefono(request.telefono());
        }
        if (request.direccion() != null) {
            cliente.setDireccion(request.direccion());
        }
        log.info("event=cliente_actualizado clienteId={}", id);
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente desactivar(Long id) {
        Cliente cliente = obtenerPorId(id);
        cliente.setActivo(false);
        log.info("event=cliente_desactivado clienteId={}", id);
        return clienteRepository.save(cliente);
    }

    private void validarNombre(String nombre) {
        if (nombre == null) {
            throw new BusinessRuleException("El nombre es obligatorio");
        }
        if (nombre.isBlank()) {
            throw new BusinessRuleException("El nombre no puede estar vacío");
        }
    }

    private void validarApellido(String apellido) {
        if (apellido == null) {
            throw new BusinessRuleException("El apellido es obligatorio");
        }
        if (apellido.isBlank()) {
            throw new BusinessRuleException("El apellido no puede estar vacío");
        }
    }
}
