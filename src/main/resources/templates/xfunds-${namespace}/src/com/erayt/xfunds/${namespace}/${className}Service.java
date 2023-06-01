package com.erayt.xfunds.fund;
import com.erayt.ecas.domain.User;
import com.erayt.xfunds.base.domain.TradeLogs;
import com.erayt.xfunds.${namespace}.domain.${className};

import java.io.File;

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
	public ${className} query${className}(${className} ${className});

	/*
	 * 查询数据${title}
	 * @param ${className}
	 * @return result
	 */
	public ${className} query${className}(${className} ${className});


}