package be.pxl.services.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfiguration {

    @Bean
    public Queue notificationQueue() {
        return new Queue("notificationQueue", false);
    }
}
