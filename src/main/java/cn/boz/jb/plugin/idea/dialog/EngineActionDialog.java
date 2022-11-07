package cn.boz.jb.plugin.idea.dialog;

import cn.boz.jb.plugin.idea.bean.EngineAction;
import cn.boz.jb.plugin.idea.bean.EngineActionInput;
import cn.boz.jb.plugin.idea.bean.EngineActionOutput;
import cn.boz.jb.plugin.idea.dialog.min.EngineActionDerivePanel;
import cn.boz.jb.plugin.idea.layoutmanager.MyLayoutManager;
import cn.boz.jb.plugin.idea.utils.Constants;
import cn.boz.jb.plugin.idea.utils.ScriptFormatter;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class EngineActionDialog extends JComponent {

    private EngineAction engineAction;
;

    public EngineAction getEngineAction() {
        return engineAction;
    }

    public void setEngineAction(EngineAction engineAction) {
        this.engineAction = engineAction;
    }


    private JLabel actionIdLabel;
    private JLabel namespaceLabel;
    private JLabel actionscriptLabel;

    private JTextField actionIdTextField;
    private JTextField namespaceTextField;

    private JTextArea actionScriptTextArea;
    private JScrollPane actionScriptScrollPane;
    private JLabel actionInputLabel;
    private JTable actionInputTable;
    private JScrollPane actionInputPanel;

    private MyLayoutManager myLayoutManager;

    private JScrollPane actionOutputPanel;
    private JTable actionOutputTable;
    private JLabel actionOutputLabel;
    private DefaultTableModel outputModel;
    private String id;
    private String actionScript;

    public EngineActionDialog(EngineAction engineAction ) {
        this.engineAction = engineAction;
        String id =  engineAction.getId();
        this.id=id;
        String namespace =  engineAction.getNamespace();
        String actionscript =  engineAction.getActionscript();
        this.actionScript=actionscript;
        actionscript= ScriptFormatter.format(actionscript);


        myLayoutManager = new MyLayoutManager();

        this.setLayout(myLayoutManager);
        actionIdLabel = new JLabel("actionId:");
        actionIdTextField = new JTextField();
        actionIdTextField.setText(id);
        namespaceLabel = new JLabel("namespace:");
        namespaceTextField = new JTextField();
        namespaceTextField.setText(namespace);

        actionscriptLabel = new JLabel("actionScript:");
        actionScriptTextArea = new JTextArea("", 7, 30);
        actionScriptTextArea.setText(actionscript);
        actionScriptTextArea.setLineWrap(true);


        actionScriptScrollPane = new JBScrollPane(actionScriptTextArea);

        actionInputLabel = new JLabel("actionInput:");
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setRowCount(0);    //清空表格中的数据
        tableModel.setColumnIdentifiers(new Object[]{"BEANID", "CLAZZ", "SOURCE"});    //设置表头
        for (EngineActionInput actionInput : engineAction.getInputs()) {
            tableModel.addRow(new Object[]{actionInput.getBeanId(), actionInput.getClass_(), actionInput.getSource()});
        }
        actionInputTable = new JTable(tableModel) {
        };

//        ProjectManager projectManager = ProjectManager.getInstance();
//        Project defaultProject = projectManager.getDefaultProject();


        actionInputPanel = new JBScrollPane(actionInputTable);
        actionInputPanel.setPreferredSize(new Dimension(0, 200));

        actionOutputLabel = new JLabel("actionOutput:");
        outputModel = new DefaultTableModel();
        outputModel.setRowCount(0);    //清空表格中的数据
        outputModel.setColumnIdentifiers(new Object[]{"BEANID"});    //设置表头
        for (EngineActionOutput actionInput : engineAction.getOutputs()) {
            outputModel.addRow(new Object[]{actionInput.getBeanId()});
        }
        actionOutputTable = new JTable(outputModel) {
        };
        actionOutputPanel = new JBScrollPane(actionOutputTable);
        actionOutputPanel.setPreferredSize(new Dimension(0, 200));

        ActionManager instance = ActionManager.getInstance();
        ActionGroup actionGroup = (ActionGroup) instance.getAction(Constants.ACTION_GROUP_REF_ENGINE_ACTION_MIN);
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


        this.setFocusable(true);
    }

    public EngineActionDerivePanel derive(){
        return new EngineActionDerivePanel(engineAction);
    }


    public String getActionScript() {
        return actionScript;
    }

    public String getId() {
        return id;
    }
}
