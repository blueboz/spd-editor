package cn.boz.jb.plugin.codegen.dlg;

import cn.boz.jb.plugin.idea.bean.EcasActionPower;
import cn.boz.jb.plugin.idea.configurable.SpdEditorDBState;
import cn.boz.jb.plugin.idea.layoutmanager.MyLayoutManager;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.project.Project;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Executors;

public class EngineActionSelectorDlg extends DialogWrapper {
    public static final String ITEM_OCCUPY = "occupy";

    private Project project;
    public static final String ITEM_FREE = "free";
    private int currentNum ;
    private int pageSize ;
    DefaultTableModel tableModel;
    DBUtils dbUtils;
    Connection dbConn;
    JBTable actionPowerTable;
    JPanel mainPanel;


    public EngineActionSelectorDlg(@Nullable Project project, boolean canBeParent) {
        super(project, canBeParent);
        this.setModal(false);
        this.project = project;
        currentNum=SpdEditorDBState.getInstance(project).actionPowerStart;
        pageSize=SpdEditorDBState.getInstance(project).actionPowerPageSize;
        try {
            dbConn = DBUtils.getConnection(project);
            dbUtils = DBUtils.getInstance();
        } catch (Exception e) {
            DBUtils.dbExceptionProcessor(e, project);
        }
        init();
        this.setSize(800, 800);

    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        mainPanel = new JPanel();
        try {
            JPanel component = createListComponent();
            JPanel detailCompoent = createDetailCompoent();
            mainPanel.setLayout(new BorderLayout());
            mainPanel.add(component, BorderLayout.CENTER);
            mainPanel.add(detailCompoent, BorderLayout.SOUTH);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        mainPanel.setPreferredSize(new Dimension(800, 800));

        return mainPanel;
    }
    JSpinner startNumberSpinner;
    SpinnerNumberModel startNumberModel;
    JSpinner pageSizeSpinner;
    SpinnerNumberModel pageSizeModel;
    private JPanel createListComponent() throws Exception {
        JPanel jPanel = new JPanel();
        JScrollPane panel = new JScrollPane();

        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new Object[]{"rownum", "DEV", "YD01", "YD03", "description", "status"});
        actionPowerTable = new JBTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Object getValueAt(int row, int column) {
                if (column == 5) {
                    Object dev = getValueAt(row, 1);
                    Object yd01 = getValueAt(row, 2);
                    Object yd03 = getValueAt(row, 3);
                    return checkStatus(dev, yd01, yd03);
                }
                return super.getValueAt(row, column);
            }

            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                return new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                        if (column == 5) {
                            if (ITEM_OCCUPY.equals(value)) {
                                component.setBackground(Color.YELLOW);
                                component.setForeground(new Color(252, 114, 114));
                            } else {
                                component.setBackground(new Color(216, 248, 70));
                                component.setForeground(Color.black);
                            }
                        }
                        return component;
                    }
                };

            }
        };
        actionPowerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        load();

        TableColumnModel columnModel = actionPowerTable.getTableHeader().getColumnModel();
        columnModel.getColumn(0).setMaxWidth(50);
        columnModel.getColumn(1).setMaxWidth(50);
        columnModel.getColumn(2).setMaxWidth(50);
        columnModel.getColumn(3).setMaxWidth(50);
        columnModel.getColumn(5).setMaxWidth(100);

        panel.setViewportView(actionPowerTable);
        jPanel.setLayout(new BorderLayout());

        JLabel currentPageLabel = new JLabel("startnum:");

        startNumberSpinner = new JSpinner();
        startNumberModel = new SpinnerNumberModel(currentNum, 0, Integer.MAX_VALUE, 10);
        startNumberSpinner.setModel(startNumberModel);

        JLabel pageSizeLabel = new JLabel("num:");

        pageSizeSpinner = new JSpinner();
        pageSizeModel = new SpinnerNumberModel(pageSize, 0, Integer.MAX_VALUE, 1);
        pageSizeSpinner.setModel(pageSizeModel);


        startNumberSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Object value = startNumberModel.getValue();
                if (value instanceof Integer) {
                    currentNum = (int) value;
                } else {
                    currentNum = ((Double) value).intValue();
                }
                SpdEditorDBState.getInstance(project).actionPowerStart= currentNum;

                load();
            }
        });

        pageSizeSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Object value = pageSizeModel.getValue();
                if (value instanceof Integer) {
                    pageSize = (int) value;
                } else {
                    pageSize = ((Double) value).intValue();
                }
                SpdEditorDBState.getInstance(project).actionPowerPageSize= pageSize;
                startNumberModel.setStepSize(pageSize);

                load();
            }
        });
        envCombo = new ComboBox<>(new String[]{"DEV", "YD01", "YD02", "YD03"});
        envCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                loadActionPower();
            }
        });
        JComponent toolbarContainer = new JPanel();
        toolbarContainer.add(currentPageLabel);
        toolbarContainer.add(startNumberSpinner);
        toolbarContainer.add(pageSizeLabel);
        toolbarContainer.add(pageSizeSpinner);
        toolbarContainer.add(envCombo);
        JButton refreshBtn = new JButton("刷新");
        toolbarContainer.add(refreshBtn);
        refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                load();
            }
        });

        actionPowerTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = actionPowerTable.getSelectedRow();
                    Vector vector = tableModel.getDataVector().elementAt(selectedRow);
                    String status = checkStatus(vector.get(1), vector.get(2), vector.get(3));
                    if (ITEM_OCCUPY.equals(status)) {
                    } else {
                        onChoosen(((BigDecimal) vector.get(0)).intValue());
                    }
                }
            }
        });
        actionPowerTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                loadActionPower();
            }
        });

        actionPowerTable.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                loadActionPower();
            }
        });


        jPanel.add(panel, BorderLayout.CENTER);
        ActionManager instance = ActionManager.getInstance();

        ActionGroup actionGroup = (ActionGroup) instance.getAction("spd.menuid.spanel.group");
        ActionToolbar spd_tb = instance.createActionToolbar("spd tb", actionGroup, true);
        JComponent tableNavigator = spd_tb.getComponent();
        toolbarContainer.add(tableNavigator);

        jPanel.add(toolbarContainer, BorderLayout.SOUTH);


        return jPanel;
    }

    private void loadActionPower() {
        int selectedRow = actionPowerTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        Object valueAt = tableModel.getValueAt(selectedRow, 0);
        EcasActionPower ecasActionPower = null;
        try {
            String selectedItem = (String) envCombo.getSelectedItem();
            ecasActionPower = dbUtils.queryActionPower(999, ((BigDecimal) valueAt).intValue(), dbConn, selectedItem);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        if (ecasActionPower != null) {

            actionPowerPathTE.setText(ecasActionPower.getPath());
            actionPowerdescriptionTE.setText(ecasActionPower.getDescription());
            actionPowerEnableTE.setText(ecasActionPower.getEnabled() + "");
            actionPowerModuleNameTE.setText(ecasActionPower.getModuleName());
            actionPowerWeightTE.setText(ecasActionPower.getWeight() + "");
            actionPowerMenuIdTE.setText(ecasActionPower.getMenuId() + "");
        } else {
            actionPowerPathTE.setText("");
            actionPowerdescriptionTE.setText("");
            actionPowerEnableTE.setText("");
            actionPowerModuleNameTE.setText("");
            actionPowerWeightTE.setText("");
            actionPowerMenuIdTE.setText("");
        }
    }

    public void onChoosen(Integer chooseValue) {

    }

    JTextField actionPowerdescriptionTE;
    JTextField actionPowerPathTE;


    JTextField actionPowerEnableTE;


    JTextField actionPowerModuleNameTE;


    JTextField actionPowerWeightTE;


    JTextField actionPowerMenuIdTE;

    JComboBox<String> envCombo;


    @NotNull
    private String checkStatus(Object dev, Object yd01, Object yd03) {
        if (dev == null && yd01 == null && yd03 == null) {
            return ITEM_FREE;
        } else {
            return ITEM_OCCUPY;
        }
    }


    public void load() {
        Runnable r = () -> {
            tableModel.setRowCount(0);
            tableModel.addRow(new Object[]{"loading..."});


            List<Map<String, Object>> apps = null;
            try {
                apps = dbUtils.queryActionPowerUniq(dbConn, currentNum, pageSize, "@YD01", "@YD03", 999);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            tableModel.setRowCount(0);

            for (Map<String, Object> app : apps) {

                tableModel.addRow(new Object[]{app.get("RN"), app.get("dev"), app.get("yd01"), app.get("yd03"), app.get("description")});
            }
        };
        Executors.newSingleThreadExecutor().execute(r);
    }

    private JPanel createDetailCompoent() {


        JPanel jPanel = new JPanel();
        jPanel.setLayout(new MyLayoutManager());
        actionPowerdescriptionTE = new JTextField();
        jPanel.add(new JLabel("description"));
        jPanel.add(actionPowerdescriptionTE);
        actionPowerPathTE = new JTextField();
        jPanel.add(new JLabel("Path"));
        jPanel.add(actionPowerPathTE);
        actionPowerEnableTE = new JTextField();
        jPanel.add(new JLabel("enable"));
        jPanel.add(actionPowerEnableTE);
        actionPowerModuleNameTE = new JTextField();
        jPanel.add(new JLabel("moduleName"));
        jPanel.add(actionPowerModuleNameTE);
        actionPowerWeightTE = new JTextField();
        jPanel.add(new JLabel("weight"));
        jPanel.add(actionPowerWeightTE);
        actionPowerMenuIdTE = new JTextField();
        jPanel.add(new JLabel("menuId"));
        jPanel.add(actionPowerMenuIdTE);
        return jPanel;
    }
    public void loadPrevPage() {
        int i = currentNum - pageSize;
        if(i<=0){
            startNumberModel.setValue(0 );
        }else{
            startNumberModel.setValue(i );
        }

//        cpageNum--;
//        SpdEditorDBState.getInstance(project).pageNum = cpageNum;
//        load();
    }

    public void loadNextPage() {
        startNumberModel.setValue(currentNum + pageSize);

    }

    public JBScrollPane derive() {
        return new JBScrollPane(this.mainPanel);

    }


}

