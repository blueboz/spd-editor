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
        String s = DBUtils.getInstance().querySysDay(null);
        System.out.println(s);
    }
}
