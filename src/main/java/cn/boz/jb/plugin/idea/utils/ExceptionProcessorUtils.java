package cn.boz.jb.plugin.idea.utils;

import cn.boz.jb.plugin.idea.configurable.SpdEditorNormSettings;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import icons.SpdEditorIcons;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionProcessorUtils {
    private static final ExceptionProcessorUtils INST = new ExceptionProcessorUtils();

    private ExceptionProcessorUtils() {
        //应该怎么初始化，作为一个Service?
    }

    public static ExceptionProcessorUtils getInstance() {
        return INST;
    }

    public static String generateRecrusiveException(Throwable e) {
        if (e == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        String msg = stringWriter.toString();
        String[] split = msg.split("\n");
        for (String s : split) {
            if (s.contains("at")) {

                if (s.contains("cn.boz")) {
                    sb.append(s);
                }
            } else {
                sb.append(s);
            }

        }
        return sb.toString();
    }

    public static void exceptionProcessor(Throwable ee, Project project, String prefix) {
        String errMsg = generateRecrusiveException(ee);

        int idx = Messages.showDialog( errMsg, "Oops!something wrong happen!", new String[]{"Check mockbase Config", "I don't care!"}, 0, SpdEditorIcons.FLOW_16_ICON);
        if (idx == 0) {
            ShowSettingsUtil.getInstance().showSettingsDialog(project, SpdEditorNormSettings.class);
        }
    }

}
