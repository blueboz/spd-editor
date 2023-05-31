package com.erayt.xfunds.fund.domain;

import com.erayt.xfunds.base.domain.EngineRightPo;

public class FundWhiteList extends EngineRightPo {
	private static final long serialVersionUID = 1L;

	// 事件ID
	private long eventId;
	// 任务名称
	private String taskName;
	//主键标识
	private String id;
	//管理人名称
	private String companyId ;
	//导入日期
	private String importDate ;
	//签批日期
	private Integer signDate ;
	/**
	 * 前台操作类型
	 */
	private String operType;


	public String getOperTypeString(){
		return "ok".equals(operType)?"下一步或者批准":"拒绝或者撤销";
	}

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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCompanyId() {
		return companyId ;
	}
	public void setCompanyId(String companyId) {
		this.companyId=companyId ;
	}
	public String getImportDate() {
		return importDate ;
	}
	public void setImportDate(String importDate) {
		this.importDate=importDate ;
	}
	public Integer getSignDate() {
		return signDate ;
	}
	public void setSignDate(Integer signDate) {
		this.signDate=signDate ;
	}

	private String description;

	public String getDescription(){
	    return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	@Override
	public String toString() {
		return "FundWhiteList{" +
				"id='" + id + '\'' +
				", companyId='" + companyId + '\'' +
				", importDate='" + importDate + '\'' +
				", signDate=" + signDate +
				'}';
	}
}