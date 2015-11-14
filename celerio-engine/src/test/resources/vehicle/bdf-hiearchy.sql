-- 			-- air
-- vehicle 	|
-- 			|		-- twotires -- moto
-- 			-- road	|
-- 					|
-- 					|				-- car
-- 					-- fourtires 	|
-- 									-- truck
-- should be mapped with one2one

CREATE TABLE VEHICLE ( -- holds 3 classes with discriminator
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

CREATE TABLE TWO_TIRES (  -- holds 4 classes with discriminator and relying on vehicle class
	twotires_id				smallint  not null IDENTITY,
	subdiscriminator		varchar(255),  
	-- a faire confirmer par Hervé car j'ai fait ca de tête, si ce subdiscriminant n'est pas utilisé chez bdf, ca devient super simple, cf page 208 de "JPA with Hibernate"
	--
	-- @Entity
	-- @DiscriminatorValue("CC")
	-- @SecondaryTable(
	--   name = "CREDIT_CARD",
	--   pkJoinColumns =  @PrimaryKeyJoinColumn(name = "CREDIT_CARD_ID")
	-- )
	-- public class CreditCard extends BillingDetails {
	--     @Column(table = "CREDIT_CARD",
	--             name = "CC_NUMBER",
	--             nullable = false)
	--     private String number;
	--     ...
	-- }
	twotires_specific		varchar(255),
	fourtires_specific		varchar(255),
	car_specific			varchar(255),
	truck_specific			varchar(255),
	primary key (twotires_id)
);
