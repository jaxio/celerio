CREATE SEQUENCE hibernate_sequence START WITH 1000;

CREATE TABLE ADDRESS (
    id                          int not null IDENTITY,
    street_name                 varchar(100),
    city                        varchar(100) not null,
    version                     int default 0,

    primary key (id)
);

CREATE TABLE ACCOUNT (
    id                          char(36) not null,
    login                       varchar(100) not null,
    password                    varchar(100) not null,
    email                       varchar(100) not null,
    is_enabled                  bool not null default true,
    civility                    char(2) default 'MR',
    first_name                  varchar(100),
    last_name                   varchar(100),
    birth_date                  timestamp,
    description                 varchar(255),
    address_id                  int,
    creation_date               timestamp,
    creation_author             varchar(200),
    last_modification_date      timestamp,
    last_modification_author    varchar(200),
    version                     int default 0,

    constraint account_unique_1 unique (login),
    constraint account_unique_2 unique (email),
    constraint account_fk_1 foreign key (address_id) references ADDRESS,
    primary key (id)
);

CREATE TABLE ROLE (
    id                          int not null IDENTITY,
    role_name                   varchar(100) not null,

    constraint role_unique_1 unique (role_name),
    primary key (id)
);

CREATE TABLE ACCOUNT_ROLE (
    account_id                  char(36) not null,
    role_id                     int not null,

    constraint account_role_fk_1 foreign key (account_id) references ACCOUNT,
    constraint account_role_fk_2 foreign key (role_id) references ROLE,
    primary key (account_id, role_id)
);

CREATE TABLE DOCUMENT (
    id                          char(36)        not null,
    account_id                  char(36)        not null,
    document_binary             bytea,
    document_file_name          varchar(100)    not null,
    document_content_type       varchar(100)    not null,
    document_size               int             not null,
    version                     int             default 0,

    constraint document_fk_1 foreign key (account_id) references ACCOUNT,
    primary key (id)
);

CREATE TABLE BOOK (
    id                          int not null IDENTITY,
    account_id                  char(36),
    title                       varchar(100) not null,
    number_of_pages             int          not null,
    version                     int          default 0,

    constraint book_fk_1 foreign key (account_id) references ACCOUNT,
    primary key (id)
);

CREATE TABLE MORE_TYPES_DEMO (
    id                           DECIMAL(15,5) not null IDENTITY,
    number_int                   int,
    number_long                  bigint,
    number_double                float,
    number_float                 real,
    number_big_integer           DECIMAL(20, 0),
    number_big_decimal           DECIMAL(20, 2),
    date_java_temporal_date      date,
    date_java_temporal_timestamp timestamp,
    date_joda                    date,
    date_time_joda               timestamp,
    version                      int default 0,

    primary key (id)
);

CREATE TABLE SAVED_SEARCH (
    id                            int not null IDENTITY,
    name                          varchar(128) not null,
    form_classname                varchar(256) not null,
    form_content                  bytea,
    account_id                    char(36),

    constraint saved_search_fk_1 foreign key (account_id) references ACCOUNT,
    primary key (id)
);

CREATE TABLE AUDIT_LOG (
    id                            int not null IDENTITY,
    author                        varchar(256),
    event                         varchar(256),
    event_date                    timestamp,
    string_attribute_1            varchar(256),
    string_attribute_2            varchar(256),
    string_attribute_3            varchar(256),
    primary key (id)
);

-- has a composite PK
CREATE TABLE LEGACY (
    name varchar(16) not null,
    code varchar(8) not null,
    dept int not null,
    extra_info varchar(100) not null,

    primary key (name, code, dept)
);
