package cn.boz.test;

import cn.boz.jb.plugin.idea.bean.EngineAction;
import cn.boz.jb.plugin.idea.bean.EngineActionInput;
import cn.boz.jb.plugin.idea.bean.EngineActionOutput;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.alibaba.fastjson.JSON;
import oracle.sql.BLOB;
import org.testng.annotations.Test;

import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TestJdbc {

    @Test
    public void test2() {
//        DBUtils instance = DBUtils.getInstance();
//        Connection connection = instance.getConnection(null);
//        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from engine_event")) {
////                preparedStatement.setString(1, "");
//            List<Map<String, Object>> maps = queryForList(preparedStatement);
//            maps.stream().map(item -> {
//                System.out.println(item);
//
//                BLOB blob = (BLOB) item.get("START_OBJECT_");
//
//
//                return null;
//            }).collect(Collectors.toList());
//
//        }
      
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
    public void test() throws Exception {
        DBUtils instance = DBUtils.getInstance();
        List<EngineAction> maps = instance.queryEngineActionWithIdLike(null, "/initFrameworkProto.do");
        for (EngineAction action : maps) {
            String id_ = (String) action.getId();
            List<EngineActionInput> actionInputs = instance.queryEngineActionInputIdMatch(null, id_);
            System.out.println(JSON.toJSONString(action));
            System.out.println(JSON.toJSONString(actionInputs));
            List<EngineActionOutput> actionOutputs = instance.queryEngineActionOutputIdMatch(null, id_);
            System.out.println(JSON.toJSONString(actionOutputs));
            break;
        }
    }

    private EngineAction engineAction;
    private List<EngineActionInput> engineActionInput;
    private List<EngineActionOutput> engineActionOutput;

    @Test
    public void testgui() throws Exception {

        DBUtils instance = DBUtils.getInstance();
        List<EngineAction> maps = instance.queryEngineActionWithIdLike(null, "/modifyIfaceStatus.do");
        for (EngineAction action : maps) {
            String id_ = (String) action.getId();
            List<EngineActionInput> actionInputs = instance.queryEngineActionInputIdMatch(null, id_);
            System.out.println(JSON.toJSONString(action));
            System.out.println(JSON.toJSONString(actionInputs));
            List<EngineActionOutput> actionOutputs = instance.queryEngineActionOutputIdMatch(null, id_);
            System.out.println(JSON.toJSONString(actionOutputs));
            engineAction = action;
            engineActionInput = actionInputs;
            engineActionOutput = actionOutputs;
            break;
        }
        SwingUtilities.invokeLater(() -> {
            try {
                //注册字体
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//                EngineActionDialog myFramw = new EngineActionDialog(engineAction,engineActionInput,engineActionOutput);
//                myFramw.pack();
//                myFramw.setVisible(true);
////                Rectangle rectangle = ge.getMaximumWindowBounds();
////                myFramw.setBounds((int) rectangle.getWidth() / 2, 0, rectangle.width / 2, rectangle.height);
//                myFramw.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Thread.sleep(100000);

    }

    @Test
    public void testSwingJList() {
        System.out.println("hel");

    }
}
