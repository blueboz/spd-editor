package cn.boz.jb.plugin.idea.toolwindow;

import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.intellij.openapi.project.Project;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MenuNode extends NodeData {
    public MenuNode(Map<String, Object> nodeData) {
        super(nodeData);
    }

    @Override
    public String getTitle() {
        Map<String, Object> nd = (Map<String, Object>) getNodeData();
        return (String) nd.get("NAME");
    }

    /**
     * 获取节点数据
     *
     * @return
     */
    private Map<String, Object> getData() {
        Map<String, Object> nd = (Map<String, Object>) getNodeData();
        return nd;
    }

    @Override
    public List<NodeData> loadSubNodes(Project project) {
        if (isSubDataLoaded()) {
            return new ArrayList<>();
        }
        this.setSubDataLoaded(true);
        DBUtils instance = DBUtils.getInstance();
        List<Map<String, Object>> subMenus;
        try {
            subMenus = instance.queryMenuOfAppMenu(project, (BigDecimal) getData().get("MENUID"), (BigDecimal) getData().get("APPLID"));
            return subMenus.stream().map(it -> new MenuNode(it)).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


    public static MenuNode buildEmptyNode() {
        /**
         [
         {
         "APPLID": 999,
         "MENUID": 661,
         "NAME": "统一额度使用情况",
         "LVL": 1,
         "URL": "riskctl/MarginUsagesStatus.html",
         "PARENT": 5,
         "IMG": "../ui/eraui/images/title_icon.gif",
         "ISCHILD": 1,
         "GROUPID": 0
         }
         ]
         */
        Map<String, Object> emptyNode = new HashMap<>();
        emptyNode.put("APPLID", 9999);
        emptyNode.put("MENUID", 661);
        emptyNode.put("NAME", "统一额度使用情况");
        emptyNode.put("LVL", 1);
        emptyNode.put("URL", "riskctl/MarginUsagesStatus.html");
        emptyNode.put("IMG", "../ui/eraui/images/title_icon.gif");
        emptyNode.put("ISCHILD", 1);
        emptyNode.put("GROUPID", 0);
        return new MenuNode(emptyNode);
    }
}
