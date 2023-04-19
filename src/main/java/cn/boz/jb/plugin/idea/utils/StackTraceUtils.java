package cn.boz.jb.plugin.idea.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTraceUtils {
    public static String generateRecusiveException(Exception e) {
        if (e == null) {
            return "";
        }
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        return stringWriter.toString();

    }
}
