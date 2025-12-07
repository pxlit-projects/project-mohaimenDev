package be.pxl.services.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    @RabbitListener(queues = "notificationQueue")
    public void listen(String message) {
        log.info("*** Notification received: {} ***", message);
        System.out.println("=================================================");
        System.out.println("NOTIFICATION: " + message);
        System.out.println("=================================================");
    }
}
