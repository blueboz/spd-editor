delete from ENGINE_ACTION where ID_ = '/add${className}.do';
delete from ENGINE_ACTIONINPUT where ACTIONID_ = '/add${className}.do';
delete from ENGINE_ACTIONOUTPUT
where ACTIONID_ = '/add${className}.do';
INSERT INTO ENGINE_ACTION (ID_, NAMESPACE_, URL_, WINDOWPARAM_, ACTIONSCRIPT_, ACTIONINTERCEPT_)
VALUES ('/add${className}.do', '${namespace}', null, null, '${beanName}Service.add${className}(${beanName});', null);
INSERT INTO ENGINE_ACTIONINPUT (ACTIONID_, BEANID_, CLAZZ_, FIELDEXPR_, SOURCE_)
VALUES ('/add${className}.do', '${beanName}', 'com.erayt.xfunds.${namespace}.domain.${className}', null, null);


delete from ENGINE_ACTION where ID_ = '/update${className}.do';
delete from ENGINE_ACTIONINPUT where ACTIONID_ = '/update${className}.do';
delete from ENGINE_ACTIONOUTPUT where ACTIONID_ = '/update${className}.do';
INSERT INTO ENGINE_ACTION (ID_, NAMESPACE_, URL_, WINDOWPARAM_, ACTIONSCRIPT_, ACTIONINTERCEPT_)
VALUES ('/update${className}.do', '${namespace}', null, null, '${beanName}Service.update${className}(${beanName},user);',
        null);
INSERT INTO ENGINE_ACTIONINPUT (ACTIONID_, BEANID_, CLAZZ_, FIELDEXPR_, SOURCE_)
VALUES ('/update${className}.do', '${beanName}', 'com.erayt.xfunds.${namespace}.domain.${className}', null, null);
INSERT INTO ENGINE_ACTIONINPUT (ACTIONID_, BEANID_, CLAZZ_, FIELDEXPR_, SOURCE_)
VALUES ('/update${className}.do', 'user', null, null, '$session[com.erayt.user_key]');


delete from ENGINE_ACTION where ID_ = '/find${className}.do';
delete from ENGINE_ACTIONINPUT where ACTIONID_ = '/find${className}.do';
delete from ENGINE_ACTIONOUTPUT where ACTIONID_ = '/find${className}.do';
INSERT INTO ENGINE_ACTION (ID_, NAMESPACE_, URL_, WINDOWPARAM_, ACTIONSCRIPT_, ACTIONINTERCEPT_)
VALUES ('/find${className}.do', '${namespace}', null, null,
        '${beanName}=${beanName}Service.find${className}(${beanName});', null);
INSERT INTO ENGINE_ACTIONINPUT (ACTIONID_, BEANID_, CLAZZ_, FIELDEXPR_, SOURCE_)
VALUES ('/find${className}.do', '${beanName}', 'com.erayt.xfunds.${namespace}.domain.${className}', null, null);
INSERT INTO ENGINE_ACTIONOUTPUT (ACTIONID_, BEANID_, FIELDEXPR_, TARGET_)
VALUES ('/find${className}.do', 'fundUserRule', null, null);

<#if importExcel??>
delete from ENGINE_ACTION where ID_ = '/add${className}Import.do';
delete from ENGINE_ACTIONINPUT where ACTIONID_ = '/add${className}Import.do';
delete from ENGINE_ACTIONOUTPUT where ACTIONID_ = '/add${className}Import.do';
INSERT INTO ENGINE_ACTION (ID_, NAMESPACE_, URL_, WINDOWPARAM_, ACTIONSCRIPT_, ACTIONINTERCEPT_)
VALUES ('/add${className}Import.do', null, null, null, '${beanName}Service.doImportExcel(file, user)', null);
INSERT INTO ENGINE_ACTIONINPUT (ACTIONID_, BEANID_, CLAZZ_, FIELDEXPR_, SOURCE_)
VALUES ('/add${className}Import.do', 'file', 'java.io.File', null, null);
INSERT INTO ENGINE_ACTIONINPUT (ACTIONID_, BEANID_, CLAZZ_, FIELDEXPR_, SOURCE_)
VALUES ('/add${className}Import.do', 'user', null, null, '$session[com.erayt.user_key]');
</#if>

