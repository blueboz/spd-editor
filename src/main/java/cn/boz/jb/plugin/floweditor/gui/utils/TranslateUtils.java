package cn.boz.jb.plugin.floweditor.gui.utils;

/**
 * 翻译工具
 * 用于从xml字符串与正常字符串之间的转换
 */
public class TranslateUtils {

    /**
     * 转化为xml格式的字符串
     *
     * @param rawString
     * @return
     */
    public static String translateToXmlString(String rawString) {
        if(rawString==null){
            return "";
        }
        rawString = rawString.replace("\n", "#LEY#");
        return rawString;
    }

    /**
     * 将xml字符串转化为正常的字符串
     *
     * @param xmlString
     * @return
     */
    public static String translateFromXmlString(String xmlString) {
        //仅仅只需要处理
        xmlString = xmlString.replace("#LEY#", "\n");
        return xmlString;
    }

    /**
     * 转换成为sql语句
     * @param rawSql
     * @return
     */
    public static String translateToSql(String rawSql){
        if(rawSql==null){
            return "";
        }
        rawSql = rawSql.replace("'", "''");
        return rawSql;
    }
}
