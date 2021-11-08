package cn.boz.jb.plugin.idea.utils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

public class DBUtils {

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


    /**
     * @param jdbcUser
     * @param jdbcPass
     * @param jdbcUrl
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Connection getConnection(String jdbcUser, String jdbcPass, String jdbcUrl, String jdbcDriver) throws Exception {
        File file = new File(jdbcDriver);
        URLClassLoader loader = new URLClassLoader(new URL[]{file.toURI().toURL()});
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
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return true;

    }
}
