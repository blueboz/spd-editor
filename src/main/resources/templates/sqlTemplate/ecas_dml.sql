delete from  ECAS_MENU where MENUID=${menuId};
INSERT INTO ECAS_MENU (APPLID, MENUID, NAME, LVL, URL, PARENT, IMG, ISCHILD, GROUPID)
VALUES (999, ${menuId}, '${title}', 1, '${namespace}/${className}QueryList.html', ${pmenuId}, '../ui/eraui/images/title_icon.gif', 0, 0);


delete from  ECAS_ACTIONPOWER where POWERBIT=${powerbits?split(',')[0]};
INSERT INTO ECAS_ACTIONPOWER (APPLID, POWERBIT, PATH, DESCRIPTION, ENABLED, MODULENAME, WEIGHT, ENGMODULE, ENGDESC, MENUID)
VALUES (999, ${powerbits?split(',')[0]}, '${namespace}/${className}QueryList.html', '${functionbase}-A-${title}', 1, '${moduleName}', 3, null, null, ${menuId});

delete from  ECAS_ACTIONPOWER where POWERBIT=${powerbits?split(',')[1]};
INSERT INTO ECAS_ACTIONPOWER (APPLID, POWERBIT, PATH, DESCRIPTION, ENABLED, MODULENAME, WEIGHT, ENGMODULE, ENGDESC, MENUID)
VALUES (999, ${powerbits?split(',')[1]}, '${namespace}/${className}Input.html', '${functionbase}-A-${title}-新增', 1, '${moduleName}', 3, null, null, 0);

delete from  ECAS_ACTIONPOWER where POWERBIT=${powerbits?split(',')[2]};
INSERT INTO ECAS_ACTIONPOWER (APPLID, POWERBIT, PATH, DESCRIPTION, ENABLED, MODULENAME, WEIGHT, ENGMODULE, ENGDESC, MENUID)
VALUES (999, ${powerbits?split(',')[2]}, '${namespace}/${className}Mod.html', '${functionbase}-A-${title}-修改', 1, '${moduleName}', 3, null, null, 0);

