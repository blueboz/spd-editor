package cn.boz.jb.plugin.idea.utils;

public class ScriptFormatter {

    public static String format(String script) {
        if (script == null) {
            return "";
        }

        if (!script.contains("\n")) {
            script = script.replaceAll(";", ";\n");
            return script;
        } else {
            return script;
        }
    }
}
