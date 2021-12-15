package cn.boz.jb.plugin.idea.utils;

import cn.boz.jb.plugin.idea.configurable.SpdEditorState;

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

    public static Connection getConnection(SpdEditorState state) throws MalformedURLException, SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
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

//            CallableStatement callableStatement = connection.prepareCall(sqls);
        }
        return true;

    }

    public List<Map<String, Object>> queryEcasAppl(Connection connection  ) throws Exception {
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

    public List<Map<String, Object>> queryMenuOfAppMenu(Connection connection,BigDecimal parentId, BigDecimal applid) throws Exception {
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from ECAS_MENU where parent =? and applid=?")) {
            System.out.println("parent:"+parentId+" appid:"+applid);

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
