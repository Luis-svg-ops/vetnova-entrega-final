package cl.vetnova.ventas.client;

import cl.vetnova.ventas.exception.RemoteServiceException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AuthClient {

    private final WebClient webClient;

    public AuthClient(WebClient.Builder builder,
                      @Value("${app.auth-service-url}") String authUrl) {
        this.webClient = builder.baseUrl(authUrl).build();
    }

    public boolean clienteExiste(Long clienteId) {
        try {
            Map<?, ?> response = webClient.get()
                    .uri("/api/usuarios/{id}/existe", clienteId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            return response != null && Boolean.TRUE.equals(response.get("existe"));
        } catch (Exception ex) {
            throw new RemoteServiceException("No se pudo verificar el cliente en Auth: " + ex.getMessage());
        }
    }
}
