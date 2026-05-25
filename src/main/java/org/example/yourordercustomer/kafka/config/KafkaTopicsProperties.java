package org.example.yourordercustomer.kafka.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "kafka.topics")
public class KafkaTopicsProperties {
    private String productCreated;
    private String productUpdated;
    private String productDeleted;
    private String orderCreated;
    private String orderCancelled;
    private String stockReserved;
    private String paymentCompleted;
    private String paymentFailed;
}

