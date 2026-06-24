package cl.vetnova.envio.client;

import cl.vetnova.envio.exception.RemoteServiceException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class VentasClient {
    private static final Logger log = LoggerFactory.getLogger(VentasClient.class);
    private final WebClient webClient;

    public VentasClient(WebClient.Builder builder,
                        @Value("${app.ventas-service-url}") String ventasUrl) {
        this.webClient = builder.baseUrl(ventasUrl).build();
    }

    public boolean ordenExiste(Long ordenId) {
        try {
            Map<?, ?> response = webClient.get()
                    .uri("/api/v1/ordenes/{id}/existe", ordenId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            boolean existe = response != null && Boolean.TRUE.equals(response.get("existe"));
            log.info("event=remote_orden_existe ordenId={} existe={}", ordenId, existe);
            return existe;
        } catch (Exception ex) {
            throw new RemoteServiceException("No se pudo validar la orden en Ventas: " + ex.getMessage());
        }
    }
}
