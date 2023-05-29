package cn.boz.jb.plugin.idea.dialog.min;

import cn.boz.jb.plugin.floweditor.gui.process.fragment.UserTask;
import cn.boz.jb.plugin.idea.layoutmanager.MyLayoutManager;
import cn.boz.jb.plugin.idea.utils.Constants;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;

public class EngineRightDerivePanel extends JComponent {

    private String candidate;
    private String sqlCondition;
    private String doCondition;

    JLabel candidateLabel;
    JLabel sqlConditionLabel;
    JLabel doConditionLabel;
    JLabel routeLabel;

    JTextArea candidateTextArea;
    JTextArea sqlConditionTextArea;
    JTextArea doConditionTextArea;
    JTextArea routeTextArea;

    JBScrollPane candidateJscroll;
    JBScrollPane sqlConditionJscroll;
    JBScrollPane doConditionJscroll;
    private UserTask userTask;
    private String processId;

    public EngineRightDerivePanel(String candidate, String sqlCondition, String doCondition, String processId, UserTask userTask) {

        this.processId = processId;
        this.candidate = candidate;
        this.sqlCondition = sqlCondition;
        this.doCondition = doCondition;
        this.userTask = userTask;

        this.setLayout(new MyLayoutManager());
        String rights = userTask.getRights();
        String id = userTask.getId();
        String name = userTask.getName();
        String titleStr = String.format("%s->%s(%s)->%s", this.getProcessId(), id, name, rights);

        routeLabel = new JLabel("route");
        routeTextArea = new JTextArea("", 2, 30);
        routeTextArea.setText(titleStr);
        routeTextArea.setLineWrap(true);
        routeTextArea.setAutoscrolls(true);

        candidateLabel = new JLabel("candidate");
        candidateTextArea = new JTextArea("", 7, 30);
        candidateTextArea.setText(candidate);
        candidateTextArea.setLineWrap(true);
        candidateTextArea.setAutoscrolls(true);


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

        this.add(routeLabel);
        this.add(routeTextArea);
        this.add(candidateLabel);
        this.add(candidateJscroll);
        this.add(sqlConditionLabel);
        this.add(sqlConditionJscroll);
        this.add(doConditionLabel);
        this.add(doConditionJscroll);

        this.setFocusable(true);
        installOpenInToolWindowBtn();
    }

    public void installOpenInToolWindowBtn() {
        ActionManager instance = ActionManager.getInstance();
        ActionGroup actionGroup = (ActionGroup) instance.getAction(Constants.ACTION_GROUP_FLOW_ENGINE_RIGHTS);
        ActionToolbar spd_tb = instance.createActionToolbar("spdtb", actionGroup, true);
        JComponent gotoactionScript = spd_tb.getComponent();
        this.add(gotoactionScript);
    }

    public UserTask getUserTask() {
        return userTask;
    }

    public String getProcessId() {
        return processId;
    }
}
