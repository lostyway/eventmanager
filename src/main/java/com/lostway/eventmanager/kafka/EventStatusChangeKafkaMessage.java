package com.lostway.eventmanager.kafka;

import com.lostway.eventmanager.enums.EventStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class EventStatusChangeKafkaMessage {

    private Integer eventId;
    private EventStatus status;
    private LocalDateTime timestamp;
}
