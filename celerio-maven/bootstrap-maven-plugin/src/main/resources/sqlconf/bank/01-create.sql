DROP ALL OBJECTS;

CREATE SEQUENCE hibernate_sequence START WITH 1000;

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

INSERT INTO USER (login, password, email) VALUES ('admin', 'admin', 'admin@example.com');

INSERT INTO ROLE (role_name) VALUES ('ROLE_ADMIN');
INSERT INTO ROLE (role_name) VALUES ('ROLE_USER');
INSERT INTO ROLE (role_name) VALUES ('ROLE_MONITORING');

INSERT INTO USER_ROLE (user_id, role_id) VALUES (1, 1);
INSERT INTO USER_ROLE (user_id, role_id) VALUES (1, 2);
INSERT INTO USER_ROLE (user_id, role_id) VALUES (1, 3);

INSERT INTO CURRENCY (code, name, decimal_count) VALUES ('EUR', 'Euro', 2);
INSERT INTO CURRENCY (code, name, decimal_count) VALUES ('USD', 'Dollar des états-unis', 2);
INSERT INTO CURRENCY (code, name, decimal_count) VALUES ('GBP', 'Livre Sterling', 2);
INSERT INTO CURRENCY (code, name, decimal_count) VALUES ('CAD', 'Dollar canadien', 2);
INSERT INTO CURRENCY (code, name, decimal_count) VALUES ('CHF', 'Franc suisse', 2);
INSERT INTO CURRENCY (code, name, decimal_count) VALUES ('JPY', 'Yen', 0);
INSERT INTO CURRENCY (code, name, decimal_count) VALUES ('SEK', 'Couronne suédoise', 2);

INSERT INTO ADDRESS (street, zip_code, city, country) VALUES ('1 RUE DE LA BANQUE', '75000', 'Paris', 'France');
INSERT INTO ADDRESS (street, zip_code, city, country) VALUES ('2 RUE DE LA BANQUE', '75000', 'Paris', 'France');
INSERT INTO ADDRESS (street, zip_code, city, country) VALUES ('3 RUE DE LA BANQUE', '75000', 'Paris', 'France');
INSERT INTO ADDRESS (street, zip_code, city, country) VALUES ('NY', '10045', 'New-York', 'Etats-Unis');
INSERT INTO ADDRESS (street, zip_code, city, country) VALUES ('AL', '16000', 'Alger', 'Algérie');
INSERT INTO ADDRESS (street, zip_code, city, country) VALUES ('4 RUE DE LA BANQUE', '75000', 'Paris', 'France');

INSERT INTO CUSTOMER (company_name, address_id) VALUES ('Trésor Public', 1);
INSERT INTO CUSTOMER (company_name, address_id) VALUES ('BNP Paribas', 2);
INSERT INTO CUSTOMER (company_name, address_id) VALUES ('Société Générale', 3);
INSERT INTO CUSTOMER (company_name, address_id) VALUES ('Federal Reserve bank of New York', 4);
INSERT INTO CUSTOMER (company_name, address_id) VALUES ('Banque d''Algérie', 5);
INSERT INTO CUSTOMER (company_name, address_id) VALUES ('Ministère du Budget', 6);

INSERT INTO ACCOUNT (account_number, name, currency_id, customer_id) VALUES ('0001001', 'Compte 1', 1, 1);
INSERT INTO ACCOUNT (account_number, name, currency_id, customer_id) VALUES ('0001002', 'Compte 2', 2, 1);
INSERT INTO ACCOUNT (account_number, name, currency_id, customer_id) VALUES ('0002001', 'Compte 1', 1, 2);
INSERT INTO ACCOUNT (account_number, name, currency_id, customer_id) VALUES ('0002002', 'Compte 2', 2, 2);
INSERT INTO ACCOUNT (account_number, name, currency_id, customer_id) VALUES ('0003001', 'Compte 1', 1, 3);
INSERT INTO ACCOUNT (account_number, name, currency_id, customer_id) VALUES ('0003002', 'Compte 2', 2, 3);
INSERT INTO ACCOUNT (account_number, name, currency_id, customer_id) VALUES ('0003003', 'Compte 3', 6, 3);
INSERT INTO ACCOUNT (account_number, name, currency_id, customer_id) VALUES ('0004001', 'Compte 1', 1, 4);
INSERT INTO ACCOUNT (account_number, name, currency_id, customer_id) VALUES ('0004002', 'Compte 2', 2, 4);
INSERT INTO ACCOUNT (account_number, name, currency_id, customer_id) VALUES ('0004003', 'Compte 3', 4, 4);
INSERT INTO ACCOUNT (account_number, name, currency_id, customer_id) VALUES ('0004004', 'Compte 4', 6, 4);
INSERT INTO ACCOUNT (account_number, name, currency_id, customer_id) VALUES ('0005001', 'Compte 1', 1, 5);
INSERT INTO ACCOUNT (account_number, name, currency_id, customer_id) VALUES ('0005002', 'Compte 2', 2, 5);
INSERT INTO ACCOUNT (account_number, name, currency_id, customer_id) VALUES ('0006001', 'Compte 1', 1, 6);
INSERT INTO ACCOUNT (account_number, name, currency_id, customer_id) VALUES ('0006002', 'Compte 2', 2, 6);

