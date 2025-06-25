package com.lostway.eventmanager.repository.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "locations")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class LocationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String address;
    private Integer capacity;
    private String description;
}
