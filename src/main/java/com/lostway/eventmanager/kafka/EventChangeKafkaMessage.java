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

    @Override
    public String toString() {
        return "EventChangeKafkaMessage{" + System.lineSeparator() +
                "eventId=" + eventId + System.lineSeparator() +
                ", changedById=" + changedById + System.lineSeparator() +
                ", name=" + name + System.lineSeparator() +
                ", maxPlaces=" + maxPlaces + System.lineSeparator() +
                ", date=" + date + System.lineSeparator() +
                ", cost=" + cost + System.lineSeparator() +
                ", duration=" + duration + System.lineSeparator() +
                ", locationId=" + locationId + System.lineSeparator() +
                ", status=" + status + System.lineSeparator() +
                ", users=" + users + System.lineSeparator() +
                '}';
    }
}