delete from ENGINE_ACTION where ID_ = '/delete${className}Batch.do';
delete from ENGINE_ACTIONINPUT where ACTIONID_ = '/delete${className}Batch.do';
delete from ENGINE_ACTIONOUTPUT where ACTIONID_ = '/delete${className}Batch.do';
INSERT INTO ENGINE_ACTION (ID_, NAMESPACE_, URL_, WINDOWPARAM_, ACTIONSCRIPT_, ACTIONINTERCEPT_)
VALUES ('/delete${className}Batch.do', '${namespace}', null, null,
        '${beanName}Service.delete${className}Batch(${beanName}s);', null);
INSERT INTO ENGINE_ACTIONINPUT (ACTIONID_, BEANID_, CLAZZ_, FIELDEXPR_, SOURCE_)
VALUES ('/delete${className}Batch.do', '${beanName}s', '[Lcom.erayt.xfunds.${namespace}.domain.${className};', null, null);


delete from ENGINE_ACTION where ID_ = '/find${className}Event.do';
delete from ENGINE_ACTIONINPUT where ACTIONID_ = '/find${className}Event.do';
delete from ENGINE_ACTIONOUTPUT where ACTIONID_ = '/find${className}Event.do';
INSERT INTO ENGINE_ACTION (ID_, NAMESPACE_, URL_, WINDOWPARAM_, ACTIONSCRIPT_, ACTIONINTERCEPT_)
VALUES ('/find${className}Event.do', '${namespace}', null, null, '${beanName}=processEngine.findBussinesObject(id)', null);
INSERT INTO ENGINE_ACTIONINPUT (ACTIONID_, BEANID_, CLAZZ_, FIELDEXPR_, SOURCE_)
VALUES ('/find${className}Event.do', 'id', 'java.lang.Long', null, null);
INSERT INTO ENGINE_ACTIONOUTPUT (ACTIONID_, BEANID_, FIELDEXPR_, TARGET_)
VALUES ('/find${className}Event.do', '${beanName}', null, null);


delete from ENGINE_ACTION where ID_ = '/${beanName}ControlTrade.do';
delete from ENGINE_ACTIONINPUT where ACTIONID_ = '/${beanName}ControlTrade.do';
delete from ENGINE_ACTIONOUTPUT where ACTIONID_ = '/${beanName}ControlTrade.do';
INSERT INTO ENGINE_ACTION (ID_, NAMESPACE_, URL_, WINDOWPARAM_, ACTIONSCRIPT_, ACTIONINTERCEPT_)
VALUES ('/${beanName}ControlTrade.do', '${namespace}', null, null, 'processEngine.complete(id,doneType,CONTEXT)', null);
INSERT INTO ENGINE_ACTIONINPUT (ACTIONID_, BEANID_, CLAZZ_, FIELDEXPR_, SOURCE_)
VALUES ('/${beanName}ControlTrade.do', 'doneType', 'java.lang.String', null, null);
INSERT INTO ENGINE_ACTIONINPUT (ACTIONID_, BEANID_, CLAZZ_, FIELDEXPR_, SOURCE_)
VALUES ('/${beanName}ControlTrade.do', 'id', 'java.lang.Long', null, null);
INSERT INTO ENGINE_ACTIONINPUT (ACTIONID_, BEANID_, CLAZZ_, FIELDEXPR_, SOURCE_)
VALUES ('/${beanName}ControlTrade.do', '${beanName}', 'com.erayt.xfunds.${namespace}.domain.${className}', null, null);
INSERT INTO ENGINE_ACTIONINPUT (ACTIONID_, BEANID_, CLAZZ_, FIELDEXPR_, SOURCE_)
VALUES ('/${beanName}ControlTrade.do', 'user', null, null, '$session[com.erayt.user_key]');
INSERT INTO ENGINE_ACTIONOUTPUT (ACTIONID_, BEANID_, FIELDEXPR_, TARGET_)
VALUES ('/${beanName}ControlTrade.do', '${beanName}', null, null);

