-- begin DTA_RULE_DETAIL
alter table DTA_RULE_DETAIL add constraint FK_DTA_RULE_DETAIL_ON_RULE foreign key (RULE_ID) references DTA_RULE(ID)^
create index IDX_DTA_RULE_DETAIL_ON_RULE on DTA_RULE_DETAIL (RULE_ID)^
-- end DTA_RULE_DETAIL
