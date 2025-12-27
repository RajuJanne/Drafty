package com.drafty;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.drafty.service.DataDragonService;

@SpringBootApplication
public class DraftyApplication {

    public static void main(String[] args) {
        SpringApplication.run(DraftyApplication.class, args);
    }
    @Bean
    public ObjectMapper objectMapper() {
        // In Jackson 3 (Spring 4 default), we use the builder for thread-safety
        return JsonMapper.builder()
                .findAndAddModules() // Automatically handles Java 8 times, etc.
                .build();
    }
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
    @Bean
    CommandLineRunner run(DataDragonService dataDragonService) {
        return args -> {
            dataDragonService.importChampions();
        };
    }
}