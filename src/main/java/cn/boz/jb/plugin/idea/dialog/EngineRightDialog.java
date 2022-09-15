package cn.boz.jb.plugin.idea.dialog;

import cn.boz.jb.plugin.floweditor.gui.process.fragment.UserTask;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class EngineRightDialog extends JComponent {

    private String candidate;
    private String sqlCondition;
    private String doCondition;

    JLabel candidateLabel;
    JLabel sqlConditionLabel;
    JLabel doConditionLabel;

    JTextArea candidateTextArea;
    JTextArea sqlConditionTextArea;
    JTextArea doConditionTextArea;

    JBScrollPane candidateJscroll;
    JBScrollPane sqlConditionJscroll;
    JBScrollPane doConditionJscroll;
    private UserTask userTask;
    private String processId;
    public EngineRightDialog(String candidate, String sqlCondition, String doCondition,String processId, UserTask userTask, boolean installOpenInToolWindowBtn) {

        this.processId=processId;
        this.candidate=candidate;
        this.sqlCondition=sqlCondition;
        this.doCondition=doCondition;
        this.userTask=userTask;

        this.setLayout(new MyLayoutManager());


        candidateLabel = new JLabel("candidate");
        candidateTextArea = new JTextArea("", 7, 30);
        candidateTextArea.setText(candidate);
        candidateTextArea.setLineWrap(true);
        candidateTextArea.setAutoscrolls(true);

        sqlConditionLabel = new JLabel("sqlCondition");
        sqlConditionTextArea = new JTextArea("", 7, 30);
        sqlConditionTextArea.setText(sqlCondition);
        sqlConditionTextArea.setLineWrap(true);
        sqlConditionTextArea.setAutoscrolls(true);

        doConditionLabel = new JLabel("docondition");
        doConditionTextArea = new JTextArea("", 7, 30);
        doConditionTextArea.setText(doCondition);
        doConditionTextArea.setLineWrap(true);
        doConditionTextArea.setAutoscrolls(true);


        candidateJscroll = new JBScrollPane(candidateTextArea);
        sqlConditionJscroll = new JBScrollPane(sqlConditionTextArea);
        doConditionJscroll = new JBScrollPane(doConditionTextArea);


        this.add(candidateLabel);
        this.add(candidateJscroll);
        this.add(sqlConditionLabel);
        this.add(sqlConditionJscroll);
        this.add(doConditionLabel);
        this.add(doConditionJscroll);

        installOpenInToolWindowBtn();
        this.setFocusable(true);
    }

    public void installOpenInToolWindowBtn(){
        ActionManager instance = ActionManager.getInstance();
        ActionGroup actionGroup = (ActionGroup) instance.getAction("spd.engineright.dlg.group");
        ActionToolbar spd_tb = instance.createActionToolbar("spdtb", actionGroup, true);
        JComponent gotoactionScript = spd_tb.getComponent();

        this.add(gotoactionScript) ;
    }

    public JComponent derive() {
        JBTextField candidateTextArea = new JBTextField();
        candidateTextArea.setText(this.candidate);

        JBTextField sqlConditionTextArea = new JBTextField();
        sqlConditionTextArea.setText(this.sqlCondition);

        JBTextField doConditionTextArea = new JBTextField();
        doConditionTextArea.setText(this.doCondition);
                UserTask userTask = this.getUserTask();
        String rights = userTask.getRights();
        String id = userTask.getId();
        String name = userTask.getName();

        String titleStr =String.format("%s-%s(%s)-%s",this.getProcessId(),id,name,rights);
        JPanel panel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("route:"), new JBTextField(titleStr), 1, true)
                .addLabeledComponent(new JBLabel("candidate:"), candidateTextArea, 1, true)
                .addLabeledComponent(new JBLabel("sqlCondition:"), sqlConditionTextArea, 1, true)
                .addLabeledComponent(new JBLabel("doCondition:"), doConditionTextArea, 1, true)
                .setHorizontalGap(4)
                .setVerticalGap(4)
                .addComponentFillVertically(new JPanel(), 5)
                .getPanel();
        panel.setBorder(IdeBorderFactory.createBorder(10));
        return panel;
    }

    public UserTask getUserTask() {
        return userTask;
    }

    public String getProcessId() {
        return processId;
    }
}
