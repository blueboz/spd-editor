package com.erayt.xfunds.fund.impl.dao;
import com.erayt.xfunds.fund.domain.${className};

import java.util.List;

public interface ${className}Dao {
	/**
	*数据入库
	* @param bean
	* @return
	*/
	public void insert${className}(${className} bean);

	/**
	 * 数据批量入库
	 * @param bean
	 */
	public void insert${className}Batch(List<${className}> bean);


	/**
	*更新数据
	* @param bean
	* @return
	*/
	public void update${className}(${className} bean);
	/**
	*查询数据
	* @param bean
	* @return result
	*/
	public ${className} select${className}(${className} bean);

	/**
	 * 清空所以的数据
	 */
	public void delete${className}();

	/**
	 * 按照管理人进行删除
	 * @param companyId
	 */
	public void delete${className}Id(String id);

}