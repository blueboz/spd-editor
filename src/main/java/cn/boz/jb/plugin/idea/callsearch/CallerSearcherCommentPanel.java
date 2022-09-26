package cn.boz.jb.plugin.idea.callsearch;

import cn.boz.jb.plugin.idea.bean.EngineAction;
import cn.boz.jb.plugin.idea.bean.EngineTask;
import cn.boz.jb.plugin.idea.utils.Constants;
import cn.boz.jb.plugin.idea.utils.MyHighlightUtils;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBDimension;
import com.intellij.util.ui.ListTableModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

public class CallerSearcherCommentPanel extends JBPanel {
    private CallerSearcherTablePanel table;
    JTextArea textArea;

    public CallerSearcherCommentPanel(CallerSearcherTablePanel table) {
        this.table = table;
        textArea = new JTextArea("", 7, 30);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(@NotNull ListSelectionEvent e) {
                ListTableModel model = (ListTableModel) table.getModel();
                int selectedRow = table.getSelectedRow();
                int i = table.convertRowIndexToModel(selectedRow);

                if(i==-1){
                    return ;
                }
                Object item = model.getItem(i);
                String hint = "";
                if (item instanceof EngineTask) {
                    hint = ((EngineTask) item).getExpression();
                } else if (item instanceof EngineAction) {
                    hint = ((EngineAction) item).getActionscript();
                }

                textArea.setText(hint);
                MyHighlightUtils.installHighlightForTextArea(table, textArea);


            }
        });
        this.setLayout(new BorderLayout());
        ;
        JScrollPane textScrollPane = ScrollPaneFactory.createScrollPane(textArea);
        JLabel commentLabel = new JLabel("script");
        this.add(commentLabel, "North");
        commentLabel.setBorder(IdeBorderFactory.createBorder(11));
        textScrollPane.setBorder((Border) null);
        this.add(textScrollPane, "Center");
        this.setPreferredSize(new JBDimension(800, 200));

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
        this.add(gotoactionScript, "South");

    }

    public String getScript() {
        return textArea.getText();
    }

    public CallerSearcherTablePanel getTable() {
        return table;
    }

    public void setTable(CallerSearcherTablePanel table) {
        this.table = table;
    }
}
