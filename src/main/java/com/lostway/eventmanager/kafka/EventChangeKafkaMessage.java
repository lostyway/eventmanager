package com.lostway.eventmanager.kafka;

import com.lostway.eventmanager.enums.EventStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
public class EventChangeKafkaMessage {

    private Long eventId;
    private Long changedById;
    private FieldChange<String> name;
    private FieldChange<Integer> maxPlaces;
    private FieldChange<LocalDateTime> date;
    private FieldChange<Double> cost;
    private FieldChange<Integer> duration;
    private FieldChange<Long> locationId;
    private FieldChange<EventStatus> status;
    private List<Long> users;
}
