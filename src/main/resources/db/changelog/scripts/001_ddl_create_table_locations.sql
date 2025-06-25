create table locations
(
    id          serial primary key,
    name        text,
    address     text,
    capacity    int,
    description text
);