-- 			-- air
-- vehicle 	|
-- 			|		-- twotires -- moto
-- 			-- road	|
-- 					|
-- 					|				-- car
-- 					-- fourtires 	|
-- 									-- truck
-- should be mapped with one2one

CREATE TABLE VEHICLE (
	vehicle_id	smallint not null IDENTITY,
	name		varchar(255),
	primary key (vehicle_id)
);

CREATE TABLE AIR (
	air_id			smallint not null IDENTITY,
	air_specific	varchar(255),
	primary key (air_id)
);

CREATE TABLE ROAD (
	road_id			smallint not null IDENTITY,
	road_specific	varchar(255),
	primary key (road_id)
);

CREATE TABLE TWOTIRES (
	twotires_id			smallint not null IDENTITY,
	twotires_specific	varchar(255),
	primary key (twotires_id)
);

CREATE TABLE MOTO (
	moto_id			smallint not null IDENTITY,
	moto_specific	varchar(255),
	primary key (moto_id)
);

CREATE TABLE FOURTIRES (
	fourtires_id		smallint not null IDENTITY,
	fourtires_specific	varchar(255),
	primary key (fourtires_id)
);

CREATE TABLE CAR (
	car_id			smallint not null IDENTITY,
	car_specific	varchar(255),
	primary key (car_id)
);

CREATE TABLE TRUCK (
	truck_id		smallint not null IDENTITY,
	truck_specific	varchar(255),
	primary key (truck_id)
);
