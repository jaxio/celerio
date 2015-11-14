
CREATE TABLE OPERATION (
	operation_id			smallint not null IDENTITY,
	discriminator			varchar(31),
	jaja					varchar(255),
	jeje					varchar(255),
	primary key (operation_id)
);

CREATE TABLE EXTRA_OPERATION (
	extra_operation_id		smallint not null IDENTITY,
	jojo					varchar(255),
	juju					varchar(255),	
	primary key (extra_operation_id)
);
