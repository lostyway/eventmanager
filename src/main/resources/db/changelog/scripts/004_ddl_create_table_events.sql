create table events
(
    id              serial primary key,
    name            text,
    owner_id        int references users (id),
    max_places      int,
    occupied_places int,
    date            timestamp,
    cost            decimal,
    duration        int,
    location_id     int references locations (id),
    status          text
);