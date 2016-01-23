-- The create script is used to create a database that Celerio can reverse.
-- On wildfly, hibernate create itself the database in memory (not using this script but by looking at entities...)

CREATE SEQUENCE HIBERNATE_SEQUENCE START WITH 0;

CREATE TABLE USER (
    id                       int not null IDENTITY,
    login                    varchar(100) not null,
    password                 varchar(100) not null,
    email                    varchar(100),
    is_enabled               bool not null default true,
    civility                 char(2) default 'MR',
    first_name               varchar(100),
    last_name                varchar(100),
    creation_date            timestamp,
    creation_author          varchar(200),
    last_modification_date   timestamp,
    last_modification_author varchar(200),
    version                  int default 0,

    constraint user_unique_1 unique (login),
    primary key (id)
);

COMMENT ON TABLE USER IS 'The User is a human that can connect to this web application';
COMMENT ON COLUMN USER.LOGIN IS 'The login used to login';

CREATE TABLE ROLE (
    id              int not null IDENTITY,
    role_name       varchar(100) not null,
    constraint role_unique_1 unique (role_name),
    primary key (id)
);

CREATE TABLE USER_ROLE (
    user_id     int not null,
    role_id     int not null,

    constraint user_role_fk_1 foreign key (user_id) references USER,
    constraint user_role_fk_2 foreign key (role_id) references ROLE,
    primary key (user_id, role_id)
);

CREATE TABLE ADDRESS (
    id                          int not null IDENTITY,
    street                      varchar(100),
    zip_code                    varchar(10),
    city                        varchar(100) not null,
    country                     varchar(100) not null,
    version                     int default 0,
    primary key (id)
);

CREATE TABLE CUSTOMER (
    id                      int not null IDENTITY,
    company_name            varchar(100) not null,
    address_id              int,
    contract_binary         bytea,
    contract_file_name      varchar(100),
    contract_content_type   varchar(100),
    contract_size           int,
    version                 int default 0,
    constraint customer_address foreign key (address_id) references ADDRESS,
    primary key (id)
);

CREATE TABLE CURRENCY (
    id              int not null IDENTITY,
    code            char(3) not null,
    name            varchar(100) not null,
    decimal_count   int,
    version         int default 0,
    primary key (id)
);

CREATE TABLE ACCOUNT (
    id                  int not null IDENTITY,
    account_number      varchar(100) not null,
    name                varchar(100) not null,
    currency_id         int not null,
    customer_id         int not null,
    version             int default 0,
    constraint account_number_unique unique (account_number),
    constraint account_currency foreign key (currency_id) references CURRENCY,
    constraint account_custumer foreign key (customer_id) references CUSTOMER,
    primary key (id)
);

COMMENT ON TABLE ACCOUNT IS 'The Account represent a basic bank account';

CREATE TABLE TRANSACTION (
    id                  int not null IDENTITY,
    account_id          int not null,
    amount              DECIMAL(20, 2) not null,
    transaction_date    date not null,
    value_date          date not null,
    currency_id         int not null,
    description         varchar(255),
    version             int default 0,

    constraint transaction_account foreign key (account_id) references ACCOUNT,
    constraint transaction_currency foreign key (currency_id) references CURRENCY,
    primary key (id)
);

CREATE TABLE SAVED_SEARCH (
    id              int not null IDENTITY,
    name            varchar(128) not null,
    form_classname  varchar(256) not null,
    form_content    bytea,
    user_id         int,

    constraint saved_search_fk_1 foreign key (user_id) references USER,
    primary key (id)
);
