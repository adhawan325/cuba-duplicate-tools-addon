-- begin DTA_RULE
create table DTA_RULE (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255),
    STATUS integer,
    DESCRIPTION longvarchar,
    BASE_RECORD_TYPE varchar(255),
    MATCHING_RECORD_TYPE varchar(255),
    CASE_SENSITIVE boolean,
    --
    primary key (ID)
)^
-- end DTA_RULE
-- begin DTA_RULE_DETAIL
create table DTA_RULE_DETAIL (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    BASE_RECORD_FIELD varchar(255) not null,
    MATCHING_RECORD_FIELD varchar(255) not null,
    MATCH_TYPE integer not null,
    RULE_ID varchar(36),
    --
    primary key (ID)
)^
-- end DTA_RULE_DETAIL
