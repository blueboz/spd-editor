var queryurl = "find${className}Init.do";
var addurl = "add${className}.do";
var updateurl = "add${className}Process.do";

seajs.use(["./../js/config_i18n.js"], pageInit);
var tool;
function pageInit() {
	$(function() {
		tool = new Eraui_init();
		E_init.ready();
		if (window.location.search.indexOf("modify=true") != -1) {
			document.body.id = 'initUpdateCrud';
		}
		tool.initCrud({initParam : 'Y'});
		
	});
	
}


// window.queryObjectCallBack=function (){
//
// }
