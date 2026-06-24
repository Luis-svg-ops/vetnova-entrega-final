package cl.vetnova.envio;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

public class EnvioServiceApplicationTest {

    @Test
    void testMainLevantaLaAplicacion() {
        assertDoesNotThrow(() -> EnvioServiceApplication.main(new String[] {
                "--server.port=0",
                "--spring.datasource.url=jdbc:h2:mem:enviocovdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE"
        }));
    }
}
