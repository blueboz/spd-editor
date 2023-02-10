package cn.boz.jb.plugin.idea.configurable;

import cn.boz.jb.plugin.floweditor.gui.utils.StringUtils;

public class DBInfo {

    public String showName = "";
    public String id = "";

    public String jdbcUrl = "";

    public String jdbcUserName = "";

    public String jdbcPassword = "";

    public String jdbcDriver = "";

    @Override
    public String toString() {
        if (StringUtils.isBlank(showName)) {
            return "未录入";
        }
        return showName;
    }
}
