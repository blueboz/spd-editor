DECLARE
VN_COUNT INTEGER;
    V_SQL VARCHAR(30000);
BEGIN
    -- 建表ddl语句
SELECT COUNT(*) INTO VN_COUNT FROM USER_TABLES
WHERE TABLE_NAME = 'XFUNDS_FUND_WHITELIST';
IF (VN_COUNT < 1) THEN
        V_SQL := 'create table ${tableName?upper_case}
(
    <#list columns as col>
     ${col.fieldName?upper_case}      ${col.dataType} <#if col.nullable==false> not null </#if><#if col.isId==true >primary key</#if><#sep>,
    </#list>

) tablespace TBS_XFUNDS_DAT';
-- EXECUTE IMMEDIATE V_SQL;
-- V_SQL := 'create index FUND_WHITELIST_IDX1 on XFUNDS_FUND_WHITELIST(COMPANY) TABLESPACE TBS_XFUNDS_INDEX';
EXECUTE IMMEDIATE V_SQL;
END IF;
END;
/

comment on table ${tableName?upper_case} is '基金白名单';
<#list columns as col>
comment on column ${tableName?upper_case}.${col.fieldName?upper_case} is '${col.fieldChName}';
</#list>

