package cn.boz.test;

import cn.boz.jb.plugin.idea.utils.DBUtils;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.SQLException;

public class DBTest {

    @Test
    public void dbTest() throws NoSuchMethodException, InvocationTargetException, InstantiationException, SQLException, IllegalAccessException, MalformedURLException, ClassNotFoundException {
        Connection connection = DBUtils.getConnection("xfunds201701", "Xfunds_1234", "jdbc:oracle:thin:@21.96.5.85:1521:FMSS", "oracle.jdbc.OracleDriver");

        DBUtils.getInstance().textSearch(connection);
    }
}
