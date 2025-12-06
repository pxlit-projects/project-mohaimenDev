package be.pxl.services;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "eureka.client.register-with-eureka=false",
        "eureka.client.fetch-registry=false"
})
public class DiscoveryServiceTests {

    @LocalServerPort
    private int port;

    @Test
    void contextLoads() {
        assertTrue(port > 0, "Eureka server should start on a port");
    }

    @Test
    void eurekaServerIsRunning() {
        // If we get here, the Eureka server started successfully
        assertTrue(true, "Eureka server started successfully");
    }
}
