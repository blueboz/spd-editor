package cn.boz.jb.plugin.idea.action.flowSearch;

import cn.boz.jb.plugin.floweditor.gui.process.fragment.CallActivity;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ForeachGateway;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.SequenceFlow;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ServiceTask;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.UserTask;
import cn.boz.jb.plugin.floweditor.gui.shape.Label;
import cn.boz.jb.plugin.idea.utils.Constants;
import cn.boz.jb.plugin.idea.utils.ScriptFormatter;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.util.TextRange;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.speedSearch.SpeedSearchSupply;
import com.intellij.util.ui.JBDimension;
import com.intellij.util.ui.ListTableModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;

public class FlowSearchCommentPanel extends JPanel {

    public FlowSearchCommentPanel(FlowSearchTable table){
        JTextArea textArea = new JTextArea();

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(@NotNull ListSelectionEvent e) {

                ListTableModel model = (ListTableModel) table.getModel();
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    return;
                }
                int i = table.convertRowIndexToModel(selectedRow);
                Object item = model.getItem(i);
                String hint = "";
                if (item instanceof ServiceTask) {
                    hint = ((ServiceTask) item).getExpression();
                    hint=ScriptFormatter.format(hint);
                } else if (item instanceof UserTask) {
                    hint = ((UserTask) item).getExpression();
                } else if (item instanceof SequenceFlow) {
                    hint = ((SequenceFlow) item).getConditionExpression();

                } else if (item instanceof CallActivity) {
                    hint = ((CallActivity) item).getCalledElement();

                } else if (item instanceof ForeachGateway) {
                    hint = ((ForeachGateway) item).getExpression();
                } else if (item instanceof Label) {
                    hint = ((Label) item).getText();
                }


                textArea.setText(hint);
                Highlighter highlighter = textArea.getHighlighter();
                highlighter.removeAllHighlights();

                SpeedSearchSupply speedSearch = SpeedSearchSupply.getSupply(table);
                if (speedSearch == null) {
                    return;
                }
                //底层匹配的时候，可能使用的是最大公共子串的算法
                Iterable<TextRange> textRanges = speedSearch.matchingFragments(hint);
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
        });
        this.setLayout(new BorderLayout());
        JScrollPane textScrollPane = ScrollPaneFactory.createScrollPane(textArea);
        JLabel commentLabel = new JLabel("script");
        this.add(commentLabel, "North");
        commentLabel.setBorder(IdeBorderFactory.createBorder(11));
        textScrollPane.setBorder((Border) null);
        this.add(textScrollPane, "Center");
        this.setPreferredSize(new JBDimension(800, 200));


        ActionManager instance = ActionManager.getInstance();
        ActionGroup actionGroup = (ActionGroup) instance.getAction(Constants.ACTION_GROUP_FLOW_SEARCH_MIN);
        ActionToolbar spd_tb = instance.createActionToolbar("spd tb", actionGroup, true);
        JComponent gotoactionScript = spd_tb.getComponent();
        this.add(gotoactionScript, "South");
    }

}
