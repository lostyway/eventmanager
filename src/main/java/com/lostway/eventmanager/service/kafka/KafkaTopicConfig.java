package com.lostway.eventmanager.service.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public KafkaAdmin kafkaAdmin() {
        return new KafkaAdmin(Map.of(
                "bootstrap.servers", "kafka:9092"
        ));
    }

    @Bean
    public NewTopic eventChangesTopic() {
        return TopicBuilder
                .name("event_changes")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
