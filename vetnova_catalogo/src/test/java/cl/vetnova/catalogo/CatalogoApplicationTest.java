package cl.vetnova.catalogo;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

public class CatalogoApplicationTest {

    @Test
    void testMainLevantaLaAplicacion() {
        assertDoesNotThrow(() -> CatalogoApplication.main(new String[] {
                "--server.port=0",
                "--spring.datasource.url=jdbc:h2:mem:catalogocovdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE"
        }));
    }
}