delete from ENGINE_RIGHTS where RIGHTS_='${beanName}MstChk';
INSERT INTO ENGINE_RIGHTS (RIGHTS_, CANDIDATE_, SQLCONDITION_, DOCONDITION_)
VALUES ('${beanName}MstChk',
        '${r'${'}${beanName}.insUser${r'}'},${r'${'}${beanName}.mstBaker${r'}'},${r'${'}${beanName}.brhBaker${r'}'},${r'${'}${beanName}.subBaker${r'}'},${r'${'}${beanName}.outBaker${r'}'}',
        '''${r'${'}user.bankid${r'}'}'' = candidate2_ or candidate1_ = ''${r'${'}user.logonid${r'}'}''',
        '''${r'${'}user.bankid${r'}'}'' = candidate2_  and candidate1_ <> ''${r'${'}user.logonid${r'}'}'' and ''复核''= ''${r'${'}user.lvlStr${r'}'}'' ');
delete from ENGINE_RIGHTS where RIGHTS_='${beanName}Issuer';
INSERT INTO ENGINE_RIGHTS (RIGHTS_, CANDIDATE_, SQLCONDITION_, DOCONDITION_)
VALUES ('${beanName}Issuer',
        '${r'${'}${beanName}.insUser${r'}'},${r'${'}${beanName}.mstBaker${r'}'},${r'${'}${beanName}.brhBaker${r'}'},${r'${'}${beanName}.subBaker${r'}'},${r'${'}${beanName}.outBaker${r'}'}',
        '''${r'${'}user.bankid${r'}'}'' in (candidate2_)', 'candidate1_ = ''${r'${'}user.logonid${r'}'}''');

delete from ENGINE_ACTION where ID_='/add${className}Process.do';
delete from ENGINE_ACTIONINPUT where ACTIONID_='/add${className}Process.do';
delete from ENGINE_ACTIONOUTPUT where ACTIONID_='/add${className}Process.do';
INSERT INTO ENGINE_ACTION (ID_, NAMESPACE_, URL_, WINDOWPARAM_, ACTIONSCRIPT_, ACTIONINTERCEPT_) VALUES ('/add${className}Process.do', '${namespace}', null, null, 'processEngine.start("${beanName}Process",CONTEXT)', null);
INSERT INTO ENGINE_ACTIONINPUT (ACTIONID_, BEANID_, CLAZZ_, FIELDEXPR_, SOURCE_) VALUES ('/add${className}Process.do', '${beanName}', 'com.erayt.xfunds.${namespace}.domain.${className}', null, null);
INSERT INTO ENGINE_ACTIONINPUT (ACTIONID_, BEANID_, CLAZZ_, FIELDEXPR_, SOURCE_) VALUES ('/add${className}Process.do', 'user', null, null, '$session[com.erayt.user_key]');

delete from ENGINE_ACTION where ID_ = '/find${className}Init.do';
delete from ENGINE_ACTIONINPUT where ACTIONID_ = '/find${className}Init.do';
delete from ENGINE_ACTIONOUTPUT where ACTIONID_ = '/find${className}Init.do';
INSERT INTO ENGINE_ACTION (ID_, NAMESPACE_, URL_, WINDOWPARAM_, ACTIONSCRIPT_, ACTIONINTERCEPT_)
VALUES ('/find${className}Init.do', '${namespace}', null, null, '${beanName}=${beanName}Service.find${className}Init(user);', null);
INSERT INTO ENGINE_ACTIONINPUT (ACTIONID_, BEANID_, CLAZZ_, FIELDEXPR_, SOURCE_)
VALUES ('/find${className}Init.do', 'user', null, null, '$session[com.erayt.user_key]');
INSERT INTO ENGINE_ACTIONOUTPUT (ACTIONID_, BEANID_, FIELDEXPR_, TARGET_)
VALUES ('/find${className}Init.do', '${beanName}', null, null);

