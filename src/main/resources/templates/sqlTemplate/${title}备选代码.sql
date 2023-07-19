<#if codedefs??>
<#list codedefs as codedef>

<#list codedef.items as item>
delete from  XFUNDS_BASE_CODEDEF where type='${codedef.type}' and CODEVAL='${item.value}';
</#list>
<#list codedef.items as item>
INSERT INTO XFUNDS_BASE_CODEDEF (ID, PARENTID, TYPE, CODEVAL, ISVAL, NAME, SNAME, DISPSEQ, RMK1, RMK2, RMK3, RMK4)
VALUES ((select trim(to_char(nvl(max(to_number(id)), 0) + 1)) as id from XFUNDS_BASE_CODEDEF),
        (select trim(to_char(nvl(max(to_number(id)), 0) + 1)) as id from XFUNDS_BASE_CODEDEF),
        '${codedef.type}', '${item.value}', '0', '${item.name}', '${item.name}', ${item_index}, '${codedef.rmk1}', '${codedef.rmk2}', '${codedef.rmk3}', null);
</#list>

</#list>
</#if>