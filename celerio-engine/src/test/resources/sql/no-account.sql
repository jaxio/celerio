DROP ALL OBJECTS;

CREATE TABLE ADDRESS (
    address_id                  smallint        not null IDENTITY,
    street_name                 varchar(255)    ,
    city                        varchar(255)    not null,
    version                     smallint        default 0,
    primary key (address_id)
);

CREATE TABLE DOCUMENT (
    document_id                 char(32)        not null,
    document_content_type       varchar(255)    not null,
    document_size               integer         not null,
    document_file_name          varchar(255)    not null,
    document_binary             bytea,
    version                     smallint        default 0,

    primary key (document_id)
);

CREATE TABLE BOOK (
    book_id                     smallint        not null IDENTITY,
    title                       varchar(255)    not null,
    number_of_pages             integer         not null,
    version                     smallint        default 0,

    primary key (book_id)
);

CREATE TABLE LEGACY (
    name varchar(16) not null,
    code varchar(8) not null,
    dept smallint not null,
    extra_info varchar(255) not null,
    primary key (name, code, dept)
);


INSERT INTO LEGACY (name, code, dept, extra_info) VALUES ('name1', 'code1', 1, 'extra1');
INSERT INTO LEGACY (name, code, dept, extra_info) VALUES ('name2', 'code2', 2, 'extra2');
INSERT INTO LEGACY (name, code, dept, extra_info) VALUES ('name3', 'code3', 3, 'extra3');
INSERT INTO LEGACY (name, code, dept, extra_info) VALUES ('name4', 'code4', 4, 'extra4');


