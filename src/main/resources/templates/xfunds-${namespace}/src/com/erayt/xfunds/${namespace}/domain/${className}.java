package com.erayt.xfunds.${namespace}.domain;

import com.erayt.xfunds.base.domain.EngineRightPo;

public class ${className} extends EngineRightPo {

	private static final long serialVersionUID = 1L;
	<#list columns as col>
	//${col.fieldChName}
	private ${col.objType} ${col.fieldName};

	</#list>

	/**
	 * 前台操作类型
	 */
	private String operType;
	/**
	 * 事件ID
	 */
	private long eventId;
	/**
	 * 任务名称
	 */
	private String taskName;

	/**
	 * 链接ID ,用于标识整个流程而用的标识ID
	 */
	private long linkId;

	<#list columns as col>
	public void set${col.fieldName?substring(0,1)?upper_case}${col.fieldName?substring(1)}(${col.objType} ${col.fieldName}){
		this.${col.fieldName}=${col.fieldName};
	}

	public ${col.objType} get${col.fieldName?substring(0,1)?upper_case}${col.fieldName?substring(1)}(){
		return this.${col.fieldName};
	}
	</#list>


	public String getOperType() {
		return operType;
	}

	public void setOperType(String operType) {
		this.operType = operType;
	}

	public long getEventId() {
		return eventId;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public long getLinkId() {
		return linkId;
	}

	public void setLinkId(long linkId) {
		this.linkId = linkId;
	}

	public String getDescription(){
		return "${title}";
	}
}