package cl.vetnova.ventas;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

public class VentasServiceApplicationTest {

    @Test
    void testMainLevantaLaAplicacion() {
        assertDoesNotThrow(() -> VentasServiceApplication.main(new String[] {
                "--server.port=0",
                "--spring.datasource.url=jdbc:h2:mem:ventascovdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE"
        }));
    }
}
