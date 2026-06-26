package cl.vetnova.envio.client;

import cl.vetnova.envio.exception.RemoteServiceException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class InventarioClient {
    private static final Logger log = LoggerFactory.getLogger(InventarioClient.class);
    private final WebClient webClient;

    public InventarioClient(WebClient.Builder builder,
                            @Value("${app.inventario-service-url}") String inventarioUrl) {
        this.webClient = builder.baseUrl(inventarioUrl).build();
    }

    private Long buscarInventarioId(Long productoId, String sucursal) {
        try {
            Map<?, ?> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/inventarios/buscar")
                            .queryParam("productoId", productoId)
                            .queryParam("sucursal", sucursal)
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            if (response == null || response.get("id") == null) {
                throw new RemoteServiceException("No se encontró inventario para producto " + productoId + " en sucursal " + sucursal);
            }
            return ((Number) response.get("id")).longValue();
        } catch (RemoteServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RemoteServiceException("Error buscando inventario: " + ex.getMessage());
        }
    }

    public void registrarMovimiento(Long productoId, String sucursal, String tipo, Integer cantidad, String motivo) {
        try {
            Long inventarioId = buscarInventarioId(productoId, sucursal);
            Map<String, Object> body;
            String endpoint;
            if ("SALIDA".equals(tipo)) {
                endpoint = "/api/v1/inventarios/{id}/salida";
                body = Map.of("cantidad", cantidad, "motivo", motivo);
            } else {
                endpoint = "/api/v1/inventarios/{id}/entrada";
                body = Map.of("cantidad", cantidad, "responsable", motivo);
            }
            webClient.post()
                    .uri(endpoint, inventarioId)
                    .bodyValue(body)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            log.info("event=remote_movimiento_inventario productoId={} tipo={} cantidad={}", productoId, tipo, cantidad);
        } catch (RemoteServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RemoteServiceException("No se pudo registrar el movimiento en Inventario: " + ex.getMessage());
        }
    }
}
