package com.lostway.eventmanager.audit;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@ConditionalOnProperty(name = "jpa.auditing.enabled", havingValue = "true", matchIfMissing = true)
public class JpaAuditingConfig {
}
