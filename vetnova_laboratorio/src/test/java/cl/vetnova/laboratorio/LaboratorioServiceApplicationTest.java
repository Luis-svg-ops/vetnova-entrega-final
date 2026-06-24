package cl.vetnova.laboratorio;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

public class LaboratorioServiceApplicationTest {

    @Test
    void testMainLevantaLaAplicacion() {
        assertDoesNotThrow(() -> LaboratorioServiceApplication.main(new String[] {
                "--server.port=0",
                "--spring.datasource.url=jdbc:h2:mem:laboratoriocovdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE"
        }));
    }
}
