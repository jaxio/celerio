DROP ALL OBJECTS;

CREATE TABLE columns_with_conflict_names (
    role_id char(32) not null,
    roleId char(32) not null,
    _role_id char(32) not null,
    role__id char(32) not null,
    __role_id char(32) not null,
    role_id_ char(32) not null,

    primary key (role_id)
);
