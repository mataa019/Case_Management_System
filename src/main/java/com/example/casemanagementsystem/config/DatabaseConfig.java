package com.example.casemanagementsystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.casemanagementsystem.repository")
@EnableTransactionManagement
public class DatabaseConfig {
    
    // Additional database configuration can be added here if needed
    // For now, we'll rely on Spring Boot auto-configuration
}
