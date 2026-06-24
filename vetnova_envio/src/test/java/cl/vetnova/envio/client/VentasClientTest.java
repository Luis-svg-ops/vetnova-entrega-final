package cl.vetnova.envio.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import cl.vetnova.envio.exception.RemoteServiceException;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class VentasClientTest {

private WebClient webClient;

    private WebClient.Builder builderSimulado() {
        WebClient.Builder builder = mock(WebClient.Builder.class);
        webClient = mock(WebClient.class, RETURNS_DEEP_STUBS);
        when(builder.baseUrl(anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(webClient);
        return builder;
    }

    private void simularGet(Mono<Map> respuesta) {
        when(webClient.get().uri(anyString(), any(Object[].class)).retrieve().bodyToMono(Map.class))
                .thenReturn(respuesta);
    }

    private void simularPost(Mono<ResponseEntity<Void>> respuesta) {
        when(webClient.post().uri(anyString()).bodyValue(any()).retrieve().toBodilessEntity())
                .thenReturn(respuesta);
    }

    @Test
    void testOrdenExisteDevuelveTrue() {
        VentasClient client = new VentasClient(builderSimulado(), "http://localhost:8084");
        simularGet(Mono.just(Map.of("id", 1L, "existe", true)));

        assertTrue(client.ordenExiste(1L));
    }

    @Test
    void testOrdenExisteConRespuestaVaciaDevuelveFalse() {
        VentasClient client = new VentasClient(builderSimulado(), "http://localhost:8084");
        simularGet(Mono.empty());

        assertFalse(client.ordenExiste(1L));
    }

    @Test
    void testOrdenExisteConVentasCaidoLanzaRemoteServiceException() {
        VentasClient client = new VentasClient(builderSimulado(), "http://localhost:8084");
        simularGet(Mono.error(new RuntimeException("conexion rechazada")));

        assertThrows(RemoteServiceException.class, () -> client.ordenExiste(1L));
    }

    @Test
    void testOrdenExisteConExisteFalseDevuelveFalse() {
        VentasClient client = new VentasClient(builderSimulado(), "http://localhost:8084");
        simularGet(Mono.just(Map.of("id", 1L, "existe", false)));

        assertFalse(client.ordenExiste(1L));
    }

}