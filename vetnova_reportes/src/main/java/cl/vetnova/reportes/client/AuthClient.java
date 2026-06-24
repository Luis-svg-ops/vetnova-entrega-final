package cl.vetnova.reportes.client;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


    @Component
public class AuthClient {

    private final RestTemplate restTemplate;

    public AuthClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean usuarioExiste(Long usuarioId) {
        try {
            String url = "http://localhost:8081/api/usuarios/" + usuarioId + "/existe";
            Map response = restTemplate.getForObject(url, Map.class);
            return response != null && Boolean.TRUE.equals(response.get("existe"));
        } catch (Exception e) {
            return false;
        }
    }
}

