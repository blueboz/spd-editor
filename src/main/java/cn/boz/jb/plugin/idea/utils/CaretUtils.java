package cn.boz.jb.plugin.idea.utils;

import cn.boz.jb.plugin.floweditor.gui.utils.StringUtils;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;

public class CaretUtils {

    public static String localString(Editor editor){
        CaretModel caretModel = editor.getCaretModel();
        Document document = editor.getDocument();
        int offset = editor.getCaretModel().getOffset();

        int lineNumber = document.getLineNumber(offset);
        int lineStartOffset = document.getLineStartOffset(lineNumber);
        int lineEndOffset = document.getLineEndOffset(lineNumber);
        int lineOffset = offset - lineStartOffset;
        String selectedLine = document.getText(new TextRange(lineStartOffset, lineEndOffset));
        String res = extractByChar(lineOffset, selectedLine, '"');
        if(!StringUtils.isBlank(res)){
            return res;
        }

        res = extractByChar(lineOffset, selectedLine, '\'');
        if(!StringUtils.isBlank(res)){
            return res;
        }
        return null;



    }

    private  static String extractByChar(int lineOffset, String selectedLine,char ch) {
        if(StringUtils.isBlank(selectedLine)){
            return null;
        }
        int wordstartoffset = lineOffset;
        int wordendoffset = lineOffset;
        while (wordendoffset < selectedLine.length()) {
            if (ch == selectedLine.charAt(wordendoffset)) {
                break;
            }
            wordendoffset++;
        }
        while (wordstartoffset >= 0) {
            if (ch == selectedLine.charAt(wordstartoffset)) {
                break;
            }
            wordstartoffset--;
        }
        if (wordstartoffset >= 0 && wordendoffset <= selectedLine.length()) {
            String stringValue = selectedLine.substring(wordstartoffset + 1, wordendoffset);
            return stringValue;
        }
        return null;
    }

}
