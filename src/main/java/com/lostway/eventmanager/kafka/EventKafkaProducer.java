package com.lostway.eventmanager.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventKafkaProducer {
    private final KafkaTemplate<String, EventChangeKafkaMessage> kafkaTemplate;
    private static final String TOPIC = "event-changes";

    public void sendEventChanges(EventChangeKafkaMessage message) {
        kafkaTemplate.send(TOPIC, message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        System.err.println("Ошибка при отправке сообщения: " + ex.getMessage());
                    } else {
                        System.out.println("Отправлено сообщение с offset: " + result.getRecordMetadata().offset());
                    }
                });
    }
}
