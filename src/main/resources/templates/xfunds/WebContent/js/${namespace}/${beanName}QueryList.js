seajs.use(["./../js/config_i18n.js","./../ui/form/Filefield.js"],pageInit);

var tool;
function search() {
	if(!window.Validator.Validate(window.document.forms[0])){
		return;
	}
	tool.curdsearch();
}


/**
 * 新增
 */
function add(){
	$ui.showJumpDlg({
		title: "新增",
		url: "../${namespace}/${className}Add.html",
		width: 600,
		height: 600,
		modal: false
	});

}

/**
 * 修改
 */
function mod(){
	$ui.showJumpDlg({
		title: "修改",
		url: "../${namespace}/${className}Modify.html?modify=true",
		width: 600,
		height: 600,
		modal: false
	});
}

/**
 * 修改记录
 */
function modlogs(){
	$ui.showJumpDlg({
		title: "修改记录",
		url: "../${namespace}/${className}ModifyLogs.html?modify=true",
		width: 600,
		height: 600,
		modal: false
	});
}


function pageInit() {
	E_init.init = function() {
		var cols1 = [
			//fieldname,chnname,isIdKey,width,display,align,'unkown',是否展示，渲染回调函数
<#list columns as col>
<#if col.codedef??>
		['${col.fieldName}Name', '${col.fieldChName}', true, '120px', false, 'left',false],
</#if>
<#if col.class??>
	<#if col.class=='e-date'>
		['${col.fieldName}', '${col.fieldChName}', true, '120px', false, 'left',false,Formatter.formatDate] <#sep>,
	</#if>
	<#if col.class=='e-time'>
		['${col.fieldName}', '${col.fieldChName}', true, '120px', false, 'left',false,Formatter.formatTime] <#sep>,
	</#if>
<#else>
		['${col.fieldName}', '${col.fieldChName}', true, '120px', false, 'left',false] <#sep>,
</#if>
</#list>

		];
		tool.init('${beanName}', 'query.do?queryId=${quernamespace}.query${className}List', cols1, {
			isMenu : false,
			checkHid : false,
			multiSel : false,
			singleSelect : true,
			sortHid : true,
			sortWth: '40px',
            checkWth: '40px',
			scrollWth : $("#tblct").width() - 2,
			width : '600px',
			colResizeable : false,
			height : document.body.clientHeight - 200,
			autoLoad : false,
			queryParamName : "queryParam" ,
			dataspath : "data.events",
			datasname : `${beanName}s`,
			simpleSubmit : false ,
			events : {}
		});
	};

	$(function() {
		tool = new Eraui_init();
		E_init.ready();

	});
}
