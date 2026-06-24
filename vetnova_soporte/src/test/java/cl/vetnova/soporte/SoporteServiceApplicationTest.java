package cl.vetnova.soporte;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

public class SoporteServiceApplicationTest {

    @Test
    void testMainLevantaLaAplicacion() {
        assertDoesNotThrow(() -> SoporteServiceApplication.main(new String[] {
                "--server.port=0",
                "--spring.datasource.url=jdbc:h2:mem:soportecovdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE"
        }));
    }
}
