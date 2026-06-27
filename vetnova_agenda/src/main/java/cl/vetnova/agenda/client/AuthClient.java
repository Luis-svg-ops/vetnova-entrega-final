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

@Component
public class AuthClient {

    private static final Logger log = LoggerFactory.getLogger(AuthClient.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.auth-service-url:http://localhost:8081}")
    private String authUrl;

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
            throw ex;
        } catch (Exception e) {
            throw new ResourceNotFoundException("No se pudo verificar el cliente en el sistema");
        }
    }
}
