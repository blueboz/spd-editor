package cn.boz.jb.plugin.idea.bean;


public class EngineFlow {

    private String processid;
    private String source;
    private String target;
    private String condition;
    private String order;


    public String getProcessid() {
        return processid;
    }

    public void setProcessid(String processid) {
        this.processid = processid;
    }


    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }


    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }


    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }


    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String toCsv(boolean wrap) {
        return "flow" +
                " " + processid +
                " " + source +
                " " + target +
                " " + (condition == null ? "" : (wrap ? condition : condition.replaceAll("\n", ""))) +
                " " + order;

    }

    @Override
    public String toString() {
        return "EngineFlow{" +
                "processid='" + processid + '\'' +
                ", source='" + source + '\'' +
                ", target='" + target + '\'' +
                ", condition='" + condition + '\'' +
                ", order='" + order + '\'' +
                '}';
    }
}
