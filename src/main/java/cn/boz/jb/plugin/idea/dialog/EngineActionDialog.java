package cn.boz.jb.plugin.idea.dialog;

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
        String id = (String) engineAction.get("ID_");
        String namespace = (String) engineAction.get("NAMESPACE_");
        String actionscript = (String) engineAction.get("ACTIONSCRIPT_");

        myLayoutManager = new MyLayoutManager();

        this.setLayout(myLayoutManager);
//        this.getContentPane().setLayout(myLayoutManager);

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
        actionOutputPanel = new JScrollPane(actionOutputTable);
        actionOutputPanel.setPreferredSize(new Dimension(0, 200));

        this.add(actionIdLabel);
        this.add(actionIdTextField);
        this.add(namespaceLabel);
        this.add(namespaceTextField);
        this.add(actionscriptLabel);
        this.add(actionScriptScrollPane);
        this.add(actionInputLabel);
        this.add(actionInputPanel);
        this.add(actionOutputLabel);
        this.add(actionOutputPanel);

        this.setFocusable(true);
    }


}
