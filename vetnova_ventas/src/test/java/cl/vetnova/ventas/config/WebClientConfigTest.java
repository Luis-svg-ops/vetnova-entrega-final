package cl.vetnova.ventas.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class WebClientConfigTest {

    @Test
    void testWebClientBuilderSeCrea() {
        WebClientConfig config = new WebClientConfig();

        assertNotNull(config.webClientBuilder());
    }
}
