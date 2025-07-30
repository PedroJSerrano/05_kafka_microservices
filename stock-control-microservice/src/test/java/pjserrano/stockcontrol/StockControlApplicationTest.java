package pjserrano.stockcontrol;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = StockControlApplication.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS) // Este limpia el contexto después de toda la clase
@TestPropertySource(properties = {
        "spring.r2dbc.url=r2dbc:h2:mem:///testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.r2dbc.username=sa",
        "spring.r2dbc.password=",
        "spring.r2dbc.initial-sql=classpath:/schema.sql"
})
class StockControlApplicationTests {

    @Test
    void contextLoads() {
        // Este test simple verifica que el contexto de Spring Boot y la BBDD H2 cargan correctamente.
        System.out.println("Context loaded successfully for StockControlApplicationTests.");
    }
}