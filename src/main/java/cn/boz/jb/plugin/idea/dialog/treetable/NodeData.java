package cn.boz.jb.plugin.idea.dialog.treetable;

import java.sql.Connection;
import java.util.List;

public abstract class NodeData {
    private boolean loading=false;

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

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }
}
