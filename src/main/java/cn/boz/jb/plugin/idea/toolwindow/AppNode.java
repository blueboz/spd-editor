package cn.boz.jb.plugin.idea.toolwindow;

import cn.boz.jb.plugin.idea.utils.DBUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AppNode extends NodeData {
    public AppNode(Map<String, Object> nodeData) {
        super(nodeData);
    }

    public static List<NodeData> initLoad(Connection connection) {
        try {
            DBUtils instance = DBUtils.getInstance();
            List<Map<String, Object>> apps = instance.queryEcasAppl(connection);
            return apps.stream().map(app -> new AppNode(app)).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public String getTitle() {
        return (String) getNodeData().get("CODE");
    }

    @Override
    public List<NodeData> loadSubNodes(Connection connection) {
        if (isSubDataLoaded()) {
            return new ArrayList<>();
        }
        this.setSubDataLoaded(true);
        DBUtils instance = DBUtils.getInstance();
        List<Map<String, Object>> topMenus = null;
        try {
            topMenus = instance.queryTopMenuOfApp(connection, (BigDecimal) getNodeData().get("APPLID"));
            return topMenus.stream().map(it -> new MenuNode(it)).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
