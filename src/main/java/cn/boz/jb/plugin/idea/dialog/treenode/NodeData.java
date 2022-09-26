package cn.boz.jb.plugin.idea.dialog.treenode;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public abstract class NodeData {

    private Object nodeData;

    private boolean subDataLoaded;

    public Object getNodeData() {
        return nodeData;
    }

    public void setNodeData(Object nodeData) {
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
    public abstract List<NodeData> loadSubNodes(Connection connection);
}
