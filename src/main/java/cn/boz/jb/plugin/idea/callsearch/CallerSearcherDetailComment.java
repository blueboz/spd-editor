package cn.boz.jb.plugin.idea.callsearch;

import cn.boz.jb.plugin.idea.bean.EngineAction;
import cn.boz.jb.plugin.idea.bean.EngineTask;
import cn.boz.jb.plugin.idea.dialog.MyLayoutManager;
import cn.boz.jb.plugin.idea.utils.MyHighlightUtils;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManager;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManagerImpl;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.AnActionButton;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;

public class CallerSearcherDetailComment extends JBPanel {
    private JBTable table;
    JTextArea textArea;
    JTextField jpidf;
    JTextField taskidf;

    public CallerSearcherDetailComment(JBTable table) {
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
                    String id = ((EngineTask) item).getId();
                    if(id.contains("_")){
                        String[] s = id.split("_");
                        jpidf.setText(s[0]);
                        taskidf.setText(s[1]);
                    }
                } else if (item instanceof EngineAction) {
                    hint = ((EngineAction) item).getActionscript();
                    String id = ((EngineTask) item).getId();
                    if(id.contains("_")){
                        String[] s = id.split("_");
                        jpidf.setText(s[0]);
                        taskidf.setText(s[1]);
                    }
                }

                textArea.setText(hint);
                MyHighlightUtils.installHighlightForTextArea(table, textArea);


            }
        });
        JScrollPane textScrollPane = ScrollPaneFactory.createScrollPane(textArea);
        JLabel commentLabel = new JLabel("script");
        this.add(commentLabel);
        commentLabel.setBorder(IdeBorderFactory.createBorder(11));
        textScrollPane.setBorder((Border) null);
        this.add(textScrollPane );
        this.setPreferredSize(new JBDimension(800, 200));

        //增加应用
        ActionManager instance = ActionManager.getInstance();
        ActionGroup actionGroup = (ActionGroup) instance.getAction("spd.engineaction.dlg.group");
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
}
