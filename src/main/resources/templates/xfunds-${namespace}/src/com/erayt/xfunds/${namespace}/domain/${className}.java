package com.erayt.xfunds.${namespace}.domain;

import com.erayt.xfunds.base.domain.EngineRightPo;

public class ${className} extends EngineRightPo {

	private static final long serialVersionUID = 1L;
	<#list columns as col>
	//${col.fieldChName}
	private ${col.objType} ${col.fieldName};

	</#list>
	<#list columns as col>
	public void set${col.fieldName?substring(0,1)?upper_case}${col.fieldName?substring(1)}(${col.objType} ${col.fieldName}){
		this.${col.fieldName}=${col.fieldName};
	}

	public ${col.objType} get${col.fieldName?substring(0,1)?upper_case}${col.fieldName?substring(1)}(){
		return this.${col.fieldName};
	}
	</#list>

}