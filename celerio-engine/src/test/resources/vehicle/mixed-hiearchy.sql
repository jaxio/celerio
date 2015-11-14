-- 			-- air - plane
-- vehicle 	|
-- 			|		-- twotires
-- 			-- road	|
-- 					|
-- 					|				-- car
-- 					-- fourtires 	|
-- 									-- truck
-- should be mapped with one2one

CREATE TABLE VEHICLE (
	vehicle_id		smallint not null IDENTITY,
	discriminator 	varchar(255),
	name			varchar(255),
	air_specific	varchar(255),
	road_specific	varchar(255),
	primary key (vehicle_id)
);

CREATE TABLE PLANE (
	plane_id			smallint  not null IDENTITY,
	plane_specific	varchar(255),
	primary key (plane_id)
);

CREATE TABLE TWOTIRES (
	twotires_id			smallint  not null IDENTITY,
	twotires_specific	varchar(255),
	primary key (twotires_id)
);

CREATE TABLE MOTO (
	moto_id			smallint  not null IDENTITY,
	moto_specific	varchar(255),
	primary key (moto_id)
);

CREATE TABLE FOURTIRES (
	fourtires_id		smallint  not null IDENTITY,
	fourtires_specific	varchar(255),
	primary key (fourtires_id)
);

CREATE TABLE CAR (
	car_id			smallint  not null IDENTITY,
	car_specific	varchar(255),
	primary key (car_id)
);

CREATE TABLE TRUCK (
	truck_id		smallint  not null IDENTITY,
	truck_specific	varchar(255),
	primary key (truck_id)
);
