package cn.boz.jb.plugin.floweditor.gui.utils;

public class StringUtils {

    /**
     * 判断字符串是否为空
     * @param str
     * @return
     */
    public static boolean isBlank(String str){
        if(str==null||"".equals(str.trim())){
            return true;
        }
        return false;
    }
}
