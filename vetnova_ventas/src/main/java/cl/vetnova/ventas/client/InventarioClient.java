package cl.vetnova.ventas.client;

import cl.vetnova.ventas.exception.RemoteServiceException;
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

    public Integer consultarStock(Long productoId, String sucursal) {
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
            Integer disponible = response == null ? 0 : ((Number) response.get("stockDisponible")).intValue();
            log.info("event=remote_consulta_stock productoId={} sucursal={} disponible={}", productoId, sucursal, disponible);
            return disponible;
        } catch (Exception ex) {
            throw new RemoteServiceException("No se pudo consultar stock en Inventario: " + ex.getMessage());
        }
    }

    public void registrarSalida(Long productoId, String sucursal, Integer cantidad, String motivo) {
        try {
            Long inventarioId = buscarInventarioId(productoId, sucursal);
            Map<String, Object> body = Map.of("cantidad", cantidad, "motivo", motivo);
            webClient.post()
                    .uri("/api/v1/inventarios/{id}/salida", inventarioId)
                    .bodyValue(body)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            log.info("event=remote_salida_stock productoId={} cantidad={}", productoId, cantidad);
        } catch (RemoteServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RemoteServiceException("No se pudo descontar stock en Inventario: " + ex.getMessage());
        }
    }

    public void registrarEntrada(Long productoId, String sucursal, Integer cantidad, String responsable) {
        try {
            Long inventarioId = buscarInventarioId(productoId, sucursal);
            Map<String, Object> body = Map.of("cantidad", cantidad, "responsable", responsable);
            webClient.post()
                    .uri("/api/v1/inventarios/{id}/entrada", inventarioId)
                    .bodyValue(body)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            log.info("event=remote_entrada_stock productoId={} cantidad={}", productoId, cantidad);
        } catch (RemoteServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RemoteServiceException("No se pudo reponer stock en Inventario: " + ex.getMessage());
        }
    }
}
