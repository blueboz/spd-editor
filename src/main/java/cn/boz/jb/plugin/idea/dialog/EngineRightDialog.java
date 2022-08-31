package cn.boz.jb.plugin.idea.dialog;

import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;

public class EngineRightDialog extends JComponent {

    private MyLayoutManager myLayoutManager;

    public EngineRightDialog(String candidate, String sqlCondition, String doCondition) {

        myLayoutManager = new MyLayoutManager();

        this.setLayout(myLayoutManager);

        JLabel candidateLabel = new JLabel("candidate");
        JTextArea candidateTextArea = new JTextArea("", 7, 30);
        candidateTextArea.setText(candidate);

        JLabel sqlConditionLabel = new JLabel("sqlCondition");
        JTextArea sqlConditionTextArea = new JTextArea("", 7, 30);
        sqlConditionTextArea.setText(sqlCondition);

        JLabel doConditionLabel = new JLabel("docondition");
        JTextArea doConditionTextArea = new JTextArea("", 7, 30);
        doConditionTextArea.setText(doCondition);
        doConditionTextArea.setLineWrap(true);

        JBScrollPane candidateJscroll = new JBScrollPane(candidateTextArea);
        JBScrollPane sqlConditionJscroll = new JBScrollPane(sqlConditionTextArea);
        JBScrollPane doConditionJscroll = new JBScrollPane(doConditionTextArea);


        this.add(candidateLabel);
        this.add(candidateJscroll);
        this.add(sqlConditionLabel);
        this.add(sqlConditionJscroll);
        this.add(doConditionLabel);
        this.add(doConditionJscroll);

        this.setFocusable(true);
    }

}
