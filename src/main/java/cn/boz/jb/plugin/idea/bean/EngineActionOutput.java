package cn.boz.jb.plugin.idea.bean;

public class EngineActionOutput {
    private String actionId;
    private String beanId;
    private String fieldExpr;
    private String target;

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getBeanId() {
        return beanId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    public String getFieldExpr() {
        return fieldExpr;
    }

    public void setFieldExpr(String fieldExpr) {
        this.fieldExpr = fieldExpr;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
