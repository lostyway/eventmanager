package com.lostway.eventmanager.kafka;

import com.lostway.eventdtos.EventChangeKafkaMessage;
import com.lostway.eventdtos.EventStatusChangeKafkaMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventKafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC_CHANGES = "event-changes";
    private static final String TOPIC_STATUS_CHANGES = "event-status-changes";

    public void sendEventChanges(EventChangeKafkaMessage message) {
        send(TOPIC_CHANGES, message);
    }

    public void sendStatusEventChanges(EventStatusChangeKafkaMessage message) {
        send(TOPIC_STATUS_CHANGES, message);
    }

    private void send(String topic, Object message) {
        kafkaTemplate.send(topic, message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        System.err.println("Ошибка при отправке сообщения: " + ex.getMessage());
                    } else {
                        System.out.println("Отправлено сообщение с offset: " + result.getRecordMetadata().offset());
                    }
                });
    }
}
