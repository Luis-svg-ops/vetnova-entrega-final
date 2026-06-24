package cl.vetnova.facturacion.client;

import cl.vetnova.facturacion.exception.RemoteServiceException;
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

    public boolean ordenConfirmada(Long ordenId) {
        try {
            Map<?, ?> response = webClient.get()
                    .uri("/api/v1/ordenes/{id}", ordenId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            boolean confirmada = response != null && "CONFIRMADA".equals(response.get("estado"));
            log.info("event=remote_orden_confirmada ordenId={} confirmada={}", ordenId, confirmada);
            return confirmada;
        } catch (Exception ex) {
            throw new RemoteServiceException("No se pudo validar el estado de la orden en Ventas: " + ex.getMessage());
        }
    }
}
