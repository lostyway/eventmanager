package com.lostway.eventmanager.kafka;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class EventChangeKafkaMessage {

    private Integer eventId;
    private String name;
    private String status;

//    private final List<Long> users;
//
//    private final Long changedById;
//
//    private FieldChange<String> name;
//    private FieldChange<Integer> maxPlaces;
//    private FieldChange<LocalDateTime> date;
//    private FieldChange<Integer> cost;
//    private FieldChange<Integer> duration;
//    private FieldChange<Integer> locationId;
//    private FieldChange<EventStatus> status;
}