INSERT INTO TRANSACTION (account_id, amount, transaction_date, value_date, currency_id, description)
                VALUES (1, 100000, '2013-01-08', '2013-01-11', 1, 'Réglement 1');
INSERT INTO TRANSACTION (account_id, amount, transaction_date, value_date, currency_id, description)
                VALUES (1, -50000, '2013-01-09', '2013-01-12', 1, 'Réglement 2');
INSERT INTO TRANSACTION (account_id, amount, transaction_date, value_date, currency_id, description)
                VALUES (1, 250000, '2013-01-10', '2013-01-13', 1, 'Réglement 3');
INSERT INTO TRANSACTION (account_id, amount, transaction_date, value_date, currency_id, description)
                VALUES (1, -5000000, '2013-01-11', '2013-01-14', 1, 'Réglement 4');
INSERT INTO TRANSACTION (account_id, amount, transaction_date, value_date, currency_id, description)
                VALUES (1, 2000000, '2013-01-12', '2013-01-15', 1, 'Réglement 5');
INSERT INTO TRANSACTION (account_id, amount, transaction_date, value_date, currency_id, description)
                VALUES (1, 100000, '2013-01-13', '2013-01-16', 1, 'Réglement 6');
INSERT INTO TRANSACTION (account_id, amount, transaction_date, value_date, currency_id, description)
                VALUES (1, 500000, '2013-01-14', '2013-01-17', 1, 'Réglement 7');
INSERT INTO TRANSACTION (account_id, amount, transaction_date, value_date, currency_id, description)
                VALUES (1, -5000000, '2013-01-15', '2013-01-18', 1, 'Réglement 8');
INSERT INTO TRANSACTION (account_id, amount, transaction_date, value_date, currency_id, description)
                VALUES (1, 9000000, '2013-01-16', '2013-01-19', 1, 'Réglement 9');
INSERT INTO TRANSACTION (account_id, amount, transaction_date, value_date, currency_id, description)
                VALUES (1, 200000, '2013-01-17', '2013-01-20', 1, 'Réglement 10');
INSERT INTO TRANSACTION (account_id, amount, transaction_date, value_date, currency_id, description)
                VALUES (2, 100000, '2013-01-08', '2013-01-11', 2, 'Réglement 1');
INSERT INTO TRANSACTION (account_id, amount, transaction_date, value_date, currency_id, description)
                VALUES (2, -50000, '2013-01-09', '2013-01-12', 2, 'Réglement 2');
INSERT INTO TRANSACTION (account_id, amount, transaction_date, value_date, currency_id, description)
                VALUES (2, 250000, '2013-01-10', '2013-01-13', 2, 'Réglement 3');
INSERT INTO TRANSACTION (account_id, amount, transaction_date, value_date, currency_id, description)
                VALUES (2, -5000000, '2013-01-11', '2013-01-14', 2, 'Réglement 4');
INSERT INTO TRANSACTION (account_id, amount, transaction_date, value_date, currency_id, description)
                VALUES (2, 2000000, '2013-01-12', '2013-01-15', 2, 'Réglement 5');
INSERT INTO TRANSACTION (account_id, amount, transaction_date, value_date, currency_id, description)
                VALUES (2, 100000, '2013-01-13', '2013-01-16', 2, 'Réglement 6');
INSERT INTO TRANSACTION (account_id, amount, transaction_date, value_date, currency_id, description)
                VALUES (2, 500000, '2013-01-14', '2013-01-17', 2, 'Réglement 7');
INSERT INTO TRANSACTION (account_id, amount, transaction_date, value_date, currency_id, description)
                VALUES (2, -5000000, '2013-01-15', '2013-01-18', 2, 'Réglement 8');
INSERT INTO TRANSACTION (account_id, amount, transaction_date, value_date, currency_id, description)
                VALUES (2, 9000000, '2013-01-16', '2013-01-19', 2, 'Réglement 9');
INSERT INTO TRANSACTION (account_id, amount, transaction_date, value_date, currency_id, description)
                VALUES (2, 200000, '2013-01-17', '2013-01-20', 2, 'Réglement 10');