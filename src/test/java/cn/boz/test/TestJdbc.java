package cn.boz.test;

import cn.boz.jb.plugin.idea.bean.EngineAction;
import cn.boz.jb.plugin.idea.bean.EngineActionInput;
import cn.boz.jb.plugin.idea.bean.EngineActionOutput;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.alibaba.fastjson.JSON;
import org.testng.annotations.Test;

import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class TestJdbc {




    @Test
    public void test() {
        String path = "D:/Code/FMS/FMSS_xfunds/xfunds/WebContent/WEB-INF/lib/orai18n-19.3.0.0.jar;D:/Code/FMS/FMSS_xfunds/xfunds/WebContent/WEB-INF/lib/ojdbc8.jar";
        DBUtils instance = DBUtils.getInstance();
        try (Connection connection = instance.getConnection("xfunds201701", "Xfunds_1234", "jdbc:oracle:thin:@21.96.5.85:1521:FMSS", path)) {
            List<EngineAction> maps = instance.queryEngineActionWithIdLike(connection, "/initFrameworkProto.do");
            for (EngineAction action : maps) {
                String id_ = (String) action.getId();
                List<EngineActionInput> actionInputs = instance.queryEngineActionInputIdMatch(connection, id_);
                System.out.println(JSON.toJSONString(action));
                System.out.println(JSON.toJSONString(actionInputs));
                List<EngineActionOutput> actionOutputs = instance.queryEngineActionOutputIdMatch(connection, id_);
                System.out.println(JSON.toJSONString(actionOutputs));
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private EngineAction engineAction;
    private List<EngineActionInput> engineActionInput;
    private List<EngineActionOutput> engineActionOutput;
    @Test
    public void testgui() throws InterruptedException {
        String path = "D:/Code/FMS/FMSS_xfunds/xfunds/WebContent/WEB-INF/lib/orai18n-19.3.0.0.jar;D:/Code/FMS/FMSS_xfunds/xfunds/WebContent/WEB-INF/lib/ojdbc8.jar";


        try (Connection connection = DBUtils.getConnection("xfunds201701", "Xfunds_1234", "jdbc:oracle:thin:@21.96.5.85:1521:FMSS", path)) {
            DBUtils instance = DBUtils.getInstance();
            List<EngineAction> maps = instance.queryEngineActionWithIdLike(connection, "/modifyIfaceStatus.do");
            for (EngineAction action : maps) {
                String id_ = (String) action.getId();
                List<EngineActionInput> actionInputs = instance.queryEngineActionInputIdMatch(connection, id_);
                System.out.println(JSON.toJSONString(action));
                System.out.println(JSON.toJSONString(actionInputs));
                List<EngineActionOutput> actionOutputs = instance.queryEngineActionOutputIdMatch(connection, id_);
                System.out.println(JSON.toJSONString(actionOutputs));
                engineAction=action;
                engineActionInput=actionInputs;
                engineActionOutput=actionOutputs;
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
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
