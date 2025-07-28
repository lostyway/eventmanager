package com.lostway.eventmanager.service.model;

import lombok.*;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Location {
    private Long id;
    private String name;
    private String address;
    private Integer capacity;
    private String description;
}

