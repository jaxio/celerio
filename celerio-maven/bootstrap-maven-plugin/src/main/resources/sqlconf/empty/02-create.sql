CREATE SEQUENCE hibernate_sequence START WITH 1000;

CREATE TABLE USER (
    id                       int not null IDENTITY,
    username                 varchar(100) not null,
    password                 varchar(100) not null,
    is_enabled               bool not null default true,
    version                  int default 0,
    constraint user_unique_1 unique (username),
    primary key (id)
);
