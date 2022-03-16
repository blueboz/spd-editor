package cn.boz.jb.plugin.idea.dialog;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;
import java.util.List;
import java.util.Map;

public class EngineActionDialog extends JComponent {

    private Map<String, Object> engineAction;
    private List<Map<String, Object>> engineActionInput;
    private List<Map<String, Object>> engineActionOutput;

    public Map<String, Object> getEngineAction() {
        return engineAction;
    }

    public void setEngineAction(Map<String, Object> engineAction) {
        this.engineAction = engineAction;
    }

    public List<Map<String, Object>> getEngineActionInput() {
        return engineActionInput;
    }

    public void setEngineActionInput(List<Map<String, Object>> engineActionInput) {
        this.engineActionInput = engineActionInput;
    }

    public List<Map<String, Object>> getEngineActionOutput() {
        return engineActionOutput;
    }

    public void setEngineActionOutput(List<Map<String, Object>> engineActionOutput) {
        this.engineActionOutput = engineActionOutput;
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

    public EngineActionDialog(Map<String, Object> engineAction, List<Map<String, Object>> engineActionInput, List<Map<String, Object>> engineActionOutput) {
        this.engineAction = engineAction;
        this.engineActionInput = engineActionInput;
        this.engineActionOutput = engineActionOutput;
        String id = (String) engineAction.get("ID_");
        String namespace = (String) engineAction.get("NAMESPACE_");
        String actionscript = (String) engineAction.get("ACTIONSCRIPT_");


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


        actionScriptScrollPane = new JScrollPane(actionScriptTextArea);

        actionInputLabel = new JLabel("actionInput:");
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setRowCount(0);    //清空表格中的数据
        tableModel.setColumnIdentifiers(new Object[]{"BEANID", "CLAZZ", "SOURCE"});    //设置表头
        for (Map<String, Object> actionInput : engineActionInput) {
            tableModel.addRow(new Object[]{actionInput.get("BEANID_"), actionInput.get("CLAZZ_"), actionInput.get("SOURCE_")});
        }
        actionInputTable = new JTable(tableModel) {
        };

//        ProjectManager projectManager = ProjectManager.getInstance();
//        Project defaultProject = projectManager.getDefaultProject();


        actionInputPanel = new JScrollPane(actionInputTable);
        actionInputPanel.setPreferredSize(new Dimension(0, 200));

        actionOutputLabel = new JLabel("actionOutput:");
        outputModel = new DefaultTableModel();
        outputModel.setRowCount(0);    //清空表格中的数据
        outputModel.setColumnIdentifiers(new Object[]{"BEANID"});    //设置表头
        for (Map<String, Object> actionInput : engineActionOutput) {
            outputModel.addRow(new Object[]{actionInput.get("BEANID_")});
        }
        actionOutputTable = new JTable(outputModel) {
        };
        actionOutputPanel = new JBScrollPane(actionOutputTable);
        actionOutputPanel.setPreferredSize(new Dimension(0, 200));
        ActionManager instance = ActionManager.getInstance();
        ActionGroup actionGroup = (ActionGroup) instance.getAction("spd.engineaction.dlg.group");
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


}
