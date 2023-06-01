package com.erayt.xfunds.fund.impl;

import com.erayt.ecas.domain.User;
import com.erayt.solar2.engine.facade.ActionContext;
import com.erayt.solar2.engine.process.ProcessEngine;
import com.erayt.xfunds.base.*;
import com.erayt.xfunds.base.domain.Bank;
import com.erayt.xfunds.base.domain.SequenceConstant;
import com.erayt.xfunds.base.domain.SysParam;
import com.erayt.xfunds.base.domain.TradeLogs;
import com.erayt.xfunds.common.DateHelper;
import com.erayt.xfunds.common.StringHelper;
import com.erayt.xfunds.common.XfundsBaseException;
import com.erayt.xfunds.common.fund.XfundsFundException;
import com.erayt.xfunds.fund.${className}Service;
import com.erayt.xfunds.fund.domain.${className};
import com.erayt.xfunds.fund.impl.dao.${className}Dao;
import com.erayt.xfunds.tools.OracleStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

public class ${className}ServiceImpl implements ${className}Service {
 
    private ${className}Dao ${className}Dao;

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
    public ${className} find${className}(${className} ${className}) {
        return ${className};
    }

    /**
     * 数据验证
     *
     * @param bean
     * @return
     */
    private void validate(${className} bean) {
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
    public void add${className}(${className} bean) {
        validate(bean);
        ${className}Dao.insert${className}(bean);
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


    /**
     * 更新数据
     *
     * @param bean
     * @return
     */
    @Override
    public void update${className}(${className} bean, User user) {
        validate(bean);

        ${className} byId = findById(bean.getId());
        if (byId == null) {
            throw new XfundsFundException("数据有误");
        }
        if (StringUtils.equals(byId.getCompanyId(), bean.getCompanyId())) {
            bean.setDescription(String.format("白名单调整|从%s->%s", byId.getCompanyId(), bean.getCompanyId()));
            ${className}Dao.update${className}(bean);
            TradeLogs tradeLogs = buildTradeLogForModDirectly(bean , user);
            tradeLogsService.addTradeLogs(tradeLogs);
        } else {
            bean.setDescription(String.format("白名单调整|从%s->%s", byId.getCompanyId(), bean.getCompanyId()));
            enginePoControlService.initEngineRightPo(bean, user);
            ActionContext actionContext = new ActionContext();
            actionContext.put("${className}", bean);
            actionContext.put("user", user);
            processEngine.start("${className}Modify", actionContext);
        }


    }

    public ${className} findById(String id) {
        ${className} ${className} = new ${className}();
        ${className}.setId(id);
        return ${className}Dao.select${className}(${className});
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(${className}Service.class);

    /**
     * 根据Excel 生成白名单
     *
     * @param file
     * @return
     */
    public List<${className}> buildWhiteList(File file) {
        List<${className}> result = new ArrayList<${className}>();
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
                ${className} ${className} = new ${className}();
                result.add(${className});
            }
        } catch (Exception e) {
            LOGGER.error("${title}Excel导入发生异常e={}", e.getLocalizedMessage());
            throw new XfundsFundException(e.getMessage());

        }

        return result;
    }


    @Override
    public void doImportExcel(File file, User user) {
        List<${className}> ${className}s = buildWhiteList(file);
        LOGGER.info(String.format("用户%s执行了批量导入",user.getLogonid()));
        if(CollectionUtils.isEmpty(${className}s)){
            throw new XfundsFundException("未录入任何数据，导入被终止");
        }
        LOGGER.info("导入的数据有");
        for (${className} ${className} : ${className}s) {
            LOGGER.info(${className}.toString());
        }
        ${className}Dao.delete${className}();
        ${className}Dao.insert${className}Batch(${className}s);
    }

    @Override
    public void delete${className}Batch(${className}[] ${className}s) {
        if (${className}s != null && ${className}s.length == 0) {
            return;
        }
        for (${className} ${className} : ${className}s) {
            ${className}Dao.delete${className}Id(${className}.getId());
        }
    }

    /**
     * 查询数据
     *
     * @param bean
     * @return result
     */
    @Override
    public ${className} query${className}(${className} bean) {
        return ${className}Dao.select${className}(bean);
    }

    public ${className}Dao get${className}Dao() {
        return ${className}Dao;
    }

    public void set${className}Dao(${className}Dao ${className}Dao) {
        this.${className}Dao = ${className}Dao;
    }

    /**
     * 缺少TransName y
     * @param any${className}
     * @param user
     * @return
     */
    private TradeLogs buildTradeLogBase(${className} any${className},User user){
        TradeLogs tradeLogs = new TradeLogs();
        tradeLogs.setDownloadKey(sequenceRepository.findSequenceByType(SequenceConstant.TRADLOG_SEQ));
        tradeLogs.setBusinessNo(any${className}.getCompanyId());
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



}