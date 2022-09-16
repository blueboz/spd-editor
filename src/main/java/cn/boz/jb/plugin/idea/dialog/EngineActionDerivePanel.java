package cn.boz.jb.plugin.idea.dialog;

import cn.boz.jb.plugin.idea.utils.Constants;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class EngineActionDerivePanel extends JPanel {
    MyLayoutManager myLayoutManager;

    public EngineActionDerivePanel( Map<String, Object> engineAction,
                                   List<Map<String, Object>> engineActionInput, List<Map<String, Object>> engineActionOutput) {
        JPanel panel = new JPanel();
        String id = (String) engineAction.get("ID_");
        String namespace = (String) engineAction.get("NAMESPACE_");
        String actionscript = (String) engineAction.get("ACTIONSCRIPT_");

        ;


        myLayoutManager = new MyLayoutManager();

        panel.setLayout(myLayoutManager);

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


        JBScrollPane actionScriptScrollPane = new JBScrollPane(actionScriptTextArea);

        JLabel actionInputLabel = new JLabel("actionInput:");
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setRowCount(0);    //清空表格中的数据
        tableModel.setColumnIdentifiers(new Object[]{"BEANID", "CLAZZ", "SOURCE"});    //设置表头
        for (Map<String, Object> actionInput : engineActionInput) {
            tableModel.addRow(new Object[]{actionInput.get("BEANID_"), actionInput.get("CLAZZ_"), actionInput.get("SOURCE_")});
        }
        JTable actionInputTable = new JTable(tableModel) {
        };


        JBScrollPane actionInputPanel = new JBScrollPane(actionInputTable);
        actionInputPanel.setPreferredSize(new Dimension(0, 200));

        JLabel actionOutputLabel = new JLabel("actionOutput:");
        DefaultTableModel outputModel = new DefaultTableModel();
        outputModel.setRowCount(0);    //清空表格中的数据
        outputModel.setColumnIdentifiers(new Object[]{"BEANID"});    //设置表头
        for (Map<String, Object> actionInput : engineActionOutput) {
            outputModel.addRow(new Object[]{actionInput.get("BEANID_")});
        }
        JTable actionOutputTable = new JTable(outputModel) {
        };
        JBScrollPane actionOutputPanel = new JBScrollPane(actionOutputTable);
        actionOutputPanel.setPreferredSize(new Dimension(0, 200));

        ActionManager instance = ActionManager.getInstance();
        ActionGroup actionGroup = (ActionGroup) instance.getAction(Constants.ACTION_GROUP_REF_ENGINE_ACTION);
        ActionToolbar spd_tb = instance.createActionToolbar("spd tb", actionGroup, true);

        JComponent gotoactionScript = spd_tb.getComponent();


        panel.add(actionIdLabel);
        panel.add(actionIdTextField);
        panel.add(namespaceLabel);
        panel.add(namespaceTextField);
        panel.add(actionscriptLabel);
        panel.add(actionScriptScrollPane);
        panel.add(gotoactionScript);
        panel.add(actionInputLabel);
        panel.add(actionInputPanel);
        panel.add(actionOutputLabel);
        panel.add(actionOutputPanel);


    }
}
