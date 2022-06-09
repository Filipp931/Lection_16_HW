create table cache
(
    id     int auto_increment
        primary key,
    method varchar(255) not null,
    args   blob         null,
    value  blob         not null
);

