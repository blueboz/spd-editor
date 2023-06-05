package cn.boz.jb.plugin.idea.utils;

import cn.boz.jb.plugin.floweditor.gui.utils.StringUtils;
import cn.boz.jb.plugin.idea.bean.EcasActionPower;
import cn.boz.jb.plugin.idea.bean.EcasMenu;
import cn.boz.jb.plugin.idea.bean.EngineAction;
import cn.boz.jb.plugin.idea.bean.EngineActionInput;
import cn.boz.jb.plugin.idea.bean.EngineActionOutput;
import cn.boz.jb.plugin.idea.bean.EngineFlow;
import cn.boz.jb.plugin.idea.bean.EngineTask;
import cn.boz.jb.plugin.idea.bean.UserColComments;
import cn.boz.jb.plugin.idea.bean.UserTabCols;
import cn.boz.jb.plugin.idea.bean.XFunCodeDef;
import cn.boz.jb.plugin.idea.bean.XfundsBatch;
import cn.boz.jb.plugin.idea.configurable.SpdEditorDBSettings;
import cn.boz.jb.plugin.idea.configurable.SpdEditorDBState;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import icons.SpdEditorIcons;
import oracle.jdbc.driver.OracleDriver;
import org.apache.commons.collections.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DBUtils {

    private static final DBUtils INST = new DBUtils();
    private Map<String, HikariDataSource> dataSourceMapper = new HashMap<>();

    private DBUtils() {
        //应该怎么初始化，作为一个Service?


    }

    public static DBUtils getInstance() {
        return INST;
    }

    public static void dbExceptionProcessor(Exception ee, Project project) {
        String errMsg = ExceptionProcessorUtils.generateRecrusiveException(ee);
        int idx = Messages.showDialog(errMsg, "Oops!Something wrong happen!", new String[]{"Check Db Config", "Never Mind"}, 0, SpdEditorIcons.FLOW_16_ICON);
        if (idx == 0) {
            ShowSettingsUtil.getInstance().showSettingsDialog(project, SpdEditorDBSettings.class);
        }
    }

    /**
     * 测试连接
     *
     * @param jdbcUser
     * @param jdbcPass
     * @param jdbcUrl
     * @return
     * @throws MalformedURLException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws SQLException
     */
    public boolean testConnection(String jdbcUser, String jdbcPass, String jdbcUrl, String jdbcDriverText) throws Exception {
        try (Connection connection = getConnection(jdbcUser, jdbcPass, jdbcUrl, jdbcDriverText)) {
            Statement statement = connection.createStatement();
            boolean execute = statement.execute("select * from dual");
            return execute;
        }
    }

    private Connection getConnection(Project project) throws MalformedURLException, SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (project == null) {
            return getConnection("xfunds201701", "Xfunds_1234", "jdbc:oracle:thin:@21.96.5.85:1521:FMSS", OracleDriver.class.getName());
        } else {
            SpdEditorDBState instance = SpdEditorDBState.getInstance(project);
            return getConnection(instance);
        }
    }

    private Connection getConnection(SpdEditorDBState state) throws MalformedURLException, SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return getConnection(state.jdbcUserName, state.jdbcPassword, state.jdbcUrl, state.jdbcDriver);
    }


    /**
     * @param jdbcUser
     * @param jdbcPass
     * @param jdbcUrl
     * @return please do not call this method unless you need ant known what it mean's
     */
    @SuppressWarnings("unchecked")
    private Connection getConnection(String jdbcUser, String jdbcPass, String jdbcUrl, String jdbcDriver) throws ClassNotFoundException, MalformedURLException, NoSuchMethodException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String key = Base64.getEncoder().encodeToString((jdbcUser + jdbcPass + jdbcUrl).getBytes());
        if (!dataSourceMapper.containsKey(key)) {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(jdbcUrl);
            hikariConfig.setUsername(jdbcUser);
            hikariConfig.setPassword(jdbcPass);

            hikariConfig.setDriverClassName(OracleDriver.class.getName());
            HikariDataSource dataSource = new HikariDataSource(hikariConfig);
            dataSourceMapper.put(key, dataSource);
            return dataSourceMapper.get(key).getConnection();
        } else {
            HikariDataSource hikariDataSource = dataSourceMapper.get(key);
            return hikariDataSource.getConnection();
        }


    }

    /**
     * @param sqls SQL can be split with \n
     * @return
     */
    public boolean executeSql(Project project, List<String> sqls) throws MalformedURLException, SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        try (Connection connection = getConnection(project)) {

            Statement statement = connection.createStatement();
            for (String s : sqls) {
                //非空条件判断
                if (s != null && !s.trim().equals("")) {
                    statement.execute(s);
                }
            }
            statement.executeBatch();
            //PROBLEMS's
            statement.close();
            return true;
        }
    }

    public boolean executeSqlBatchs(Project project, String sqlText) throws Exception {
        String[] sqls = sqlText.split(";");
        try (Connection connection = getConnection(project)) {
            Statement statement = connection.createStatement();
            for (String sql : sqls) {
                if (StringUtils.isBlank(sql)) {
                    continue;
                }
                //非空条件判断
                statement.execute(sql);
            }
            statement.executeBatch();
            //PROBLEMS's
            statement.close();


            return true;
        }
    }

    private String listMapToCsv(List<Map<String, Object>> listmap, boolean wrap, String type) {
        StringBuilder result = new StringBuilder();
        for (Map<String, Object> item : listmap) {
            result.append(type + " ");
            for (Map.Entry<String, Object> entry : item.entrySet()) {
                Object value = entry.getValue();
                if (!wrap) {
                    if (value instanceof String) {
                        value = ((String) value).replace("\n", "");
                    }
                }
                result.append(value);
                result.append(" ");
            }
            result.append("\n");
        }

        return result.toString();
    }


    public Map<String, String> fetchAndCompare(Project project, List<String> sqls, String processId, boolean wrap) throws Exception {
        HashMap<String, String> result = new HashMap<>();
        try (Connection connection = getConnection(project)) {


            connection.setAutoCommit(false);
            List<EngineTask> engineTasks = queryEngineTaskByIdLike(project, processId);
            List<EngineFlow> engineFlows = queryEngineFlowById(project, processId);
            String taskString = engineTasks.stream().map(enginetask -> enginetask.toCsv(wrap)).collect(Collectors.joining("\n"));
            String flowString = engineFlows.stream().map(engineFlow -> engineFlow.toCsv(wrap)).collect(Collectors.joining("\n"));

            result.put("old", taskString + "\n" + flowString);
            Statement statement = connection.createStatement();
            for (String s : sqls) {
                //非空条件判断
                if (s != null && !s.trim().equals("")) {
                    statement.execute(s);
                }
            }

            List<EngineTask> engineTasksNew = queryEngineTaskByIdLike(project, processId);
            List<EngineFlow> engineFlowsNew = queryEngineFlowById(project, processId);
            String taskStringNew = engineTasksNew.stream().map(enginetask -> enginetask.toCsv(wrap)).collect(Collectors.joining("\n"));
            String flowStringNew = engineFlowsNew.stream().map(engineFlow -> engineFlow.toCsv(wrap)).collect(Collectors.joining("\n"));
            result.put("new", taskStringNew + "\n" + flowStringNew);
            return result;
        }
    }

    public void executeSql(Project project, String sql) throws Exception {
        try (Connection connection = getConnection(project)) {

            Statement statement = connection.createStatement();
            statement.execute(sql);
        }
    }

    /**
     * 查询Ecas 菜单唯一键
     *
     * @param
     * @param currentNum
     * @param pageSize
     * @param dbLinkYD01Name
     * @param dbLinkYD03Name
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryActionPowerUniq(Project project, int currentNum, int pageSize, String dbLinkYD01Name, String dbLinkYD02Name, String dbLinkYD03Name, Integer applid) throws Exception {
        if (StringUtils.isBlank(dbLinkYD01Name)) {
            dbLinkYD01Name = "";
        }
        if (StringUtils.isBlank(dbLinkYD02Name)) {
            dbLinkYD01Name = "";
        }
        if (StringUtils.isBlank(dbLinkYD03Name)) {
            dbLinkYD03Name = "";
        }
        if (applid == null) {
            applid = 999;
        }

        String sql = "with numlist as (\n" +
                "    SELECT rownum + " + (currentNum - 1) + " rn\n" +
                "    FROM dual\n" +
                "    CONNECT BY 1 = 1\n" +
                "           and rownum < " + pageSize + "\n" +
                "),\n" +
                "     dev as (\n" +
                "         select *\n" +
                "         from ECAS_ACTIONPOWER\n" +
                "         where applid = 999\n" +
                "     ),\n" +
                "\n" +
                "     yd01m as (\n" +
                "         select *\n" +
                "         from ECAS_ACTIONPOWER@YD01\n" +
                "         where applid = " + applid + "\n" +
                "     ),\n" +
                "     yd02m as (\n" +
                "         select *\n" +
                "         from ECAS_ACTIONPOWER@YD02\n" +
                "         where applid = " + applid + "\n" +
                "     ),\n" +
                "     yd03m as (\n" +
                "         select *\n" +
                "         from ECAS_ACTIONPOWER@YD03\n" +
                "         where applid = " + applid + "\n" +
                "     )\n" +
                "select r.rn,dev.description \"description\", " +
                "dev.powerbit \"dev\", " +
                "yd02m.powerbit \"yd02\", " +
                "yd01m.powerbit \"yd01\", " +
                "yd03m.powerbit \"yd03\"\n" +
                "from numlist r,\n" +
                "     dev ,\n" +
                "     yd01m,\n" +
                "     yd02m,\n" +
                "     yd03m\n" +
                "where dev.powerbit(+) = r.rn\n" +
                "  and yd01m.powerbit (+) = r.rn\n" +
                "  and yd02m.powerbit (+) = r.rn\n" +
                "  and yd03m.powerbit(+) = r.rn\n" +
                "order by rn";
        try (Connection connection = getConnection(project)) {

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            return queryForList(preparedStatement);
        }
    }

    /**
     * 查询Ecas 菜单唯一键
     *
     * @param
     * @param startNumber
     * @param pageSize
     * @param dbLinkYD01Name
     * @param dbLinkYD03Name
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryEcasMenuIdUniq(Project project, int startNumber, int pageSize, String dbLinkYD01Name, String dbLinkYD02Name, String dbLinkYD03Name, Integer applid) throws Exception {
        if (StringUtils.isBlank(dbLinkYD01Name)) {
            dbLinkYD01Name = "";
        }
        if (StringUtils.isBlank(dbLinkYD03Name)) {
            dbLinkYD03Name = "";
        }
        if (applid == null) {
            applid = 999;
        }

        String sql = "with numlist as (\n" +
                "    SELECT rownum + " + startNumber + " rn\n" +
                "    FROM dual\n" +
                "    CONNECT BY 1 = 1\n" +
                "           and rownum < " + pageSize + "\n" +
                "),\n" +
                "     dev as (\n" +
                "         select *\n" +
                "         from ECAS_MENU\n" +
                "         where applid = " + applid + "\n" +
                "     ),\n" +
                "\n" +
                "     yd01m as (\n" +
                "         select *\n" +
                "         from ECAS_MENU" + dbLinkYD01Name + "\n" +
                "         where applid = " + applid + "\n" +
                "     ),\n" +
                "     yd02m as (\n" +
                "         select *\n" +
                "         from ECAS_MENU" + dbLinkYD02Name + "\n" +
                "         where applid = " + applid + "\n" +
                "     ),\n" +
                "     yd03m as (\n" +
                "         select *\n" +
                "         from ECAS_MENU" + dbLinkYD03Name + "\n" +
                "         where applid = " + applid + "\n" +
                "     )\n" +
                "select r.rn, dev.menuid \"dev\", dev.name \"name\", yd01m.menuid \"yd01\",yd02m.menuid \"yd02\", yd03m.menuid \"yd03\"\n" +
                "from numlist r,\n" +
                "     dev ,\n" +
                "     yd01m,\n" +
                "     yd02m,\n" +
                "     yd03m\n" +
                "where dev.menuid(+) = r.rn\n" +
                "  and yd01m.menuid (+) = r.rn\n" +
                "  and yd02m.menuid (+) = r.rn\n" +
                "  and yd03m.menuid(+) = r.rn\n" +
                "order by rn";
        try (Connection connection = getConnection(project)) {

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            return queryForList(preparedStatement);
        }
    }

    /**
     * 查询权限
     *
     * @param
     * @param right
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryEngineRights(Project project, String right) throws Exception {
        try (Connection connection = getConnection(project)) {

            PreparedStatement preparedStatement = connection.prepareStatement("select * from ENGINE_RIGHTS where RIGHTS_ = ?");
            preparedStatement.setString(1, right);
            return queryForList(preparedStatement);
        }
    }


    public List<Map<String, Object>> queryEcasAppl(Project project) throws Exception {
        try (Connection connection = getConnection(project)) {

            PreparedStatement preparedStatement = connection.prepareStatement("select * from ECAS_APPL");
            return queryForList(preparedStatement);
        }
    }

    public List<Map<String, Object>> queryTopMenuOfApp(Project project, BigDecimal applid) throws Exception {
        try (Connection connection = getConnection(project)) {

            PreparedStatement preparedStatement = connection.prepareStatement("select * from ECAS_MENU where parent is null and applid=?");
            preparedStatement.setBigDecimal(1, applid);
            return queryForList(preparedStatement);
        }
    }

    public List<Map<String, Object>> queryMenuOfAppMenu(Project project, BigDecimal parentId, BigDecimal applid) throws Exception {
        try (Connection connection = getConnection(project)) {

            PreparedStatement preparedStatement = connection.prepareStatement("select * from ECAS_MENU where parent =? and applid=?");
            preparedStatement.setBigDecimal(1, parentId);
            preparedStatement.setBigDecimal(2, applid);
            return queryForList(preparedStatement);
        }
    }

    public List<EngineActionOutput> queryEngineActionOutputIdMatch(Project project, String actionId) throws Exception {
        try (Connection connection = getConnection(project)) {

            PreparedStatement preparedStatement = connection.prepareStatement("select * from ENGINE_ACTIONOUTPUT where ACTIONID_ =?");
            preparedStatement.setString(1, actionId);
            List<Map<String, Object>> maps = queryForList(preparedStatement);
            return maps.stream().map(item -> {
                /**
                 *   ACTIONID_  VARCHAR2(50) not null,
                 *     BEANID_    VARCHAR2(30) not null,
                 *     FIELDEXPR_ VARCHAR2(400),
                 *     TARGET_    VARCHAR2(40)
                 */
                EngineActionOutput engineActionOutput = new EngineActionOutput();
                engineActionOutput.setActionId((String) item.get("ACTIONID_"));
                engineActionOutput.setBeanId((String) item.get("BEANID_"));
                engineActionOutput.setFieldExpr((String) item.get("FIELDEXPR_"));
                engineActionOutput.setTarget((String) item.get("TARGET_"));
                return engineActionOutput;
            }).collect(Collectors.toList());
        }
    }

    public List<EngineActionInput> queryEngineActionInputIdMatch(Project project, String actionId) throws Exception {
        try (Connection connection = getConnection(project)) {

            PreparedStatement preparedStatement = connection.prepareStatement("select * from ENGINE_ACTIONINPUT where ACTIONID_ =?");
            preparedStatement.setString(1, actionId);
            List<Map<String, Object>> maps = queryForList(preparedStatement);
            return maps.stream().map(item -> {
                EngineActionInput engineActionInput = new EngineActionInput();
                engineActionInput.setActionId((String) item.get("ACTIONID_"));
                engineActionInput.setBeanId((String) item.get("BEANID_"));
                engineActionInput.setClass_((String) item.get("CLAZZ_"));
                engineActionInput.setFieldExpr((String) item.get("FIELDEXPR_"));
                engineActionInput.setSource((String) item.get("SOURCE_"));
                /**
                 *         *     ACTIONID_  VARCHAR2(50) not null,
                 **     BEANID_    VARCHAR2(30) not null,
                 **     CLAZZ_     VARCHAR2(100),
                 **     FIELDEXPR_ VARCHAR2(400),
                 **     SOURCE_    VARCHAR2(100
                 *
                 */
                return engineActionInput;
            }).collect(Collectors.toList());
        }
    }


    public List<EngineAction> queryEngineActionWithIdLike(Project project, String actionId) throws Exception {
        try (Connection connection = getConnection(project)) {

            PreparedStatement preparedStatement = connection.prepareStatement("select * from engine_action where id_ like ?");
            preparedStatement.setString(1, "%" + actionId + "%");
            List<Map<String, Object>> maps = queryForList(preparedStatement);
            return maps.stream().map(item -> {
                EngineAction engineAction = new EngineAction();
                engineAction.setId((String) item.get("ID_"));
                engineAction.setNamespace((String) item.get("NAMESPACE_"));
                engineAction.setUrl((String) item.get("URL_"));
                engineAction.setWindowparam((String) item.get("WINDOWPARAM_"));
                engineAction.setActionscript((String) item.get("ACTIONSCRIPT_"));
                engineAction.setActionintercept((String) item.get("ACTIONINTERCEPT_"));

                return engineAction;
            }).collect(Collectors.toList());
        }
    }

    private List<Map<String, Object>> queryForList(PreparedStatement preparedStatement) throws SQLException {
        List<Map<String, Object>> datas = new ArrayList<>();
        ResultSet resultSet = preparedStatement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        while (resultSet.next()) {
            Map<String, Object> resdata = new HashMap<String, Object>();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Object object = resultSet.getObject(i);
                resdata.put(columnName, object);
            }
            datas.add(resdata);
        }
        return datas;
    }

    public static Function<Map<String, Object>, EngineTask> engineTaskMapper = new Function<Map<String, Object>, EngineTask>() {
        @Override
        public EngineTask apply(Map<String, Object> map) {
            EngineTask engineTask = new EngineTask();
            engineTask.setId((String) map.get("ID_"));
            engineTask.setType((String) map.get("TYPE_"));
            engineTask.setTitle((String) map.get("TITLE_"));
            engineTask.setExpression((String) map.get("EXPRESSION_"));
            engineTask.setReturnvalue((String) map.get("RETURNVALUE_"));
            engineTask.setBussineskey((String) map.get("BUSSINESKEY_"));
            engineTask.setRights((String) map.get("RIGHTS_"));
            engineTask.setValidsecond(String.valueOf((BigDecimal) map.get("VALIDSECOND_")));
            engineTask.setListener((String) map.get("LISTENER_"));
            engineTask.setOpensecond(String.valueOf((BigDecimal) map.get("OPENSECOND_")));
            engineTask.setBussinesdesc((String) map.get("BUSSINESID_"));
            engineTask.setTasklistener((String) map.get("TASKLISTENER_"));
            return engineTask;
        }
    };

    public List<EngineTask> queryEngineTaskByIdLike(Project project, String id) throws Exception {
        String sql = "select id_, type_, title_, expression_, returnvalue_, bussineskey_, bussinesdesc_, rights_, validsecond_, listener_, opensecond_, bussinesid_, tasklistener_ from ENGINE_TASK where id_ like '" + id + "/_%' escape '/' order by ID_";
        try (Connection connection = getConnection(project)) {

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            List<Map<String, Object>> maps = queryForList(preparedStatement);
            return maps.stream().map(engineTaskMapper).collect(Collectors.toList());
        }
    }

    public List<EngineFlow> queryEngineFlowById(Project project, String id) throws Exception {
        String sql = "select processid_, source_, target_, condition_, order_ from ENGINE_FLOW where PROCESSID_='" + id + "' order by SOURCE_,TARGET_";
        try (Connection connection = getConnection(project)) {

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            List<Map<String, Object>> maps = queryForList(preparedStatement);
            return maps.stream().map(map -> {
                EngineFlow engineFlow = new EngineFlow();
                engineFlow.setProcessid((String) map.get("processid_"));
                engineFlow.setSource((String) map.get("SOURCE_"));
                engineFlow.setTarget((String) map.get("TARGET_"));
                engineFlow.setCondition((String) map.get("CONDITION_"));
                engineFlow.setOrder(String.valueOf(map.get("ORDER_")));
                return engineFlow;
            }).collect(Collectors.toList());
        }
    }

    public List<EngineTask> queryEngineTaskByExpression(Project project, String name) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, MalformedURLException, ClassNotFoundException {
        try (Connection connection = getConnection(project)) {

            PreparedStatement preparedStatement = connection.prepareStatement("select ID_, TYPE_, TITLE_, EXPRESSION_, RETURNVALUE_, BUSSINESKEY_, " +
                    "BUSSINESDESC_, RIGHTS_, VALIDSECOND_, LISTENER_, OPENSECOND_, BUSSINESID_, TASKLISTENER_ " +
                    " from ENGINE_TASK where upper(EXPRESSION_) like upper('%" + name + "%')");
            List<Map<String, Object>> maps = queryForList(preparedStatement);
            return maps.stream().map(engineTaskMapper).collect(Collectors.toList());
        }
    }

    /**
     * 查询系统日期
     *
     * @param
     * @return
     * @throws SQLException
     */
    public String querySysDay(Project project) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, MalformedURLException, ClassNotFoundException {
        try (Connection connection = getConnection(project)) {

            PreparedStatement preparedStatement = connection.prepareStatement("select PGET_SYSDATE() sd from dual");
            List<Map<String, Object>> maps = queryForList(preparedStatement);
            if (CollectionUtils.isEmpty(maps)) {
                return null;
            }
            return (String) maps.get(0).get("SD");
        }
    }


    public void textSearch(Project project) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, MalformedURLException, ClassNotFoundException {
        try (Connection connection = getConnection(project)) {

            PreparedStatement preparedStatement = connection.prepareStatement("declare\n" +
                    "    sqlt varchar(4000);\n" +
                    "    search varchar(1000):='XFUNDS_DUEINTBANK_DAYBALANCE';\n" +
                    "begin\n" +
                    "    for i in ( select OBJECT_NAME, OBJECT_TYPE, OWNER\n" +
                    "               from dba_objects\n" +
                    "               where OBJECT_TYPE in ('PACKAGE BODY', 'FUNCTION', 'PROCEDURE')\n" +
                    "                 and owner in ('PUBLIC', 'XFUNDS201701'))\n" +
                    "        loop\n" +
                    "            begin\n" +
                    "                sqlt:=DBMS_METADATA.GET_DDL(i.OBJECT_TYPE, i.OBJECT_NAME, i.OWNER);\n" +
                    "                if instr(upper(sqlt),upper(search))>0 then\n" +
                    "                    DBMS_OUTPUT.PUT_LINE('contains '||i.OBJECT_TYPE||'->'||i.OWNER||'.'||i.OBJECT_NAME);\n" +
                    "                    DBMS_OUTPUT.PUT_LINE('DBMS_METADATA.GET_DDL('''||i.OBJECT_TYPE||''',''' ||i.OBJECT_NAME||''','''|| i.OWNER||''')');\n" +
                    "                end if;\n" +
                    "            exception\n" +
                    "                when others  then\n" +
                    "--                     dbms_output.PUT('');\n" +
                    "                    DBMS_OUTPUT.PUT_LINE('excep '||i.OBJECT_TYPE||'->'||i.OWNER||'.'||i.OBJECT_NAME);\n" +
                    "                    DBMS_OUTPUT.PUT_LINE('DBMS_METADATA.GET_DDL('''||i.OBJECT_TYPE||''',''' ||i.OBJECT_NAME||''','''|| i.OWNER||''')');\n" +
                    "            end;\n" +
                    "        end loop;\n" +
                    "end");
            preparedStatement.execute();
        }

    }

    public List<XfundsBatch> queryXfundsBatchByEnterName(Project project, String entername) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, MalformedURLException, ClassNotFoundException {
        try (Connection connection = getConnection(project)) {

            PreparedStatement preparedStatement = connection.prepareStatement("select seqno||'' seqNo,DESCR,ENTERNAME from XFUNDS_BATCH_CTL where upper(ENTERNAME)  like upper('%" + entername + "%')");
            List<Map<String, Object>> maps = queryForList(preparedStatement);
            return maps.stream().map(it -> {
                XfundsBatch xfundsBatch = new XfundsBatch();
                //ID_, NAMESPACE_, URL_, WINDOWPARAM_, ACTIONSCRIPT_, ACTIONINTERCEPT_
                xfundsBatch.setSeqNo((String) it.get("SEQNO"));
                xfundsBatch.setDescr((String) it.get("DESCR"));
                xfundsBatch.setEnterName((String) it.get("ENTERNAME"));
                return xfundsBatch;
            }).collect(Collectors.toList());
        }
    }

    public List<EngineAction> queryEngineActionByActionScript(Project project, String name) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, MalformedURLException, ClassNotFoundException {
        try (Connection connection = getConnection(project)) {

            PreparedStatement preparedStatement = connection.prepareStatement("select ID_, NAMESPACE_, URL_, WINDOWPARAM_, ACTIONSCRIPT_, ACTIONINTERCEPT_  from ENGINE_ACTION where upper(ACTIONSCRIPT_) like upper('%" + name + "%')");
            List<Map<String, Object>> maps = queryForList(preparedStatement);
            return maps.stream().map(it -> {
                EngineAction engineAction = new EngineAction();
                //ID_, NAMESPACE_, URL_, WINDOWPARAM_, ACTIONSCRIPT_, ACTIONINTERCEPT_
                engineAction.setId((String) it.get("ID_"));
                engineAction.setNamespace((String) it.get("NAMESPACE_"));
                engineAction.setUrl((String) it.get("URL_"));
                engineAction.setWindowparam((String) it.get("WINDOWPARAM_"));
                engineAction.setActionscript((String) it.get("ACTIONSCRIPT_"));

                return engineAction;
            }).collect(Collectors.toList());
        }
    }

    public List<EcasMenu> queryHtmlRefMenu(Project project, String fileName) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, MalformedURLException, ClassNotFoundException {
        try (Connection connection = getConnection(project)) {

            PreparedStatement preparedStatement = connection.prepareStatement("select * from ecas_menu start with menuid=( select menuid from ECAS_MENU where url like '%" + fileName + "%' ) connect by prior parent=menuid ");
            List<Map<String, Object>> maps = queryForList(preparedStatement);
            //APPLID, MENUID, NAME, LVL, URL, PARENT, IMG, ISCHILD, GROUPID
            return maps.stream().map(it -> {
                EcasMenu ecasMenu = new EcasMenu();
                ecasMenu.setApplid(String.valueOf(it.get("APPLID")));
                ecasMenu.setMenuid(String.valueOf(it.get("MENUID")));
                ecasMenu.setName(String.valueOf(it.get("NAME")));
                ecasMenu.setLvl(String.valueOf(it.get("LVL")));
                ecasMenu.setUrl(String.valueOf(it.get("URL")));
                ecasMenu.setParent(String.valueOf(it.get("PARENT")));
                ecasMenu.setImg(String.valueOf(it.get("IMG")));
                ecasMenu.setIschild(String.valueOf(it.get("ISCHILD")));
                ecasMenu.setGroupid(String.valueOf(it.get("GROUPID")));
                return ecasMenu;
            }).collect(Collectors.toList());
        }
    }

    public List<EcasMenu> queryHtmlRefMenuTop(Project project, String fileName) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, MalformedURLException, ClassNotFoundException {
        try (Connection connection = getConnection(project)) {

            PreparedStatement preparedStatement = connection.prepareStatement(
                    " select * from ECAS_MENU where url like '%" + fileName + "%'");
            List<Map<String, Object>> maps = queryForList(preparedStatement);
            //APPLID, MENUID, NAME, LVL, URL, PARENT, IMG, ISCHILD, GROUPID
            return maps.stream().map(ecasMenuMapper).collect(Collectors.toList());
        }
    }


    public List<EcasMenu> queryMenuById(Project project, String menuId) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, MalformedURLException, ClassNotFoundException {
        try (Connection connection = getConnection(project)) {

            PreparedStatement preparedStatement = connection.prepareStatement(
                    " select * from ECAS_MENU where applid=999 and menuid= '" + menuId + "'");
            List<Map<String, Object>> maps = queryForList(preparedStatement);
            //APPLID, MENUID, NAME, LVL, URL, PARENT, IMG, ISCHILD, GROUPID
            return maps.stream().map(ecasMenuMapper).collect(Collectors.toList());
        }
    }

    public static Function<Map<String, Object>, EcasMenu> ecasMenuMapper = new Function<Map<String, Object>, EcasMenu>() {
        @Override
        public EcasMenu apply(Map<String, Object> it) {
            EcasMenu ecasMenu = new EcasMenu();
            ecasMenu.setApplid(String.valueOf(it.get("APPLID")));
            ecasMenu.setMenuid(String.valueOf(it.get("MENUID")));
            ecasMenu.setName(String.valueOf(it.get("NAME")));
            ecasMenu.setLvl(String.valueOf(it.get("LVL")));
            ecasMenu.setUrl(String.valueOf(it.get("URL")));
            ecasMenu.setParent(String.valueOf(it.get("PARENT")));
            ecasMenu.setImg(String.valueOf(it.get("IMG")));
            ecasMenu.setIschild(String.valueOf(it.get("ISCHILD")));
            ecasMenu.setGroupid(String.valueOf(it.get("GROUPID")));
            return ecasMenu;
        }
    };

    public List<String> queryUserTables(Project project) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, MalformedURLException, ClassNotFoundException {
        try (Connection connection = getConnection(project)) {

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "select TABLE_NAME from USER_TABLES order by TABLE_NAME desc");
            List<Map<String, Object>> maps = queryForList(preparedStatement);
            //APPLID, MENUID, NAME, LVL, URL, PARENT, IMG, ISCHILD, GROUPID
            return maps.stream().map(item -> (String) item.get("TABLE_NAME")).collect(Collectors.toList());
        }
    }

    public List<UserTabCols> queryUserTablesCols(Project project, String tableName) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, MalformedURLException, ClassNotFoundException {
        List<UserTabCols> userTabColsList = new ArrayList<>();
        try (Connection connection = getConnection(project)) {

            PreparedStatement ps = connection.prepareStatement(
                    "SELECT cc.*,cm.COMMENTS FROM USER_TAB_COLS cc,USER_COL_COMMENTS cm WHERE cc.TABLE_NAME = ? and cc.COLUMN_NAME=cm.COLUMN_NAME and cc.TABLE_NAME=cm.TABLE_NAME order by COLUMN_ID asc");
            ps.setString(1, tableName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UserTabCols userTabCols = new UserTabCols();
                userTabCols.setTableName(rs.getString("TABLE_NAME"));
                userTabCols.setColumnName(rs.getString("COLUMN_NAME"));
                userTabCols.setDataType(rs.getString("DATA_TYPE"));
                userTabCols.setDataLength(rs.getInt("DATA_LENGTH"));
                userTabCols.setDataPrecision(rs.getInt("DATA_PRECISION"));
                userTabCols.setDataScale(rs.getInt("DATA_SCALE"));
                userTabCols.setNullable(rs.getString("NULLABLE"));
                userTabCols.setColumnId(rs.getInt("COLUMN_ID"));
                userTabCols.setDefaultLength(rs.getInt("DEFAULT_LENGTH"));
                userTabCols.setDataDefault(rs.getString("DATA_DEFAULT"));
                userTabCols.setNumDistinct(rs.getLong("NUM_DISTINCT"));
                userTabCols.setLowValue(rs.getString("LOW_VALUE"));
                userTabCols.setHighValue(rs.getString("HIGH_VALUE"));
                userTabCols.setDensity(rs.getDouble("DENSITY"));
                userTabCols.setNumNulls(rs.getLong("NUM_NULLS"));
                userTabCols.setNumBuckets(rs.getLong("NUM_BUCKETS"));
                userTabCols.setLastAnalyzed(rs.getTimestamp("LAST_ANALYZED"));
                userTabCols.setSampleSize(rs.getLong("SAMPLE_SIZE"));
                userTabCols.setCharacterSetName(rs.getString("CHARACTER_SET_NAME"));
                userTabCols.setCharColDeclLength(rs.getLong("CHAR_COL_DECL_LENGTH"));
                userTabCols.setGlobalStats(rs.getString("GLOBAL_STATS"));
                userTabCols.setUserStats(rs.getString("USER_STATS"));
                userTabCols.setComments(rs.getString("COMMENTS"));
                userTabColsList.add(userTabCols);
            }
            return userTabColsList;
        }
    }

    /**
     * 通过Id
     *
     * @param applid
     * @param menuId
     * @param
     * @param env
     * @return
     * @throws SQLException
     */
    public EcasMenu queryEcasMenu(int applid, int menuId, Project project, String env) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, MalformedURLException, ClassNotFoundException {
        String envSql = "";

        if (!"DEV".equals(env) && !"".equals(env)) {
            envSql = "@" + env;
        }
        try (Connection connection = getConnection(project)) {

            String sql = "SELECT APPLID, MENUID, NAME, LVL, URL, PARENT, IMG, ISCHILD, GROUPID FROM ECAS_MENU" + envSql + " WHERE APPLID=? AND MENUID=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, applid);
            statement.setInt(2, menuId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return ecasMenuMapper(resultSet);
            }
            return null;
        }
    }

    public EcasActionPower queryActionPower(int applid, int powerbit, Project project, String env) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, MalformedURLException, ClassNotFoundException {
        String envSql = "";

        if (!"DEV".equals(env) && !"".equals(env)) {
            envSql = "@" + env;
        }
        String sql = "SELECT APPLID, POWERBIT, PATH, DESCRIPTION, ENABLED, MODULENAME, WEIGHT, ENGMODULE, ENGDESC, MENUID FROM ECAS_ACTIONPOWER" + envSql + " WHERE APPLID = ? AND POWERBIT = ?";
        try (Connection connection = getConnection(project)) {

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, applid);
            statement.setInt(2, powerbit);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new EcasActionPower(
                        resultSet.getInt("APPLID"),
                        resultSet.getInt("POWERBIT"),
                        resultSet.getString("PATH"),
                        resultSet.getString("DESCRIPTION"),
                        resultSet.getInt("ENABLED"),
                        resultSet.getString("MODULENAME"),
                        resultSet.getInt("WEIGHT"),
                        resultSet.getString("ENGMODULE"),
                        resultSet.getString("ENGDESC"),
                        resultSet.getInt("MENUID")
                );
            }
            return null;
        }
    }

    public List<UserColComments> queryUserColComments(Project project, String tableName) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, MalformedURLException, ClassNotFoundException {
        List<UserColComments> userColCommentsList = new ArrayList<>();
        try (Connection connection = getConnection(project)) {

            PreparedStatement ps = connection.prepareStatement(
                    "select * from USER_COL_COMMENTS where TABLE_NAME=?");
            ps.setString(1, tableName);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UserColComments userColComments = new UserColComments();
                userColComments.setTableName(rs.getString("TABLE_NAME"));
                userColComments.setColumnName(rs.getString("COLUMN_NAME"));
                userColComments.setComments(rs.getString("COMMENTS"));

                userColCommentsList.add(userColComments);
            }
            return userColCommentsList;
        }
    }

    public List<String> queryUserTabComments(Project project, String tableName) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, MalformedURLException, ClassNotFoundException {
        List<String> userTabsComments = new ArrayList<>();
        try (Connection connection = getConnection(project)) {

            PreparedStatement ps = connection.prepareStatement(
                    "select COMMENTS from USER_TAB_COMMENTS where TABLE_NAME=?");
            ps.setString(1, tableName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                userTabsComments.add(rs.getString("COMMENTS"));
            }
            return userTabsComments;
        }
    }

    public EcasMenu ecasMenuMapper(ResultSet rs) throws SQLException {
        EcasMenu menu = new EcasMenu();
        menu.setApplid(rs.getString("APPLID"));
        menu.setMenuid(rs.getString("MENUID"));
        menu.setName(rs.getString("NAME"));
        menu.setLvl(rs.getString("LVL"));
        menu.setUrl(rs.getString("URL"));
        menu.setParent(rs.getString("PARENT"));
        menu.setImg(rs.getString("IMG"));
        menu.setIschild(rs.getString("ISCHILD"));
        menu.setGroupid(rs.getString("GROUPID"));
        return menu;
    }

    public List<EcasMenu> queryRootMenus(String applid, Project project) throws Exception {
        List<EcasMenu> menus = new ArrayList<>();
        try (Connection connection = getConnection(project)) {

            PreparedStatement stmt = connection.prepareStatement(
                    "select applid, menuid, name, lvl, url, parent, img, ischild, groupid from ECAS_MENU where parent is null and applid=?");
            stmt.setString(1, applid);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                EcasMenu menu = ecasMenuMapper(rs);
                // Set other properties as necessary
                menus.add(menu);
            }
        }

        return menus;
    }

    public List<EcasMenu> querySubMenus(String applid, String parent, Project project) throws Exception {
        List<EcasMenu> menus = new ArrayList<>();
        try (Connection connection = getConnection(project)) {

            PreparedStatement stmt = connection.prepareStatement(
                    "select applid, menuid, name, lvl, url, parent, img, ischild, groupid from ECAS_MENU where parent =?  and applid=?");
            stmt.setString(1, parent);
            stmt.setString(2, applid);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                EcasMenu menu = ecasMenuMapper(rs);
                // Set other properties as necessary
                menus.add(menu);
            }

            return menus;
        }
    }


    public List<String> queryRootActionPower(String s, Project project) throws Exception {
        List<String> menus = new ArrayList<>();
        try (Connection connection = getConnection(project)) {

            PreparedStatement stmt = connection.prepareStatement(
                    "select distinct MODULENAME as \"MODULENAME\" from ECAS_ACTIONPOWER where APPLID=999  order by MODULENAME");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // Set other properties as necessary
                menus.add(rs.getString("MODULENAME"));
            }

            return menus;
        }
    }

    public List<EcasActionPower> querySubActionPower(String s, Project project) throws Exception {
        List<EcasActionPower> menus = new ArrayList<>();
        try (Connection connection = getConnection(project)) {

            PreparedStatement stmt = connection.prepareStatement(
                    "select applid, powerbit, path, description, enabled, modulename, weight," +
                            " engmodule, engdesc, menuid from ECAS_ACTIONPOWER where MODULENAME=?  order by description");
            stmt.setString(1, s);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                EcasActionPower ecasActionPower = new EcasActionPower();
                ecasActionPower.setApplid(resultSet.getInt("APPLID"));
                ecasActionPower.setPowerbit(resultSet.getInt("POWERBIT"));
                ecasActionPower.setPath(resultSet.getString("PATH"));
                ecasActionPower.setDescription(resultSet.getString("DESCRIPTION"));
                ecasActionPower.setEnabled(resultSet.getInt("ENABLED"));
                ecasActionPower.setModuleName(resultSet.getString("MODULENAME"));
                ecasActionPower.setWeight(resultSet.getInt("WEIGHT"));
                ecasActionPower.setEngModule(resultSet.getString("ENGMODULE"));
                ecasActionPower.setEngDesc(resultSet.getString("ENGDESC"));
                ecasActionPower.setMenuId(resultSet.getInt("MENUID"));
                menus.add(ecasActionPower);
            }

            return menus;
        }
    }

    public List<XFunCodeDef> queryXfundsCodeDef(Project project,String codeType)throws Exception{
        String sql = "SELECT id, parentid, type, codeval, isval, name, sname, dispseq, rmk1, rmk2, rmk3, rmk4 FROM XFUNDS_BASE_CODEDEF WHERE type=?";
        try(Connection connection = getConnection(project);){
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, codeType);

            ResultSet rs = ps.executeQuery();
            List<XFunCodeDef> list = new ArrayList<>();
            while (rs.next()) {
                XFunCodeDef obj = new XFunCodeDef();
                obj.setId(rs.getString("ID"));
                obj.setParentId(rs.getString("PARENTID"));
                obj.setType(rs.getString("TYPE"));
                obj.setCodeVal(rs.getString("CODEVAL"));
                obj.setIsVal(rs.getString("ISVAL"));
                obj.setName(rs.getString("NAME"));
                obj.setsName(rs.getString("SNAME"));
                obj.setDispSeq(rs.getInt("DISPSEQ"));
                obj.setRmk1(rs.getString("RMK1"));
                obj.setRmk2(rs.getString("RMK2"));
                obj.setRmk3(rs.getString("RMK3"));
                obj.setRmk4(rs.getString("RMK4"));
                list.add(obj);
            }
            return list;
        }

    }


}