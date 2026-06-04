package com.example.web_bansach.infrastructure.persistence;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing configuration
 * Enables automatic audit field population
 */
@Configuration
@EnableJpaAuditing
public class AuditingConfig {
}
