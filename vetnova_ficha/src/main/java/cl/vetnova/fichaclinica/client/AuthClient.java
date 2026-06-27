package cl.vetnova.fichaclinica.client;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthClient {

    private static final String AUTH_URL = "http://localhost:8081";

    @Autowired
    private RestTemplate restTemplate;

    public String obtenerNombreCliente(Long clienteId) {
        try {
            Map<String, Object> cliente = restTemplate.exchange(
                    AUTH_URL + "/api/v1/clientes/" + clienteId,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            ).getBody();
            if (cliente == null) return null;
            String nombre = (String) cliente.get("nombre");
            String apellido = (String) cliente.get("apellido");
            return (nombre != null ? nombre : "") + (apellido != null ? " " + apellido : "");
        } catch (Exception e) {
            return null;
        }
    }

    public boolean usuarioExiste(Long usuarioId) {
        try {
            Boolean existe = restTemplate.getForObject(
                    AUTH_URL + "/api/usuarios/" + usuarioId + "/existe",
                    Boolean.class);
            return Boolean.TRUE.equals(existe);
        } catch (Exception e) {
            return false;
        }
    }
}
