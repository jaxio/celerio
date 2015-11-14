DROP ALL OBJECTS;

--CREATE TABLE no_pk (
--    no_pk_name varchar(255)
--);

CREATE TABLE single_pk (
    single_pk_id integer,
    single_pk varchar(255),
    PRIMARY KEY (single_pk_id)
);

CREATE TABLE composite_pk (
    composite_pk_a integer,
    composite_pk_b integer,
    composite_pk_name varchar(255),
    PRIMARY KEY (composite_pk_a, composite_pk_b)
);

CREATE TABLE one_to_one (
    one_to_one_id integer,
    one_to_one_name varchar(255),
    points_to_unique_single_pk integer,
        CONSTRAINT one_to_one_FK FOREIGN KEY (points_to_unique_single_pk) REFERENCES single_pk,    
    constraint one_to_one_unique unique (points_to_unique_single_pk),        
    PRIMARY KEY (one_to_one_id)
);

CREATE TABLE composite_one_to_one (
    composite_one_to_one_id integer,
    composite_one_to_one_name varchar(255),
    points_to_unique_composite_pk_a integer,
    points_to_unique_composite_pk_b integer,    
        CONSTRAINT one_to_one_CFK FOREIGN KEY (points_to_unique_composite_pk_a, points_to_unique_composite_pk_b) REFERENCES composite_pk(composite_pk_a, composite_pk_b),
    constraint composite_one_to_one_unique unique (points_to_unique_composite_pk_a, points_to_unique_composite_pk_b),        
    PRIMARY KEY (composite_one_to_one_id)
);

CREATE TABLE one_to_many (
    one_to_many_id integer,
    one_to_many_name varchar(255),
    points_to_single_pk integer,
        CONSTRAINT one_to_many_fk FOREIGN KEY (points_to_single_pk) REFERENCES single_pk,    
    PRIMARY KEY (one_to_many_id)
);

CREATE TABLE self_reference_one_to_many (
    self_reference_one_to_many_id integer,
    self_reference_one_to_many_name varchar(255),
    points_to_self_reference_one_to_many integer,
    PRIMARY KEY (self_reference_one_to_many_id)
);

ALTER TABLE self_reference_one_to_many ADD 
    CONSTRAINT self_reference_one_to_many_fk 
        FOREIGN KEY (points_to_self_reference_one_to_many) 
            REFERENCES self_reference_one_to_many;

CREATE TABLE self_reference_composite_one_to_many (
    self_reference_composite_one_to_many_a integer,
    self_reference_composite_one_to_many_b integer,
    self_reference_composite_one_to_many_name varchar(255),
    points_to_self_reference_composite_one_to_many_a integer,
    points_to_self_reference_composite_one_to_many_b integer,
    PRIMARY KEY (self_reference_composite_one_to_many_a, self_reference_composite_one_to_many_b)
);

ALTER TABLE self_reference_composite_one_to_many ADD
    CONSTRAINT self_reference_composite_one_to_many_cfk 
        FOREIGN KEY (points_to_self_reference_composite_one_to_many_a,points_to_self_reference_composite_one_to_many_b) 
            REFERENCES self_reference_composite_one_to_many(self_reference_composite_one_to_many_a, self_reference_composite_one_to_many_b);


CREATE TABLE HAS_NN_B (
    HAS_NN_B_ID integer,
    HAS_NN_B_NAME varchar(255),
    PRIMARY KEY (HAS_NN_B_ID)
);

CREATE TABLE HAS_NN_A (
    HAS_NN_A_ID integer,
    HAS_NN_A_NAME varchar(255),
    PRIMARY KEY (HAS_NN_A_ID)
);

CREATE TABLE NN_A_B (
    points_to_HAS_NN_A integer,
    points_to_HAS_NN_B integer,
        CONSTRAINT NN_A_B_fk_1 FOREIGN KEY (points_to_HAS_NN_A) REFERENCES HAS_NN_A,    
        CONSTRAINT NN_A_B_fk_2 FOREIGN KEY (points_to_HAS_NN_B) REFERENCES HAS_NN_B,    
);

CREATE TABLE COMPOSITE_HAS_NN_B (
    COMPOSITE_HAS_NN_B_a integer,
    COMPOSITE_HAS_NN_B_b integer,
    COMPOSITE_HAS_NN_B_NAME varchar(255),
    PRIMARY KEY (COMPOSITE_HAS_NN_B_a,COMPOSITE_HAS_NN_B_b)
);

CREATE TABLE COMPOSITE_HAS_NN_A (
    COMPOSITE_HAS_NN_A_a integer,
    COMPOSITE_HAS_NN_A_b integer,
    COMPOSITE_HAS_NN_A_NAME varchar(255),
    PRIMARY KEY (COMPOSITE_HAS_NN_A_a, COMPOSITE_HAS_NN_A_b)
);

CREATE TABLE COMPOSITE_NN_A_B (
    points_to_COMPOSITE_HAS_NN_A_a integer,
    points_to_COMPOSITE_HAS_NN_A_b integer,
    points_to_COMPOSITE_HAS_NN_B_a integer,
    points_to_COMPOSITE_HAS_NN_B_b integer,
        CONSTRAINT COMPOSITE_NN_A_B_cfk_1 FOREIGN KEY (points_to_COMPOSITE_HAS_NN_A_a,points_to_COMPOSITE_HAS_NN_A_b) REFERENCES COMPOSITE_HAS_NN_A,    
        CONSTRAINT COMPOSITE_NN_A_B_cfk_2 FOREIGN KEY (points_to_COMPOSITE_HAS_NN_B_a,points_to_COMPOSITE_HAS_NN_B_b) REFERENCES COMPOSITE_HAS_NN_B 
);

CREATE TABLE COMPOSITE_HAS_MIXED_B (
    COMPOSITE_HAS_MIXED_B_a integer,
    COMPOSITE_HAS_MIXED_B_b integer,
    COMPOSITE_HAS_MIXED_B_name varchar(255),
    PRIMARY KEY (COMPOSITE_HAS_MIXED_B_a,COMPOSITE_HAS_MIXED_B_b)
);

CREATE TABLE COMPOSITE_HAS_MIXED_A (
    COMPOSITE_HAS_MIXED_A_id integer,
    COMPOSITE_HAS_MIXED_A_NAME varchar(255),
    PRIMARY KEY (COMPOSITE_HAS_MIXED_A_id)
);

CREATE TABLE MIXED_NN_MIXED_A_MIXED_B (
    points_to_COMPOSITE_HAS_MIXED_B_a integer,
    points_to_COMPOSITE_HAS_MIXED_B_b integer,
    points_to_COMPOSITE_HAS_MIXED_A_id integer,
    CONSTRAINT MIXED_NN_MIXED_A_MIXED_B_cfk1 FOREIGN KEY (points_to_COMPOSITE_HAS_MIXED_B_a,points_to_COMPOSITE_HAS_MIXED_B_b) REFERENCES COMPOSITE_HAS_MIXED_B,    
    CONSTRAINT MIXED_NN_MIXED_A_MIXED_B_cfk2 FOREIGN KEY (points_to_COMPOSITE_HAS_MIXED_A_id) REFERENCES COMPOSITE_HAS_MIXED_A 
);

CREATE TABLE ENUM_TABLE (
    a_b_or_c varchar CONSTRAINT ENUM_a_b_c CHECK (a_b_or_c in ('a', 'b', 'c'))
);
