package cl.vetnova.laboratorio.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import cl.vetnova.laboratorio.exception.RemoteServiceException;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class AuthClientTest {

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
    void testUsuarioExisteDevuelveTrue() {
        AuthClient client = new AuthClient(builderSimulado(), "http://localhost:8081");
        simularGet(Mono.just(Map.of("id", 4L, "existe", true)));

        assertTrue(client.usuarioExiste(4L));
    }

    @Test
    void testUsuarioExisteConRespuestaVaciaDevuelveFalse() {
        AuthClient client = new AuthClient(builderSimulado(), "http://localhost:8081");
        simularGet(Mono.empty());

        assertFalse(client.usuarioExiste(4L));
    }

    @Test
    void testUsuarioExisteConAuthCaidoLanzaRemoteServiceException() {
        AuthClient client = new AuthClient(builderSimulado(), "http://localhost:8081");
        simularGet(Mono.error(new RuntimeException("conexion rechazada")));

        assertThrows(RemoteServiceException.class, () -> client.usuarioExiste(4L));
    }

    @Test
    void testObtenerRolDevuelveElRol() {
        AuthClient client = new AuthClient(builderSimulado(), "http://localhost:8081");
        simularGet(Mono.just(Map.of("rol", "VETERINARIO")));

        assertEquals("VETERINARIO", client.obtenerRol(4L));
    }

    @Test
    void testObtenerRolConErrorLanzaRemoteServiceException() {
        AuthClient client = new AuthClient(builderSimulado(), "http://localhost:8081");
        simularGet(Mono.error(new RuntimeException("timeout")));

        assertThrows(RemoteServiceException.class, () -> client.obtenerRol(4L));
    }

    @Test
    void testObtenerRolSinRespuestaDevuelveNull() {
        AuthClient client = new AuthClient(builderSimulado(), "http://localhost:8081");
        simularGet(Mono.empty());

        assertNull(client.obtenerRol(2L));
    }

    @Test
    void testUsuarioExisteConExisteFalseDevuelveFalse() {
        AuthClient client = new AuthClient(builderSimulado(), "http://localhost:8081");
        simularGet(Mono.just(Map.of("id", 4L, "existe", false)));

        assertFalse(client.usuarioExiste(4L));
    }

}