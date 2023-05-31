package com.erayt.xfunds.fund.impl.dao;
import com.erayt.xfunds.fund.domain.FundWhiteList;

import java.util.List;

public interface FundWhiteListDao {
	/**
	*数据入库
	* @param bean
	* @return
	*/
	public void insertFundWhiteList(FundWhiteList bean);

	/**
	 * 数据批量入库
	 * @param bean
	 */
	public void insertFundWhiteListBatch(List<FundWhiteList> bean);


	/**
	*更新数据
	* @param bean
	* @return
	*/
	public void updateFundWhiteList(FundWhiteList bean);
	/**
	*查询数据
	* @param bean
	* @return result
	*/
	public FundWhiteList selectFundWhiteList(FundWhiteList bean);

	/**
	 * 清空所以的数据
	 */
	public void deleteFundWhiteList();

	/**
	 * 按照管理人进行删除
	 * @param companyId
	 */
	public void deleteFundWhiteListId(String id);

}