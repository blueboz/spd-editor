create table ${tableName?upper_case}
(
    <#list columns as col>
     ${col.fieldName?upper_case}      ${col.dataType} <#if col.nullable==false> not null </#if><#if col.isId==true >primary key</#if><#sep>,
    </#list>

)
 /
