package cn.boz.jb.plugin.idea.callsearch;

import cn.boz.jb.plugin.floweditor.gui.utils.StringUtils;
import cn.boz.jb.plugin.idea.bean.EngineAction;
import cn.boz.jb.plugin.idea.bean.EngineTask;
import cn.boz.jb.plugin.idea.layoutmanager.MyLayoutManager;
import cn.boz.jb.plugin.idea.utils.Constants;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.table.JBTable;
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

public class CallerSearcherDetailComment extends JBPanel {
    private CallerSearcherTablePanel table;
    JTextArea textArea;
    JTextField jpidf;
    JTextField taskidf;

    public void query() {
    }

    public CallerSearcherDetailComment(CallerSearcherTablePanel table, String queryName) {
        this.table = table;
        this.setLayout(new MyLayoutManager());
        JLabel jpidl = new JLabel("processId");
        jpidf = new JTextField();


        this.add(jpidl);
        this.add(jpidf);
        JLabel taskidl = new JLabel("taskid");
        taskidf = new JTextField();

        this.add(taskidl);
        this.add(taskidf);


        textArea = new JTextArea("", 12, 28);


        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(@NotNull ListSelectionEvent e) {
                ListTableModel model = (ListTableModel) table.getModel();
                int selectedRow = table.getSelectedRow();
                int i = table.convertRowIndexToModel(selectedRow);

                if (i == -1) {
                    return;
                }
                Object item = model.getItem(i);
                String hint = "";
                if (item instanceof EngineTask) {
                    hint = ((EngineTask) item).getExpression();
                    String id = ((EngineTask) item).getId();
                    if (id.contains("_")) {
                        String[] s = id.split("_");
                        jpidf.setText(s[0]);
                        taskidf.setText(s[1]);
                    }
                } else if (item instanceof EngineAction) {
                    hint = ((EngineAction) item).getActionscript();
                    String id = ((EngineAction) item).getId();
                    if (id.contains("_")) {
                        String[] s = id.split("_");
                        jpidf.setText(s[0]);
                        taskidf.setText(s[1]);
                    }else{
                        jpidf.setText("");
                        taskidf.setText("");

                    }
                }

                textArea.setText(hint);
                DefaultHighlighter.DefaultHighlightPainter defaultHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.ORANGE);
                Highlighter highlighter = textArea.getHighlighter();
                if (!StringUtils.isBlank(queryName)) {
                    int queryNameidx = hint.indexOf(queryName);
                    if (queryNameidx != -1) {
                        highlighter.removeAllHighlights();
                        try {
                            highlighter.addHighlight(queryNameidx, queryNameidx + queryName.length(), defaultHighlightPainter);
                        } catch (BadLocationException badLocationException) {
                            badLocationException.printStackTrace();
                        }
                    }
                }

//                MyHighlightUtils.installHighlightForTextArea(table, textArea);


            }
        });
        JScrollPane textScrollPane = ScrollPaneFactory.createScrollPane(textArea);
        JLabel commentLabel = new JLabel("script");
        this.add(commentLabel);
        commentLabel.setBorder(IdeBorderFactory.createBorder(11));
        textScrollPane.setBorder((Border) null);
        this.add(textScrollPane);
//        this.setPreferredSize(new JBDimension(800, 200));

        //增加应用
        ActionManager instance = ActionManager.getInstance();
        ActionGroup actionGroup = (ActionGroup) instance.getAction(Constants.ACTION_GROUP_REF_ENGINE_ACTION);
        //建议将结果打开再ToolWindow中供方便搜索
//        new ActionGroup(){
//
//            @Override
//            public AnAction @NotNull [] getChildren(@Nullable AnActionEvent anActionEvent) {
//                return new AnAction[0];
//            }
//        };

        ActionToolbar spd_tb = instance.createActionToolbar("spd tb", actionGroup, true);
        JComponent gotoactionScript = spd_tb.getComponent();
        this.add(gotoactionScript);

    }

    public String getScript() {
        return textArea.getText();
    }

    public CallerSearcherTablePanel getTable() {
        return table;
    }
}

