package cn.boz.jb.plugin.floweditor.gui.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * 比较带字母与数字字符串的顺序
     * @param o1
     * @param o2
     * @return
     */
    public static int compareStringWithNumber(String o1,String o2){
        Pattern pattern = Pattern.compile("(\\w+)(\\d+)");
        Matcher m1 = pattern.matcher(o1);
        Matcher m2 = pattern.matcher(o2);
        if (m1.find() && m2.find()) {
            int i = m1.group(1).compareTo(m2.group(1));
            if (i != 0) {
                return i;
            } else {
                int i1 = m1.group(2).compareTo(m2.group(2));
                return i1;
            }
        }

        return o1.compareTo(o2);
    }
}
