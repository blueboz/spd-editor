package com.erayt.xfunds.fund.domain;

import com.erayt.xfunds.base.domain.EngineRightPo;

public class ${className} extends EngineRightPo {

	private static final long serialVersionUID = 1L;
	<#list columns as col>
	//${col.fieldChName}
	private String ${col.fieldName};

	</#list>
	<#list columns as col>
	public void se${col.fieldName?substring(0,1)?upper_case}${col.fieldName?substring(1)}(String ${col.fieldName}){
		this.${col.fieldName}=${col.fieldName};
	}

	public String get${col.fieldName?substring(0,1)?upper_case}${col.fieldName?substring(1)}(){
		return this.${col.fieldName};
	}
	</#list>

}