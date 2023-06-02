package cn.boz.jb.plugin.idea.utils;

public class MyStringUtils {

    public static String firstCharUpper(String str){
        return str.substring(0,1).toUpperCase()+str.substring(1);
    }
    public static String firstCharLower(String str){
        return str.substring(0,1).toLowerCase()+str.substring(1);
    }

}
