package com.lostway.eventmanager.kafka;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FieldChange<T> {
    private T oldValue;
    private T newValue;
}
