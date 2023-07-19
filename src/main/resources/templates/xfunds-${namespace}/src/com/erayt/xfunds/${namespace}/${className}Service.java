package com.erayt.xfunds.${namespace};
import com.erayt.ecas.domain.User;
import com.erayt.xfunds.base.domain.TradeLogs;
import com.erayt.xfunds.${namespace}.domain.${className};
<#if importExcel>
import java.io.File;
</#if>

public interface ${className}Service {

	/**
	* 数据入库${title}
	* @param bean
	* @return
	*/
	public void add${className}(${className} bean);

	/**
	* 更新数据${title}
	* @param bean
	* @return
	*/
	void update${className}(${className} bean, User user);

	/**
	 * 删除${title}
	 * @params ${className}s
	 */
	public void delete${className}Batch(${className}[] ${className}s);

	/*
	* 查询数据${title}
	* @param ${className}
	* @return result
	*/
	public ${className} find${className}(${className} ${className});

	<#if importExcel>
	/**
	 * Excel 数据导入
	 */
	public void doImportExcel(File file, User user) ;
	</#if>


	/**
	 * 新增数据初始化调用的方法
	 */
	${className} find${className}Init(User user);

	/**
	 * 构建交易日志
	 */
	TradeLogs buildTradeLog(${className} ${beanName}, User user);

}