seajs.use(["./../js/config_i18n.js","./../ui/form/Filefield.js"],pageInit);

var tool;
function search() {
	if(!window.Validator.Validate(window.document.forms[0])){
		return;
	}
	tool.curdsearch();
}



function pageInit() {
	E_init.init = function() {
		var cols1 = [
			//fieldname,chnname,isIdKey,width,display,align,'unkown',是否展示，渲染回调函数
			<#list columns as col>
				['${col.fieldName}', '${col.fieldChName}', true, '120px', false, 'left',false]<#sep>,
			</#list>

		];
		tool.init(`${beanName}`, `query.do?queryId=${namespace}query.query${className}List`, cols1, {
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
