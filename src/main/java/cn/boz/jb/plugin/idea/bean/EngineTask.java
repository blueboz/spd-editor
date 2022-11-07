package cn.boz.jb.plugin.idea.bean;


import cn.boz.jb.plugin.idea.utils.ScriptFormatter;

public class EngineTask {
    private boolean checked;

    private String id;
    private String type;
    private String title;
    private String expression;
    private String returnvalue;
    private String bussineskey;
    private String bussinesdesc;
    private String rights;
    private String validsecond;
    private String listener;
    private String opensecond;
    private String bussinesid;
    private String tasklistener;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExpressionRaw(){
        return expression;
    }

    public String getExpression() {
        if (expression == null) {
            return "";
        }
        return ScriptFormatter.format(expression);
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }


    public String getReturnvalue() {
        return returnvalue;
    }

    public void setReturnvalue(String returnvalue) {
        this.returnvalue = returnvalue;
    }


    public String getBussineskey() {
        return bussineskey;
    }

    public void setBussineskey(String bussineskey) {
        this.bussineskey = bussineskey;
    }


    public String getBussinesdesc() {
        return bussinesdesc;
    }

    public void setBussinesdesc(String bussinesdesc) {
        this.bussinesdesc = bussinesdesc;
    }


    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }


    public String getValidsecond() {
        return validsecond;
    }

    public void setValidsecond(String validsecond) {
        this.validsecond = validsecond;
    }


    public String getListener() {
        return listener;
    }

    public void setListener(String listener) {
        this.listener = listener;
    }


    public String getOpensecond() {
        return opensecond;
    }

    public void setOpensecond(String opensecond) {
        this.opensecond = opensecond;
    }


    public String getBussinesid() {
        return bussinesid;
    }

    public void setBussinesid(String bussinesid) {
        this.bussinesid = bussinesid;
    }


    public String getTasklistener() {
        return tasklistener;
    }

    public void setTasklistener(String tasklistener) {
        this.tasklistener = tasklistener;
    }

    public String toCsv(boolean wrap) {
        return "task" +
                " " + id +
                " " + type +
                " " + title +
                " " + (expression == null ? "" : (wrap ? expression : expression.replaceAll("\n", ""))) +
                " " + returnvalue +
                " " + bussineskey +
                " " + bussinesdesc +
                " " + rights +
                " " + validsecond +
                " " + listener +
                " " + opensecond +
                " " + bussinesid +
                " " + tasklistener;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
