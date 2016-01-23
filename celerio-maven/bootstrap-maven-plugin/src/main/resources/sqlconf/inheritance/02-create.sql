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

CREATE TABLE ROCKET (
    id                  int not null IDENTITY,
	name				varchar(100) not null,
	weight				smallint,
	discriminator		char(4) not null,
	seats_count		    smallint,
    primary key (id)
);

CREATE TABLE ACCOUNT (
    id               int not null IDENTITY,
    account_num	     varchar(100) not null,
    name             varchar(100) not null,
	version			 smallint default 0,
	constraint account_unique_1 unique (account_num),

    primary key (id)
);

CREATE TABLE ADMINISTRATIVE_ACCOUNT (
    id                      int not null IDENTITY,
	country					varchar(100) not null,	
	city					varchar(100),	
    primary key (id)
);

CREATE TABLE ENTERPRISE_ACCOUNT (
    id                  int not null IDENTITY,
	company_name		varchar(100),
	is_ethical			bool not null default true,
    primary key (id)
);
