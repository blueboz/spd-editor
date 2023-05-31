package com.erayt.xfunds.fund;
import com.erayt.ecas.domain.User;
import com.erayt.xfunds.base.domain.TradeLogs;
import com.erayt.xfunds.fund.domain.FundWhiteList;

import java.io.File;

public interface FundWhiteListService {
	FundWhiteList findFundWhiteList(FundWhiteList fundWhiteList);

	/**
	*数据入库
	* @param bean
	* @return
	*/
	public void addFundWhiteList(FundWhiteList bean);

	/**
	 * 直接入库
	 * @param fundWhiteList
	 */
	void updateToDb(FundWhiteList fundWhiteList);

	/**
	*更新数据
	* @param bean
	* @return
	*/
	void updateFundWhiteList(FundWhiteList bean, User user);

	void doImportExcel(File file, User user);


	public void deleteFundWhiteListBatch(FundWhiteList[] fundWhiteLists);

	/*
	*查询数据
	* @param bean
	* @return result
	*/
	public FundWhiteList queryFundWhiteList(FundWhiteList bean);

	TradeLogs buildTradeLogForModDirectly(FundWhiteList toWhiteList, User user);

	TradeLogs buildTradeLogForFlowIssue(FundWhiteList fundWhiteList, User user);

	TradeLogs buildTradeLogForFlowRecheck(FundWhiteList fundWhiteList, User user);

	TradeLogs buildTradeLogForFlowIssuerModify(FundWhiteList fundWhiteList, User user);

	boolean checkIfCompanyIdInWhiteList(String company);
}