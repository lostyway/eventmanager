create table users
(
    id            bigserial primary key,
    login         text unique,
    password_hash text not null,
    role          text not null
);