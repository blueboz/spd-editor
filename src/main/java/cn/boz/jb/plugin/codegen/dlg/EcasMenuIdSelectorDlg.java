package cn.boz.jb.plugin.codegen.dlg;

import cn.boz.jb.plugin.idea.bean.EcasMenu;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Executors;

public class EcasMenuIdSelectorDlg extends DialogWrapper {
    public static final String ITEM_OCCUPY = "occupy";

    private Project project;
    public static final String ITEM_FREE = "free";
    private int currentNum ;
    private int pageSize ;
    DefaultTableModel tableModel;
    DBUtils dbUtils;
    JBTable actionPowerTable;
    JPanel mainPanel;


    public EcasMenuIdSelectorDlg(@Nullable Project project, boolean canBeParent) {
        super(project, canBeParent);
        setTitle("Ecas 菜单Id占用选择器");
        this.setModal(false);
        this.project = project;
        currentNum=SpdEditorDBState.getInstance(project).actionPowerStart;
        pageSize=SpdEditorDBState.getInstance(project).actionPowerPageSize;
        try {
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
        tableModel.setColumnIdentifiers(new Object[]{"rownum", "DEV", "YD01","YD02", "YD03", "description", "status"});
        actionPowerTable = new JBTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Object getValueAt(int row, int column) {
                if (column == 6) {
                    Object dev = getValueAt(row, 1);
                    Object yd01 = getValueAt(row, 2);
                    Object yd02 = getValueAt(row, 3);
                    Object yd03 = getValueAt(row, 4);
                    return checkStatus(dev, yd01,yd02, yd03);
                }
                return super.getValueAt(row, column);
            }

            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                return new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                        if (column == 6) {
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
        columnModel.getColumn(0).setMaxWidth(60);
        columnModel.getColumn(1).setMaxWidth(60);
        columnModel.getColumn(2).setMaxWidth(60);
        columnModel.getColumn(3).setMaxWidth(60);
        columnModel.getColumn(4).setMaxWidth(60);
        columnModel.getColumn(6).setMaxWidth(100);

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
                loadEcasMenu();
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
                    String status = checkStatus(vector.get(1), vector.get(2), vector.get(3),vector.get(4));
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

                loadEcasMenu();
            }
        });

        actionPowerTable.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                loadEcasMenu();
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

    private void loadEcasMenu() {
        int selectedRow = actionPowerTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        Object valueAt = tableModel.getValueAt(selectedRow, 0);
        EcasMenu ecasActionPower = null;
        try {
            String selectedItem = (String) envCombo.getSelectedItem();
            ecasActionPower = dbUtils.queryEcasMenu(999, ((BigDecimal) valueAt).intValue(), project, selectedItem);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        if (ecasActionPower != null) {
//            APPLID, MENUID, NAME, LVL, URL, PARENT, IMG, ISCHILD, GROUPID
            applidTe.setText(ecasActionPower.getApplid());
            menuIdTe.setText(ecasActionPower.getMenuid());
            nameTe.setText(ecasActionPower.getName() + "");
            lvlTe.setText(ecasActionPower.getLvl());
            urlTe.setText(ecasActionPower.getUrl() + "");
            parentTe.setText(ecasActionPower.getParent() + "");
            imgTe.setText(ecasActionPower.getImg() + "");
            ischildTe.setText(ecasActionPower.getIschild() + "");
            groupidTe.setText(ecasActionPower.getGroupid() + "");
        } else {
            applidTe.setText("");
            menuIdTe.setText("");
            nameTe.setText("");
            lvlTe.setText("");
            urlTe.setText("");
            parentTe.setText("");
            imgTe.setText("");
            ischildTe.setText("");
            groupidTe.setText("");
        }
    }

    public void onChoosen(Integer chooseValue) {

    }

    JTextField menuIdTe;
    JTextField applidTe;


    JTextField nameTe;
    JTextField imgTe;
    JTextField ischildTe;
    JTextField groupidTe;

    JTextField lvlTe;


    JTextField urlTe;


    JTextField parentTe;

    JComboBox<String> envCombo;


    @NotNull
    private String checkStatus(Object dev, Object yd01, Object yd02,Object yd03) {
        if (dev == null && yd01 == null && yd03 == null&&yd02==null) {
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
                apps = dbUtils.queryEcasMenuIdUniq(project, currentNum, pageSize, "@YD01","@YD02" ,"@YD03", 999);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            tableModel.setRowCount(0);

            for (Map<String, Object> app : apps) {

                tableModel.addRow(new Object[]{app.get("RN"), app.get("dev"), app.get("yd01"),app.get("yd02"), app.get("yd03"), app.get("name")});
            }
        };
        Executors.newSingleThreadExecutor().execute(r);
    }

    private JPanel createDetailCompoent() {


        JPanel jPanel = new JPanel();
        jPanel.setLayout(new MyLayoutManager());
        menuIdTe = new JTextField();
        jPanel.add(new JLabel("description"));
        jPanel.add(menuIdTe);
        applidTe = new JTextField();
        jPanel.add(new JLabel("Path"));
        jPanel.add(applidTe);
        nameTe = new JTextField();
        jPanel.add(new JLabel("enable"));
        jPanel.add(nameTe);
        lvlTe = new JTextField();
        jPanel.add(new JLabel("moduleName"));
        jPanel.add(lvlTe);
        urlTe = new JTextField();
        jPanel.add(new JLabel("weight"));
        jPanel.add(urlTe);
        parentTe = new JTextField();
        jPanel.add(new JLabel("menuId"));
        jPanel.add(parentTe);
        imgTe=new JTextField();
        jPanel.add(new JLabel("image"));
        jPanel.add(imgTe);
        ischildTe=new JTextField();
        jPanel.add(new JLabel("isChild"));
        jPanel.add(ischildTe);
        groupidTe=new JTextField();
        jPanel.add(new JLabel("groupId"));
        jPanel.add(groupidTe);

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

