package com.wikicoding.bank_account_lifecycle_engine.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class GeneralConfigs {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
