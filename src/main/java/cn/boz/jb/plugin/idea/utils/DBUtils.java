package cn.boz.jb.plugin.idea.utils;

import cn.boz.jb.plugin.floweditor.gui.utils.StringUtils;
import cn.boz.jb.plugin.idea.bean.EcasMenu;
import cn.boz.jb.plugin.idea.bean.EngineAction;
import cn.boz.jb.plugin.idea.bean.EngineActionInput;
import cn.boz.jb.plugin.idea.bean.EngineActionOutput;
import cn.boz.jb.plugin.idea.bean.EngineFlow;
import cn.boz.jb.plugin.idea.bean.EngineTask;
import cn.boz.jb.plugin.idea.configurable.SpdEditorDBSettings;
import cn.boz.jb.plugin.idea.configurable.SpdEditorDBState;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import icons.SpdEditorIcons;
import org.apache.commons.collections.CollectionUtils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DBUtils {

    private static final DBUtils INST = new DBUtils();

    private DBUtils() {
        //应该怎么初始化，作为一个Service?
    }

    public static DBUtils getInstance() {
        return INST;
    }

    public static void dbExceptionProcessor(Exception ee, Project project){

        String errMsg = generateRecrusiveException(ee);
        int idx = Messages.showDialog(errMsg, "Oops!Something wrong happen!", new String[]{"Check Db Config", "Never Mind"}, 0, SpdEditorIcons.FLOW_16_ICON);
        if (idx == 0) {

            ShowSettingsUtil.getInstance().showSettingsDialog(project, SpdEditorDBSettings.class);
        }
    }

    public static String generateRecrusiveException(Throwable e) {
        if (e == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        String msg = stringWriter.toString();
        String[] split = msg.split("\n");
        for (String s : split) {
            if(s.contains("at")){

                if (s.contains("cn.boz")) {
                    sb.append(s);
                }
            }else{
                sb.append(s);
            }

        }
        return sb.toString();
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
    public static boolean testConnection(String jdbcUser, String jdbcPass, String jdbcUrl, String jdbcDriverText) {
        try (Connection connection = getConnection(jdbcUser, jdbcPass, jdbcUrl, jdbcDriverText)) {
            Statement statement = connection.createStatement();
            boolean execute = statement.execute("select * from dual");
            return execute;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() throws MalformedURLException, SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        SpdEditorDBState instance = SpdEditorDBState.getInstance();
        return getConnection(instance);
    }

    public static Connection getConnection(SpdEditorDBState state) throws MalformedURLException, SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return getConnection(state.jdbcUserName, state.jdbcPassword, state.jdbcUrl, state.jdbcDriver);
    }


    /**
     * @param jdbcUser
     * @param jdbcPass
     * @param jdbcUrl
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Connection getConnection(String jdbcUser, String jdbcPass, String jdbcUrl, String jdbcDriver) throws ClassNotFoundException, MalformedURLException, NoSuchMethodException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String[] driverpaths = jdbcDriver.split(";");
        URL[] urls = new URL[driverpaths.length];
        for (int i = 0; i < driverpaths.length; i++) {
            String driverpath = driverpaths[i];
            urls[i] = new File(driverpath).toURI().toURL();
        }
        URLClassLoader loader = new URLClassLoader(urls);
        Class clazz = loader.loadClass("oracle.jdbc.driver.OracleDriver");
        Driver driver = (Driver) clazz.getDeclaredConstructor().newInstance();
        Properties p = new Properties();
        p.put("user", jdbcUser);
        p.put("password", jdbcPass);
        Connection connect = driver.connect(jdbcUrl, p);
        return connect;
    }

    /**
     * @param jdbcUser WYSIWYG
     * @param jdbcPass WYSIWYG
     * @param jdbcUrl  WYSIWYG
     * @param sqls     SQL can be split with \n
     * @return
     */
    public static boolean executeSql(String jdbcUser, String jdbcPass, String jdbcUrl, String jdbcDriver, List<String> sqls) throws MalformedURLException, SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        try (Connection connection = getConnection(jdbcUser, jdbcPass, jdbcUrl, jdbcDriver)) {
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

        }
        return true;

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


    public Map<String, String> fetchAndCompare(List<String> sqls, String processId, boolean wrap) {
        HashMap<String, String> result = new HashMap<>();
        try (Connection connection = getConnection(SpdEditorDBState.getInstance())) {

            try {

                connection.setAutoCommit(false);
                List<EngineTask> engineTasks = queryEngineTaskByIdLike(connection, processId);
                List<EngineFlow> engineFlows = queryEngineFlowById(connection, processId);
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

                List<EngineTask> engineTasksNew = queryEngineTaskByIdLike(connection, processId);
                List<EngineFlow> engineFlowsNew = queryEngineFlowById(connection, processId);
                String taskStringNew = engineTasksNew.stream().map(enginetask -> enginetask.toCsv(wrap)).collect(Collectors.joining("\n"));
                String flowStringNew = engineFlowsNew.stream().map(engineFlow -> engineFlow.toCsv(wrap)).collect(Collectors.joining("\n"));

                result.put("new", taskStringNew + "\n" + flowStringNew);
            } finally {
                connection.rollback();
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {

        }
        return result;
    }


    public void executeSql(Connection connection, String sql) throws Exception {
        try (Statement statement = connection.createStatement();) {
            statement.execute(sql);
        }
    }


    /**
     * 查询Ecas 菜单唯一键
     *
     * @param connection
     * @param pageNum
     * @param pageSize
     * @param dbLinkYD01Name
     * @param dbLinkYD03Name
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryEcasMenuIdUniq(Connection connection, int pageNum, int pageSize, String dbLinkYD01Name, String dbLinkYD03Name, Integer applid) throws Exception {
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
                "    SELECT rownum + " + pageNum * pageSize + " rn\n" +
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
                "     yd03m as (\n" +
                "         select *\n" +
                "         from ECAS_MENU" + dbLinkYD03Name + "\n" +
                "         where applid = " + applid + "\n" +
                "     )\n" +
                "select r.rn, dev.menuid \"dev\", yd01m.menuid \"yd01\", yd03m.menuid \"yd03\"\n" +
                "from numlist r,\n" +
                "     dev ,\n" +
                "     yd01m,\n" +
                "     yd03m\n" +
                "where dev.menuid(+) = r.rn\n" +
                "  and yd01m.menuid (+) = r.rn\n" +
                "  and yd03m.menuid(+) = r.rn\n" +
                "order by rn";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            return queryForList(preparedStatement);
        }
    }

    /**
     * 查询权限
     *
     * @param connection
     * @param right
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryEngineRights(Connection connection, String right) throws Exception {
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from ENGINE_RIGHTS where RIGHTS_ = ?")) {
            preparedStatement.setString(1, right);
            return queryForList(preparedStatement);
        }
    }

    public List<Map<String, Object>> queryEcasAppl(Connection connection) throws Exception {
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from ECAS_APPL")) {
            return queryForList(preparedStatement);
        }
    }

    public List<Map<String, Object>> queryTopMenuOfApp(Connection connection, BigDecimal applid) throws Exception {
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from ECAS_MENU where parent is null and applid=?")) {
            preparedStatement.setBigDecimal(1, applid);
            return queryForList(preparedStatement);
        }
    }

    public List<Map<String, Object>> queryMenuOfAppMenu(Connection connection, BigDecimal parentId, BigDecimal applid) throws Exception {
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from ECAS_MENU where parent =? and applid=?")) {
//            System.out.println("parent:"+parentId+" appid:"+applid);

            preparedStatement.setBigDecimal(1, parentId);
            preparedStatement.setBigDecimal(2, applid);
            return queryForList(preparedStatement);
        }
    }

    public List<EngineActionOutput> queryEngineActionOutputIdMatch(Connection connection, String actionId) throws Exception {
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from ENGINE_ACTIONOUTPUT where ACTIONID_ =?")) {
            preparedStatement.setString(1, actionId);
            List<Map<String, Object>> maps = queryForList(preparedStatement);
            return maps.stream().map(item->{
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

    public List<EngineActionInput>  queryEngineActionInputIdMatch(Connection connection, String actionId) throws Exception {
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from ENGINE_ACTIONINPUT where ACTIONID_ =?")) {
            preparedStatement.setString(1, actionId);
            List<Map<String, Object>> maps = queryForList(preparedStatement);
            return maps.stream().map(item->{
                EngineActionInput engineActionInput = new EngineActionInput();
                engineActionInput.setActionId((String) item.get("ACTIONID"));
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
            }).collect(Collectors.toList()) ;
        }
    }


    public List<EngineAction> queryEngineActionWithIdLike(Connection connection, String actionId) throws Exception {
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from engine_action where id_ like ?")) {
            preparedStatement.setString(1, "%" + actionId + "%");
            List<Map<String, Object>> maps = queryForList(preparedStatement);
            return maps.stream().map(item->{
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

    public List<EngineTask> queryEngineTaskByIdLike(Connection connection, String id) throws SQLException {
        ;
        String sql = "select id_, type_, title_, expression_, returnvalue_, bussineskey_, bussinesdesc_, rights_, validsecond_, listener_, opensecond_, bussinesid_, tasklistener_ from ENGINE_TASK where id_ like '" + id + "/_%' escape '/' order by ID_";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
            List<Map<String, Object>> maps = queryForList(preparedStatement);
            return maps.stream().map(engineTaskMapper).collect(Collectors.toList());
        }
    }

    public List<EngineFlow> queryEngineFlowById(Connection connection, String id) throws SQLException {
        String sql = "select processid_, source_, target_, condition_, order_ from ENGINE_FLOW where PROCESSID_='" + id + "' order by SOURCE_,TARGET_";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
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

    public List<EngineTask> queryEngineTaskByExpression(Connection connection, String name) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("select ID_, TYPE_, TITLE_, EXPRESSION_, RETURNVALUE_, BUSSINESKEY_, " +
                "BUSSINESDESC_, RIGHTS_, VALIDSECOND_, LISTENER_, OPENSECOND_, BUSSINESID_, TASKLISTENER_ " +
                " from ENGINE_TASK where EXPRESSION_ like '%" + name + "%'")) {
            List<Map<String, Object>> maps = queryForList(preparedStatement);
            return maps.stream().map(engineTaskMapper).collect(Collectors.toList());
        }
    }

    /**
     * 查询系统日期
     * @param connection
     * @return
     * @throws SQLException
     */
    public String querySysDay(Connection connection) throws  SQLException{
        try (PreparedStatement preparedStatement = connection.prepareStatement("select PGET_SYSDATE() sd from dual")) {
            List<Map<String, Object>> maps = queryForList(preparedStatement);
            if(CollectionUtils.isEmpty(maps)){
                return null;
            }
            return (String) maps.get(0).get("SD");
        }

    }

    public List<EngineAction> queryEngineActionByActionScript(Connection connection, String name) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("select ID_, NAMESPACE_, URL_, WINDOWPARAM_, ACTIONSCRIPT_, ACTIONINTERCEPT_  from ENGINE_ACTION where ACTIONSCRIPT_ like '%" + name + "%'")) {
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

    public List<EcasMenu> queryHtmlRefMenu(Connection connection, String fileName) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from ecas_menu start with menuid=( select menuid from ECAS_MENU where url like '%" + fileName + "%' ) connect by prior parent=menuid ")) {
            List<Map<String, Object>> maps = queryForList(preparedStatement);
            //APPLID, MENUID, NAME, LVL, URL, PARENT, IMG, ISCHILD, GROUPID
            return maps.stream().map(it -> {
                EcasMenu ecasMenu = new EcasMenu();
                ecasMenu.setApplid(String.valueOf( it.get("APPLID")));
                ecasMenu.setMenuid(String.valueOf( it.get("MENUID")));
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
}
