DECLARE
VN_COUNT INTEGER;
    V_SQL VARCHAR(30000);
BEGIN
    -- 建表ddl语句
SELECT COUNT(*) INTO VN_COUNT FROM USER_TABLES
WHERE TABLE_NAME = '${tableName?upper_case}';
IF (VN_COUNT < 1) THEN
        V_SQL := 'create table ${tableName?upper_case}
(
    <#list columns as col>
     ${col.fieldName?upper_case}      ${col.dataType} <#if col.nullable==false> not null </#if><#sep>,
    </#list>

) tablespace TBS_XFUNDS_DATA';
    EXECUTE IMMEDIATE V_SQL;
-- V_SQL := 'create index FUND_WHITELIST_IDX1 on XFUNDS_FUND_WHITELIST(COMPANY) TABLESPACE TBS_XFUNDS_INDEX';
    V_SQL:='alter table ${tableName?upper_case} add constraint PK_${tableName?upper_case} primary key(<#list columns?filter(item->item.isId==true) as col> ${col.fieldName?upper_case}<#sep>, </#list>) ';
    EXECUTE IMMEDIATE V_SQL;
END IF;
END;
/

comment on table ${tableName?upper_case} is '${title}';
<#list columns as col>
comment on column ${tableName?upper_case}.${col.fieldName?upper_case} is '${col.fieldChName}';
</#list>

