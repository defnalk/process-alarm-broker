package com.defneertugrul.alarmer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AlarmBrokerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AlarmBrokerApplication.class, args);
    }
}
