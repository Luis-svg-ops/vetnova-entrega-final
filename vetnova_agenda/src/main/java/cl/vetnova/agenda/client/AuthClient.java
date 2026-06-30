package cl.vetnova.agenda.client;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import cl.vetnova.agenda.exception.ResourceNotFoundException;

/**
 * Cliente HTTP que comunica vetnova_agenda (8086) con vetnova_auth (8081).
 * Tiene dos tipos de operaciones: verificación dura (falla si el servicio no responde) y
 * obtención de nombre con degradación suave (retorna null si falla sin interrumpir el flujo).
 */
@Component
public class AuthClient {

    private static final Logger log = LoggerFactory.getLogger(AuthClient.class);

    @Autowired
    private RestTemplate restTemplate;

    // URL base de vetnova_auth; configurable desde application.properties para distintos entornos
    @Value("${app.auth-service-url:http://localhost:8081}")
    private String authUrl;

    /**
     * Obtiene el nombre completo (nombre + apellido) de un usuario desde vetnova_auth.
     * Degradación suave: si auth no está disponible o el usuario no existe, retorna null sin lanzar excepción.
     * @param usuarioId id del usuario (cliente o veterinario)
     * @return nombre completo o null si no se pudo obtener
     */
    public String obtenerNombre(Long usuarioId) {
        if (usuarioId == null) return null;
        try {
            Map<String, Object> usuario = restTemplate.exchange(
                    authUrl + "/api/v1/usuarios/" + usuarioId,
                    HttpMethod.GET, null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
            if (usuario == null) return null;
            String nombre = (String) usuario.get("nombre");
            String apellido = (String) usuario.get("apellido");
            return ((nombre != null ? nombre : "") + (apellido != null ? " " + apellido : "")).strip();
        } catch (Exception e) {
            log.warn("event=auth_no_disponible usuarioId={} — nombre no obtenido: {}", usuarioId, e.getMessage());
            return null;
        }
    }

    /**
     * Verifica que el cliente exista en vetnova_auth antes de crear una cita (validación dura).
     * Si auth no responde o el cliente no existe, lanza ResourceNotFoundException y la cita NO se crea.
     * @param clienteId id del cliente a verificar
     */
    public void verificarCliente(Long clienteId) {
        try {
            Map<?, ?> resp = restTemplate.exchange(
                    authUrl + "/api/usuarios/" + clienteId + "/existe",
                    HttpMethod.GET, null,
                    new ParameterizedTypeReference<Map<?, ?>>() {}).getBody();
            if (resp == null || !Boolean.TRUE.equals(resp.get("existe"))) {
                throw new ResourceNotFoundException("Cliente no encontrado en el sistema");
            }
        } catch (ResourceNotFoundException ex) {
            // Re-lanzamos para que la excepción de negocio llegue al controlador sin envolverse
            throw ex;
        } catch (Exception e) {
            // Cualquier otro error de red o de auth se traduce en error de negocio: la cita no se puede crear
            throw new ResourceNotFoundException("No se pudo verificar el cliente en el sistema");
        }
    }
}
