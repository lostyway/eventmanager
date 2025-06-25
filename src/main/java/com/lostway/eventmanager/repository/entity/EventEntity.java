package com.lostway.eventmanager.repository.entity;

import com.lostway.eventmanager.roles.EventStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id")
    private UserEntity owner;

    @Column(name = "max_places")
    private Integer maxPlaces;

    @Column(name = "occupied_places")
    private Integer occupiedPlaces;

    private String date;

    private Double cost;

    @Min(30)
    private Integer duration;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    private LocationEntity location;

    @Enumerated(EnumType.STRING)
    private EventStatus status;
}
