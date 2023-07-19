package com.erayt.xfunds.${namespace}.impl;

import com.erayt.ecas.domain.User;
import com.erayt.solar2.engine.facade.ActionContext;
import com.erayt.solar2.engine.process.ProcessEngine;

import com.erayt.xfunds.base.BankRepository;
import com.erayt.xfunds.base.CodeDefRepository;
import com.erayt.xfunds.base.EnginePoControlService;
import com.erayt.xfunds.base.SequenceRepository;
import com.erayt.xfunds.base.SysParamRepository;
import com.erayt.xfunds.base.TradeLogsService;
import com.erayt.xfunds.base.domain.Bank;
import com.erayt.xfunds.base.domain.CodeDef;
import com.erayt.xfunds.base.domain.SequenceConstant;
import com.erayt.xfunds.base.domain.SysParam;
import com.erayt.xfunds.base.domain.TradeLogs;

import com.erayt.xfunds.common.DateHelper;
import com.erayt.xfunds.common.StringHelper;
import com.erayt.xfunds.common.XfundsBaseException;
import ${exceptionFullName};
import com.erayt.xfunds.${namespace}.${className}Service;
import com.erayt.xfunds.${namespace}.domain.${className};
import com.erayt.xfunds.${namespace}.impl.dao.${className}Dao;
import com.erayt.xfunds.tools.OracleStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class ${className}ServiceImpl implements ${className}Service {

    private static final Logger LOGGER = LoggerFactory.getLogger(${className}Service.class);

    private CodeDefRepository codeDefRepository;

    public CodeDefRepository getCodeDefRepository() {
        return codeDefRepository;
    }

    public void setCodeDefRepository(CodeDefRepository codeDefRepository) {
        this.codeDefRepository = codeDefRepository;
    }

    <#if importExcel>
    private int maxRows=10000;
    </#if>
 
    private ${className}Dao ${beanName}Dao;

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


    /**
     * 数据验证
     *
     * @param bean
     * @return
     */
    private void validate(${className} bean) {
        <#list columns as col>
        <#assign uname= myutils("upper_case_first",col.fieldName)>
        <#if col.nullable==false>
        if (bean.get${uname}() == null) {
            throw new ${exceptionName}("${col.fieldName}非空");
        }
        </#if>
        <#if col.objType=='String'>
        if (bean.get${uname}() != null && StringHelper.stringByteLength(bean.get${uname}()) > ${col.maxLenInOracle}) {
            throw new ${exceptionName}("${col.fieldName}长度不能超过${col.maxLenInOracle}");
        }
        </#if>
        <#if col.objType=='Double'>
        if(bean.get${uname}()!=null &&(bean.get${uname}()>${col.limit}||bean.get${uname}()<-${col.limit})){
            throw new ${exceptionName}("${col.fieldName}的取值范围是[-${col.limit},${col.limit}]");
        }
        </#if>
        <#if (col.objType=='Integer' || col.objType=='Long')>
        if(bean!=null && String.valueOf(bean.get${uname}()).length()>${col.maxLenInOracle}){
            throw new ${exceptionName}("${col.fieldName}长度不能超过${col.maxLenInOracle}");
        }
        </#if>
        </#list>


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
        ${beanName}Dao.insert${className}(bean);
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

        ${className} byId = findById(bean);
        if (byId == null) {
            throw new ${exceptionName}("所给ID无法定位到原交易");
        }
        ${beanName}Dao.update${className}(bean);


    }

    public ${className} findById(${className} ${beanName}) {
        ${className} bean = new ${className}();
        <#list columns?filter(item->item.isId==true) as col>
        <#assign uname= myutils("upper_case_first",col.fieldName)>
        bean.set${uname}(${beanName}.get${uname}());
        </#list>
        return ${beanName}Dao.select${className}ById(bean);
    }


    <#if importExcel>

    /**
     * 根据Excel 生成白名单
     *
     * @param file
     * @return
     */
    public List<${className}> build${className}(File file) {
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
                throw new XfundsBaseException("单次导入不要超过"+maxRows+"行");
            }
            Set<String> dataRows = new HashSet<>(); // To store unique data rows
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Start from row 1, assumes first row is header
                Row row = sheet.getRow(i);
                if (row == null) {
                    throw new ${exceptionName}(String.format("第%d行为空", i));
                }

                Cell cell = row.getCell(0);
                if (cell == null) {
                    throw new ${exceptionName}(String.format("第%d行第0列为空", i));
                }
                if (cell.getCellType() != Cell.CELL_TYPE_STRING) {
                    throw new ${exceptionName}(String.format("第%d行第0列不是字符串", i));
                }
                String data = cell.getStringCellValue().trim();
                if (StringUtils.isBlank(data)) {
                    throw new ${exceptionName}(String.format("第%d行第0列是空字符串", i));
                }
                int realLeninOracle = OracleStringUtils.realLengthInOracle(data);
//                if (realLeninOracle > maxStrLen) {
//                    throw new ${exceptionName}(String.format("第一行数据长度超长,最大长度:%d 实际长度:%d", maxStrLen, realLeninOracle));
//                }

                dataRows.add(data);
            }
            for (String dataRow : dataRows) {
                ${className} ${className} = new ${className}();
                result.add(${className});
            }
        } catch (Exception e) {
            LOGGER.error("${title}Excel导入发生异常e={}", e.getLocalizedMessage());
            throw new ${exceptionName}(e.getMessage());

        }

        return result;
    }
    </#if>


    <#if importExcel>

    @Override
    public void doImportExcel(File file, User user) {
        List<${className}> ${className}s = build${className}(file);
        LOGGER.info(String.format("用户%s执行了批量导入",user.getLogonid()));
        if(CollectionUtils.isEmpty(${className}s)){
            throw new ${exceptionName}("未录入任何数据，导入被终止");
        }
        LOGGER.info("导入的数据有");
        for (${className} ${className} : ${className}s) {
            LOGGER.info(${className}.toString());
        }
        ${beanName}Dao.delete${className}();
        ${beanName}Dao.insert${className}Batch(${className}s);
    }
    </#if>

    @Override
    public void delete${className}Batch(${className}[] ${beanName}s) {
        if (${beanName}s != null && ${beanName}s.length == 0) {
            return;
        }
        for (${className} ${beanName} : ${beanName}s) {
            ${beanName}Dao.delete${className}Id(${beanName});
        }
    }

    /**
     * 查询数据
     *
     * @param bean
     * @return result
     */
    @Override
    public ${className} find${className}(${className} bean) {
        return ${beanName}Dao.select${className}ById(bean);
    }

    public ${className}Dao get${className}Dao() {
        return ${beanName}Dao;
    }

    public void set${className}Dao(${className}Dao ${beanName}Dao) {
        this.${beanName}Dao = ${beanName}Dao;
    }

    /**
     * 缺少TransName y
     * @param ceftsAccount
     * @param user
     * @return
     */
    @Override
    public TradeLogs buildTradeLog(${className} ${beanName}, User user){
        TradeLogs tradeLogs = new TradeLogs();
        tradeLogs.setDownloadKey(sequenceRepository.findSequenceByType(SequenceConstant.TRADLOG_SEQ));
        tradeLogs.setTellerId(user.getLogonid());
        tradeLogs.setTellerName(user.getName());
        tradeLogs.setBlockNumber(sequenceRepository.findSequenceByType(SequenceConstant.TRADLOG_SEQ));
        Bank bank = bankRepository.findBankInfo(tradeLogs.getBankId() != null ? tradeLogs.getBankId() : user.getBankid());
        if (bank == null) {
            LOGGER.error("机构不存在 Bankid={}", user.getBankid());
            throw new XfundsIntbankException("机构不存在");
        }
        tradeLogs.setBankId(bank.getBankId());
        tradeLogs.setBankName(bank.getDipName());
        tradeLogs.setTradeTime(sysParamRepository.findSysIntTime());
        tradeLogs.setTradeDate(Integer.valueOf(this.sysParamRepository.findSysParamValue(SysParam.NAME_SYSCURRDATE)));
        CodeDef cdf = this.codeDefRepository.findValueByTypeAndCodeNoExction("P07", ${beanName}.getOperType());
        String operTypeName = ${beanName}.getOperType();
        if (cdf != null) {
            operTypeName = cdf.getName();
        }
        if(${beanName}.getEventId()==0L) {
            tradeLogs.setTransName("${title}交易-发起");
        }else {
            tradeLogs.setTransName("${title}交易-审批流 事件流水号:" + ${beanName}.getEventId() + "," + "任务名称:" + ${beanName}.getTaskName() + ",操作类型:" + operTypeName);
        }
        tradeLogs.setVerMemo(${beanName}.getDescription());
        return tradeLogs;
    }

    /**
     * 查询数据
     *
     * @param bean
     * @return result
     */
    @Override
    public ${className} find${className}Init(User user) {
        ${className} ${beanName} = new ${className}();
        enginePoControlService.initEngineRightPo(${beanName},user);
        return ${beanName};
    }


}