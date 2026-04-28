package org.example.yourordercustomer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class YourorderCustomerApplication {

    public static void main(String[] args) {
        SpringApplication.run(YourorderCustomerApplication.class, args);
    }

}
