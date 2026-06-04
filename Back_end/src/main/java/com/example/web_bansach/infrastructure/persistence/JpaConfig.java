package com.example.web_bansach.infrastructure.persistence;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * JPA configuration
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.example.web_bansach.module")
public class JpaConfig {
}
