-- 			-- air
-- vehicle	|
-- 			|		-- twotires -- moto
-- 			-- road |
-- 					|
-- 					|				-- car
-- 					-- fourtires	|
-- 									-- truck
-- should be mapped with one2one

CREATE TABLE VEHICLE (
	vehicle_id			smallint not null IDENTITY,
	discriminator 		varchar(255),
	name				varchar(255),
	air_specific		varchar(255),
	road_specific		varchar(255),
	twotires_specific	varchar(255),
	moto_specific		varchar(255),
	fourtires_specific	varchar(255),
	car_specific		varchar(255),
	truck_specific		varchar(255),
	primary key (vehicle_id)
);

CREATE TABLE CAR_DRIVER (
	car_driver_id			smallint not null IDENTITY,
	name					varchar(255),
	favorite_car_id			smallint,
    constraint car_driver_fk_1 foreign key (favorite_car_id) references VEHICLE,
	primary key (car_driver_id)
);
