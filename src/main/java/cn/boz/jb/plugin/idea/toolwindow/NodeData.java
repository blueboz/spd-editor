package cn.boz.jb.plugin.idea.toolwindow;

import com.intellij.openapi.project.Project;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public abstract class NodeData {


    private Map<String, Object> nodeData;

    private boolean subDataLoaded;

    public NodeData(Map<String, Object> nodeData) {
        this.nodeData = nodeData;
    }

    public Map<String, Object> getNodeData() {
        return nodeData;
    }

    public void setNodeData(Map<String, Object> nodeData) {
        this.nodeData = nodeData;
    }

    public boolean isSubDataLoaded() {
        return subDataLoaded;
    }

    public void setSubDataLoaded(boolean subDataLoaded) {
        this.subDataLoaded = subDataLoaded;
    }

    public abstract String getTitle();

    /**
     * 加载子节点数据
     *
     * @param connection 数据库连接对象
     * @return
     */
    public abstract List<NodeData> loadSubNodes(Project project);
}
