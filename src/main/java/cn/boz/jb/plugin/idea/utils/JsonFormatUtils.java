package cn.boz.jb.plugin.idea.utils;

public class JsonFormatUtils {

    public static String formatJson(String jsonString) {
        StringBuilder sb = new StringBuilder();
        int indentLevel = 0;
        boolean inQuote = false; // 是否在字符串中
        for (int i = 0; i < jsonString.length(); i++) {
            char ch = jsonString.charAt(i);
            switch (ch) {
                case '{':
                case '[':
                    sb.append(ch);
                    if (!inQuote) {
                        sb.append('\n');
                        indentLevel++;
                        addIndentBlank(sb, indentLevel);
                    }
                    break;
                case '}':
                case ']':
                    if (!inQuote) {
                        sb.append('\n');
                        indentLevel--;
                        addIndentBlank(sb, indentLevel);
                    }
                    sb.append(ch);
                    break;
                case ',':
                    sb.append(ch);
                    if (!inQuote) {
                        sb.append('\n');
                        addIndentBlank(sb, indentLevel);
                    }
                    break;
                case ':':
                    sb.append(ch);
                    if (!inQuote) {
                        sb.append(' ');
                    }
                    break;
                case ' ':
                case '\t':
                case '\r':
                case '\n':
                    if (inQuote) {
                        sb.append(ch);
                    }
                    break;
                case '\"':
                    sb.append(ch);
                    if (!inQuote) {
                        inQuote = true;
                    } else {
                        inQuote = false;
                    }
                    break;
                default:
                    sb.append(ch);
            }
        }
        return sb.toString();
    }

    public static void addIndentBlank(StringBuilder sb, int indentLevel) {
        for (int i = 0; i < indentLevel; i++) {
            sb.append("  "); // 每个缩进级别使用两个空格
        }
    }
}
