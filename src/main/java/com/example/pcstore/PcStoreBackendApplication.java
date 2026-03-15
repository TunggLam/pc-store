package com.example.pcstore;

import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(KeycloakSpringBootProperties.class)
@SpringBootApplication
public class PcStoreBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PcStoreBackendApplication.class, args);
    }

}
