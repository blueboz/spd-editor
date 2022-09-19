package cn.boz.jb.plugin.idea.dialog;

import cn.boz.jb.plugin.floweditor.gui.process.fragment.UserTask;
import cn.boz.jb.plugin.idea.dialog.min.EngineRightDerivePanel;
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
        ActionGroup actionGroup = (ActionGroup) instance.getAction(Constants.ACTION_GROUP_FLOW_ENGINE_RIGHTS);
        ActionToolbar spd_tb = instance.createActionToolbar("spdtb", actionGroup, true);
        JComponent gotoactionScript = spd_tb.getComponent();
        this.add(gotoactionScript) ;
    }

    public JComponent derive() {
      return new EngineRightDerivePanel(candidate,sqlCondition,doCondition,processId,userTask);
    }

    public UserTask getUserTask() {
        return userTask;
    }

    public String getProcessId() {
        return processId;
    }
}
