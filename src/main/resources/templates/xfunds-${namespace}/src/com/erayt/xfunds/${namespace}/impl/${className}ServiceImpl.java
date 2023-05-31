package com.erayt.xfunds.fund.impl;

import com.erayt.ecas.domain.User;
import com.erayt.solar2.engine.facade.ActionContext;
import com.erayt.solar2.engine.process.ProcessEngine;
import com.erayt.xfunds.base.BankRepository;
import com.erayt.xfunds.base.EnginePoControlService;
import com.erayt.xfunds.base.SequenceRepository;
import com.erayt.xfunds.base.SysParamRepository;
import com.erayt.xfunds.base.TradeLogsService;
import com.erayt.xfunds.base.domain.Bank;
import com.erayt.xfunds.base.domain.SequenceConstant;
import com.erayt.xfunds.base.domain.SysParam;
import com.erayt.xfunds.base.domain.TradeLogs;
import com.erayt.xfunds.common.DateHelper;
import com.erayt.xfunds.common.StringHelper;
import com.erayt.xfunds.common.XfundsBaseException;
import com.erayt.xfunds.common.fund.XfundsFundException;
import com.erayt.xfunds.fund.FundWhiteListService;
import com.erayt.xfunds.fund.domain.FundWhiteList;
import com.erayt.xfunds.fund.impl.dao.FundWhiteListDao;
import com.erayt.xfunds.tools.OracleStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FundWhiteListServiceImpl implements FundWhiteListService {
    //最多可以录入10000行
    int maxRows = 10000; // Maximum number of rows to process
    //每行最大字符数
    int maxStrLen = 100; // Maximum length of string

    private FundWhiteListDao fundWhiteListDao;

    private SequenceRepository sequenceRepository;

    private BankRepository bankRepository;

    private SysParamRepository sysParamRepository;

    private TradeLogsService tradeLogsService;

    public TradeLogsService getTradeLogsService() {
        return tradeLogsService;
    }

    public void setTradeLogsService(TradeLogsService tradeLogsService) {
        this.tradeLogsService = tradeLogsService;
    }

    public SequenceRepository getSequenceRepository() {
        return sequenceRepository;
    }

    public void setSequenceRepository(SequenceRepository sequenceRepository) {
        this.sequenceRepository = sequenceRepository;
    }

    public BankRepository getBankRepository() {
        return bankRepository;
    }

    public void setBankRepository(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    public SysParamRepository getSysParamRepository() {
        return sysParamRepository;
    }

    public void setSysParamRepository(SysParamRepository sysParamRepository) {
        this.sysParamRepository = sysParamRepository;
    }

    @Override
    public FundWhiteList findFundWhiteList(FundWhiteList fundWhiteList) {
        return fundWhiteList;
    }

    /**
     * 数据验证
     *
     * @param bean
     * @return
     */
    private void validate(FundWhiteList bean) {
        if (bean.getCompanyId() == null) {
            throw new RuntimeException("companyId非空");
        }
        if (bean.getCompanyId() != null && StringHelper.stringByteLength(bean.getCompanyId()) > 100) {
            throw new RuntimeException("companyId长度不能超过10");
        }
        if (bean.getImportDate() != null && StringHelper.stringByteLength(bean.getImportDate()) > 8) {
            throw new RuntimeException("importDate长度不能超过60");
        }
        if (bean.getSignDate() != null && String.valueOf(bean.getSignDate()).length() > 8) {
            throw new RuntimeException("signDate长度不能超过8");
        }
    }

    /**
     * 数据入库
     *
     * @param bean
     * @return
     */
    @Override
    public void addFundWhiteList(FundWhiteList bean) {
        validate(bean);
        bean.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        fundWhiteListDao.insertFundWhiteList(bean);
    }

    private EnginePoControlService enginePoControlService;

    private ProcessEngine processEngine;

    public EnginePoControlService getEnginePoControlService() {
        return enginePoControlService;
    }

    public void setEnginePoControlService(EnginePoControlService enginePoControlService) {
        this.enginePoControlService = enginePoControlService;
    }

    public ProcessEngine getProcessEngine() {
        return processEngine;
    }

    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    @Override
    public void updateToDb(FundWhiteList fundWhiteList) {
        fundWhiteListDao.updateFundWhiteList(fundWhiteList);
    }

    /**
     * 更新数据
     *
     * @param bean
     * @return
     */
    @Override
    public void updateFundWhiteList(FundWhiteList bean, User user) {
        validate(bean);

        FundWhiteList byId = findById(bean.getId());
        if (byId == null) {
            throw new XfundsFundException("数据有误");
        }
        if (StringUtils.equals(byId.getCompanyId(), bean.getCompanyId())) {
            bean.setDescription(String.format("白名单调整|从%s->%s", byId.getCompanyId(), bean.getCompanyId()));
            fundWhiteListDao.updateFundWhiteList(bean);
            TradeLogs tradeLogs = buildTradeLogForModDirectly(bean , user);
            tradeLogsService.addTradeLogs(tradeLogs);
        } else {
            bean.setDescription(String.format("白名单调整|从%s->%s", byId.getCompanyId(), bean.getCompanyId()));
            enginePoControlService.initEngineRightPo(bean, user);
            ActionContext actionContext = new ActionContext();
            actionContext.put("fundWhiteList", bean);
            actionContext.put("user", user);
            processEngine.start("fundWhiteListModify", actionContext);
        }


    }

    public FundWhiteList findById(String id) {
        FundWhiteList fundWhiteList = new FundWhiteList();
        fundWhiteList.setId(id);
        return fundWhiteListDao.selectFundWhiteList(fundWhiteList);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(FundWhiteListService.class);

    /**
     * 根据Excel 生成白名单
     *
     * @param file
     * @return
     */
    public List<FundWhiteList> buildWhiteList(File file) {
        List<FundWhiteList> result = new ArrayList<FundWhiteList>();
        Workbook wb = null;
        int sysDate = DateHelper.sysDate();
        try (InputStream inp = new FileInputStream(file);) {

            wb = WorkbookFactory.create(inp);
            if (wb == null) {
                LOGGER.error("解析EXCEL文件异常,创建WorkbookFactory出错");
                throw new XfundsBaseException("error.bank.manager.import.error");
            }

            Sheet sheet = wb.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            if (lastRowNum > maxRows) {
                throw new XfundsBaseException("单次导入不要超过10000行");
            }
            Set<String> dataRows = new HashSet<>(); // To store unique data rows
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Start from row 1, assumes first row is header
                Row row = sheet.getRow(i);
                if (row == null) {
                    throw new XfundsFundException(String.format("第%d行为空", i));
                }

                Cell cell = row.getCell(0);
                if (cell == null) {
                    throw new XfundsFundException(String.format("第%d行第0列为空", i));
                }
                if (cell.getCellType() != Cell.CELL_TYPE_STRING) {
                    throw new XfundsFundException(String.format("第%d行第0列不是字符串", i));
                }
                String data = cell.getStringCellValue().trim();
                if (StringUtils.isBlank(data)) {
                    throw new XfundsFundException(String.format("第%d行第0列是空字符串", i));
                }
                int realLeninOracle = OracleStringUtils.realLengthInOracle(data);
                if (realLeninOracle > maxStrLen) {
                    throw new XfundsFundException(String.format("第一行数据长度超长,最大长度:%d 实际长度:%d", maxStrLen, realLeninOracle));
                }

                dataRows.add(data);
            }
            for (String dataRow : dataRows) {
                FundWhiteList fundWhiteList = new FundWhiteList();
                fundWhiteList.setCompanyId(dataRow);
                fundWhiteList.setImportDate(String.valueOf(sysDate));
                fundWhiteList.setId(UUID.randomUUID().toString().replaceAll("-",""));
                result.add(fundWhiteList);
            }
        } catch (Exception e) {
            LOGGER.error("分行期权优惠点差导入异常e={}", e.getLocalizedMessage());
            throw new XfundsFundException(e.getMessage());

        }

        return result;
    }


    @Override
    public void doImportExcel(File file, User user) {
        List<FundWhiteList> fundWhiteLists = buildWhiteList(file);
        LOGGER.info(String.format("用户%s执行了批量导入",user.getLogonid()));
        if(CollectionUtils.isEmpty(fundWhiteLists)){
            throw new XfundsFundException("未录入任何数据，导入被终止");
        }
        LOGGER.info("导入的数据有");
        for (FundWhiteList fundWhiteList : fundWhiteLists) {
            LOGGER.info(fundWhiteList.toString());
        }
        fundWhiteListDao.deleteFundWhiteList();
        fundWhiteListDao.insertFundWhiteListBatch(fundWhiteLists);
    }

    @Override
    public void deleteFundWhiteListBatch(FundWhiteList[] fundWhiteLists) {
        if (fundWhiteLists != null && fundWhiteLists.length == 0) {
            return;
        }
        for (FundWhiteList fundWhiteList : fundWhiteLists) {
            fundWhiteListDao.deleteFundWhiteListId(fundWhiteList.getId());
        }
    }

    /**
     * 查询数据
     *
     * @param bean
     * @return result
     */
    @Override
    public FundWhiteList queryFundWhiteList(FundWhiteList bean) {
        return fundWhiteListDao.selectFundWhiteList(bean);
    }

    public FundWhiteListDao getFundWhiteListDao() {
        return fundWhiteListDao;
    }

    public void setFundWhiteListDao(FundWhiteListDao fundWhiteListDao) {
        this.fundWhiteListDao = fundWhiteListDao;
    }

    /**
     * 缺少TransName y
     * @param anyFundWhiteList
     * @param user
     * @return
     */
    private TradeLogs buildTradeLogBase(FundWhiteList anyFundWhiteList,User user){
        TradeLogs tradeLogs = new TradeLogs();
        tradeLogs.setDownloadKey(sequenceRepository.findSequenceByType(SequenceConstant.TRADLOG_SEQ));
        tradeLogs.setBusinessNo(anyFundWhiteList.getCompanyId());
        tradeLogs.setTellerId(user.getLogonid());
        tradeLogs.setTellerName(user.getName());
        tradeLogs.setBlockNumber(sequenceRepository.findSequenceByType(SequenceConstant.TRADLOG_SEQ));
        Bank bank = bankRepository.findBankInfo(tradeLogs.getBankId() != null ? tradeLogs.getBankId() : user.getBankid());
        if (bank == null) {
            LOGGER.error("机构不存在 Bankid={}", user.getBankid());
            throw new XfundsBaseException("机构不存在");
        }
        tradeLogs.setBankId(bank.getBankId());
        tradeLogs.setBankName(bank.getDipName());
        tradeLogs.setTradeTime(sysParamRepository.findSysIntTime());
        tradeLogs.setTradeDate(Integer.valueOf(this.sysParamRepository.findSysParamValue(SysParam.NAME_SYSCURRDATE)));
        return tradeLogs;
    }


    @Override
    public TradeLogs buildTradeLogForModDirectly(FundWhiteList toWhiteList, User user) {
        TradeLogs tradeLogs = buildTradeLogBase(toWhiteList, user);
        tradeLogs.setTransName("行员" + user.getName() + "[" + user.getLogonid() + "]"
                + "修改" + toWhiteList.getDescription()+ " 白名单信息");
        tradeLogs.setVerMemo(toWhiteList.toString());
        return tradeLogs;
    }

    @Override
    public TradeLogs buildTradeLogForFlowIssue(FundWhiteList fundWhiteList, User user){
        TradeLogs tradeLogs = buildTradeLogBase(fundWhiteList, user);
        tradeLogs.setTransName("行员" + user.getName() + "[" + user.getLogonid() + "]"
                + "发起修改 " + fundWhiteList.getDescription()+ " 白名单信息 操作类型:"+fundWhiteList.getOperTypeString());
        tradeLogs.setVerMemo(fundWhiteList.toString());
        return tradeLogs;
    }

    @Override
    public TradeLogs buildTradeLogForFlowRecheck(FundWhiteList fundWhiteList, User user){

        TradeLogs tradeLogs = buildTradeLogBase(fundWhiteList, user);

        tradeLogs.setTransName("行员" + user.getName() + "[" + user.getLogonid() + "]"
                + "总行复核复核 " + fundWhiteList.getDescription()+ " 白名单信息 操作类型:"+fundWhiteList.getOperTypeString());
        tradeLogs.setVerMemo(fundWhiteList.toString());
        return tradeLogs;
    }

    @Override
    public TradeLogs buildTradeLogForFlowIssuerModify(FundWhiteList fundWhiteList, User user){
        TradeLogs tradeLogs = buildTradeLogBase(fundWhiteList, user);
        tradeLogs.setTransName("行员" + user.getName() + "[" + user.getLogonid() + "]"
                + "发起经办修改 " + fundWhiteList.getDescription()+ " 白名单信息 操作类型:"+fundWhiteList.getOperTypeString());
        tradeLogs.setVerMemo(fundWhiteList.toString());
        return tradeLogs;
    }

    @Override
    public boolean checkIfCompanyIdInWhiteList(String company) {
        FundWhiteList queryCondition = new FundWhiteList();
        queryCondition.setCompanyId(company);
        FundWhiteList fundWhiteList = fundWhiteListDao.selectFundWhiteList(queryCondition);
        if(fundWhiteList!=null){
            return true;
        }
        return false;
    }

}