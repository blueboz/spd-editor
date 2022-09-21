package cn.boz.jb.plugin.idea.bean;

public class EngineActionInput {

    private String actionId;
    private String beanId;
    private String class_;
    private String fieldExpr;
    private String source;

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

    public String getClass_() {
        return class_;
    }

    public void setClass_(String class_) {
        this.class_ = class_;
    }

    public String getFieldExpr() {
        return fieldExpr;
    }

    public void setFieldExpr(String fieldExpr) {
        this.fieldExpr = fieldExpr;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}

