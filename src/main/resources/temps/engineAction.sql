delete from ENGINE_ACTION where ID_ = '${actionId}';
delete from ENGINE_ACTIONINPUT where ACTIONID_ = '${actionId}';
delete from ENGINE_ACTIONOUTPUT where ACTIONID_ = '${actionId}';
INSERT INTO ENGINE_ACTION (ID_, NAMESPACE_, URL_, WINDOWPARAM_, ACTIONSCRIPT_, ACTIONINTERCEPT_)
VALUES ('${actionId}', '${namespace}', null, null,
        '<#if needOutput=true>${retBeanName}=</#if>${beanName}.${methodName}(<#list parameters as par>${par.shortName}<#sep>,</#list>);', null);
<#list parameters as par>
INSERT INTO ENGINE_ACTIONINPUT (ACTIONID_, BEANID_, CLAZZ_, FIELDEXPR_, SOURCE_)
VALUES ('${actionId}', '${par.shortName}', ${par.className}, null, ${par.source});
</#list>
<#if needOutput==true>
INSERT INTO ENGINE_ACTIONOUTPUT (ACTIONID_, BEANID_, FIELDEXPR_, TARGET_)
VALUES ('${actionId}', '${retBeanName}', null, null);
</#if>
