package cn.boz.test;

public class NodeData {

    private String nodeType;

    private Object nodeData;

    private boolean subDataLoaded;

    public NodeData(String nodeType, Object nodeData) {
        this.nodeType = nodeType;
        this.nodeData = nodeData;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

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
}
