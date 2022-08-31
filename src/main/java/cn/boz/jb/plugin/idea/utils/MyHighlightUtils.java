package cn.boz.jb.plugin.idea.utils;

import com.intellij.openapi.util.TextRange;
import com.intellij.ui.speedSearch.SpeedSearchSupply;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;

public class MyHighlightUtils {

    public static void installHighlightForTextArea(JComponent component, JTextArea textArea) {
        String text = textArea.getText();
        Highlighter highlighter = textArea.getHighlighter();
        highlighter.removeAllHighlights();

        SpeedSearchSupply speedSearch = SpeedSearchSupply.getSupply(component);
        if (speedSearch == null) {
            return;
        }
        //底层匹配的时候，可能使用的是最大公共子串的算法
        Iterable<TextRange> textRanges = speedSearch.matchingFragments(text);
        if (textRanges == null) {
            return;
        }

        DefaultHighlighter.DefaultHighlightPainter defaultHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.ORANGE);

        for (TextRange textRange : textRanges) {
            try {
                highlighter.addHighlight(textRange.getStartOffset(), textRange.getEndOffset(), defaultHighlightPainter);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }

    }
}
