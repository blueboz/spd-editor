package cn.boz.jb.plugin.idea.utils;

import cn.boz.jb.plugin.floweditor.gui.utils.StringUtils;
import cn.boz.jb.plugin.idea.configurable.SpdEditorDBState;

import java.io.File;
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

public class DBUtils {

    private static final DBUtils INST = new DBUtils();

    private DBUtils() {
        //应该怎么初始化，作为一个Service?
    }

    public static DBUtils getInstance() {
        return INST;
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

    private String listMapToCsv(List<Map<String, Object>> listmap, boolean wrap) {
        StringBuilder result = new StringBuilder();
        for (Map<String, Object> item : listmap) {
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


    public Map<String, String> fetchAndCompare(List<String> sqls, String generateQueryEngineTaskSql, String generateQueryProcessTaskSql, boolean wrap) {
        HashMap<String, String> result = new HashMap<>();
        try (Connection connection = getConnection(SpdEditorDBState.getInstance())) {

            try {

                connection.setAutoCommit(false);
                List<Map<String, Object>> engineTasksOld = queryForList(connection.prepareStatement(generateQueryEngineTaskSql));
                List<Map<String, Object>> processOld = queryForList(connection.prepareStatement(generateQueryProcessTaskSql));
                result.put("old", listMapToCsv(engineTasksOld, wrap) + listMapToCsv(processOld, wrap));
                Statement statement = connection.createStatement();
                for (String s : sqls) {
                    //非空条件判断
                    if (s != null && !s.trim().equals("")) {
                        statement.execute(s);
                    }
                }

                List<Map<String, Object>> engineTasksNew = queryForList(connection.prepareStatement(generateQueryEngineTaskSql));
                List<Map<String, Object>> processOldNew = queryForList(connection.prepareStatement(generateQueryProcessTaskSql));
                result.put("new", listMapToCsv(engineTasksNew, wrap) + listMapToCsv(processOldNew, wrap));
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

    public List<Map<String, Object>> queryEngineActionOutputIdMatch(Connection connection, String actionId) throws Exception {
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from ENGINE_ACTIONOUTPUT where ACTIONID_ =?")) {
            preparedStatement.setString(1, actionId);
            return queryForList(preparedStatement);
        }
    }

    public List<Map<String, Object>> queryEngineActionInputIdMatch(Connection connection, String actionId) throws Exception {
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from ENGINE_ACTIONINPUT where ACTIONID_ =?")) {
            preparedStatement.setString(1, actionId);
            return queryForList(preparedStatement);
        }
    }

    public List<Map<String, Object>> queryEngineActionIdMatch(Connection connection, String actionId) throws Exception {
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from engine_action where id_ = ?")) {
            preparedStatement.setString(1, actionId);
            return queryForList(preparedStatement);
        }
    }

    public List<Map<String, Object>> queryEngineActionWithIdLike(Connection connection, String actionId) throws Exception {
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from engine_action where id_ like ?")) {
            preparedStatement.setString(1, "%" + actionId + "%");
            return queryForList(preparedStatement);
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

}
