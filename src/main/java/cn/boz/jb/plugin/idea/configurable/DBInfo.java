package cn.boz.jb.plugin.idea.configurable;

import cn.boz.jb.plugin.floweditor.gui.utils.StringUtils;

public class DBInfo {

    public String showName = "";
    public String id = "";

    public String jdbcUrl = "jdbc:oracle:thin:@21.96.5.85:1521:FMSS";

    public String jdbcUserName = "xfunds201701";

    public String jdbcPassword = "Xfunds_1234";

    public String jdbcDriver = "";

    @Override
    public String toString() {
        if (StringUtils.isBlank(showName)) {
            return "未录入";
        }
        return showName;
    }
}
