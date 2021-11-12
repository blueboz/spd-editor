package cn.boz.test;

import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.alibaba.fastjson.JSON;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestJdbc {



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

    @Test
    public void test() {
        String path = "D:/Code/FMS/FMSS_xfunds/xfunds/WebContent/WEB-INF/lib/orai18n-19.3.0.0.jar;D:/Code/FMS/FMSS_xfunds/xfunds/WebContent/WEB-INF/lib/ojdbc8.jar";
        try (Connection connection = DBUtils.getConnection("xfunds201701", "Xfunds_1234", "jdbc:oracle:thin:@21.96.5.85:1521:FMSS", path)) {
            List<Map<String, Object>> maps = queryEngineActionWithIdLike(connection, "/initFrameworkProto.do");
            for (Map<String, Object> map : maps) {
                String id_ = (String) map.get("ID_");
                List<Map<String, Object>> actionInputs = queryEngineActionInputIdMatch(connection, id_);
                System.out.println(JSON.toJSONString(map));
                System.out.println(JSON.toJSONString(actionInputs));
                List<Map<String, Object>> actionOutputs = queryEngineActionOutputIdMatch(connection, id_);
                System.out.println(JSON.toJSONString(actionOutputs));
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testSwingJList(){
        System.out.println("hel");

    }
}
