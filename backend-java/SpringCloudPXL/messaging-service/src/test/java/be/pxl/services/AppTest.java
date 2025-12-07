package be.pxl.services;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.rabbitmq.host=localhost",
        "spring.rabbitmq.port=5672"
})
public class AppTest {

    @Test
    void contextLoads() {
    }
}
