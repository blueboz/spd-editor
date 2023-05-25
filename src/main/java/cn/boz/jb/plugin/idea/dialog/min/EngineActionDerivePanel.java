package cn.boz.jb.plugin.idea.dialog.min;

import cn.boz.jb.plugin.idea.bean.EngineAction;
import cn.boz.jb.plugin.idea.bean.EngineActionInput;
import cn.boz.jb.plugin.idea.bean.EngineActionOutput;
import cn.boz.jb.plugin.idea.layoutmanager.MyLayoutManager;
import cn.boz.jb.plugin.idea.utils.Constants;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class EngineActionDerivePanel extends JComponent {
    MyLayoutManager myLayoutManager;

    private String actionScript;

    private String id;
    private EngineAction engineAction;

    public EngineActionDerivePanel(EngineAction engineAction ) {

        this.engineAction=engineAction;
        String id = (String) engineAction.getId();
        String namespace = (String) engineAction.getNamespace();
        String actionscript = (String) engineAction.getActionscript();
        this.id=id;

        myLayoutManager = new MyLayoutManager();

        this.setLayout(myLayoutManager);

        JLabel actionIdLabel = new JLabel("actionId:");
        JTextField actionIdTextField = new JTextField();
        actionIdTextField.setText(id);
        JLabel namespaceLabel = new JLabel("namespace:");
        JTextField namespaceTextField = new JTextField();
        namespaceTextField.setText(namespace);

        JLabel actionscriptLabel = new JLabel("actionScript:");
        JTextArea actionScriptTextArea = new JTextArea("", 7, 30);
        actionScriptTextArea.setText(actionscript);
        actionScriptTextArea.setLineWrap(true);
        this.actionScript=actionscript;


        JBScrollPane actionScriptScrollPane = new JBScrollPane(actionScriptTextArea);

        JLabel actionInputLabel = new JLabel("actionInput:");
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setRowCount(0);    //清空表格中的数据
        tableModel.setColumnIdentifiers(new Object[]{"BEANID", "CLAZZ", "SOURCE"});    //设置表头
        for (EngineActionInput actionInput : engineAction.getInputs()) {
            tableModel.addRow(new Object[]{actionInput.getBeanId(), actionInput.getClass_(), actionInput.getSource()});
        }
        JTable actionInputTable = new JTable(tableModel) {
        };


        JBScrollPane actionInputPanel = new JBScrollPane(actionInputTable);
        actionInputPanel.setPreferredSize(new Dimension(0, 200));

        JLabel actionOutputLabel = new JLabel("actionOutput:");
        DefaultTableModel outputModel = new DefaultTableModel();
        outputModel.setRowCount(0);    //清空表格中的数据
        outputModel.setColumnIdentifiers(new Object[]{"BEANID"});    //设置表头
        for (EngineActionOutput actionInput : engineAction.getOutputs()) {
            outputModel.addRow(new Object[]{actionInput.getBeanId()});
        }
        JTable actionOutputTable = new JTable(outputModel) {
        };
        JBScrollPane actionOutputPanel = new JBScrollPane(actionOutputTable);
        actionOutputPanel.setPreferredSize(new Dimension(0, 200));

        ActionManager instance = ActionManager.getInstance();
        ActionGroup actionGroup = (ActionGroup) instance.getAction(Constants.ACTION_GROUP_REF_ENGINE_ACTION);
        ActionToolbar spd_tb = instance.createActionToolbar("spd tb", actionGroup, true);

        JComponent gotoactionScript = spd_tb.getComponent();


        this.add(actionIdLabel);
        this.add(actionIdTextField);
        this.add(namespaceLabel);
        this.add(namespaceTextField);
        this.add(actionscriptLabel);
        this.add(actionScriptScrollPane);
        this.add(gotoactionScript);
        this.add(actionInputLabel);
        this.add(actionInputPanel);
        this.add(actionOutputLabel);
        this.add(actionOutputPanel);


    }

    public String getActionScript() {
        return actionScript;
    }

    public void setActionScript(String actionScript) {
        this.actionScript = actionScript;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EngineAction getEngineAction() {
        return engineAction;
    }

    public void setEngineAction(EngineAction engineAction) {
        this.engineAction = engineAction;
    }
}
