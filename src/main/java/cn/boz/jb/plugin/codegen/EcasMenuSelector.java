package cn.boz.jb.plugin.codegen;

import cn.boz.jb.plugin.idea.bean.EcasActionPower;
import cn.boz.jb.plugin.idea.layoutmanager.MyLayoutManager;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import oracle.jdbc.OracleDriver;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Executors;

public class EcasMenuSelector extends JFrame {
    public static final String ITEM_OCCUPY = "occupy";

    public static final String ITEM_FREE = "free";

    public EcasMenuSelector() throws Exception {
        initUI();
    }

    private void alignCenter() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = this.getWidth();
        int height = this.getHeight();
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        this.setLocation(x, y);
    }

    private void initUI() throws Exception {
        setSize(800, 800);
        alignCenter();
        setTitle("PowerBit Usages");
        JPanel component = createComponent();
        getContentPane().add(component, BorderLayout.CENTER);

        JPanel form=createDetailCompoent();
        getContentPane().add(form,BorderLayout.SOUTH);


        setVisible(true);

    }

    JTextField actionPowerdescriptionTE;
    JTextField actionPowerPathTE;


    JTextField actionPowerEnableTE;


    JTextField actionPowerModuleNameTE;


    JTextField actionPowerWeightTE;


    JTextField actionPowerMenuIdTE;

    JComboBox<String> envCombo;

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

    @NotNull
    private String checkStatus(Object dev, Object yd01, Object yd03) {
        if (dev == null && yd01 == null && yd03 == null) {
            return ITEM_FREE;
        } else {
            return ITEM_OCCUPY;
        }
    }

    private int currentNum=1;
    private int pageSize = 30;
    DefaultTableModel tableModel;
    DBUtils instance;
    JTable jTable;

    private JPanel createComponent() throws Exception {
        JPanel jPanel = new JPanel();
        JScrollPane panel = new JScrollPane();
//        connection = DBUtils.getInstance().getConnection("xfunds201701", "Xfunds_1234", "jdbc:oracle:thin:@21.96.5.85:1521:FMSS", OracleDriver.class.getName());
//
//        instance = DBUtils.getInstance();

        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new Object[]{"rownum", "DEV", "YD01", "YD03", "description","status"});
        jTable = new JTable(tableModel) {
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
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        load();
//        tableModel.setColumnIdentifiers(new Object[]{"rownum", "DEV", "YD01", "YD03", "description","status"});

        TableColumnModel columnModel = jTable.getTableHeader().getColumnModel();
        columnModel.getColumn(0).setMaxWidth(50);
        columnModel.getColumn(1).setMaxWidth(50);
        columnModel.getColumn(2).setMaxWidth(50);
        columnModel.getColumn(3).setMaxWidth(50);
        columnModel.getColumn(5).setMaxWidth(100);

        panel.setViewportView(jTable);
        jPanel.setLayout(new BorderLayout());

        JLabel currentPageLabel = new JLabel("startnum:");
        JSpinner currentPageSpin;
        SpinnerNumberModel currentPageModel;
        currentPageSpin = new JSpinner();
        currentPageModel = new SpinnerNumberModel(currentNum, 0, Integer.MAX_VALUE, 10);
        currentPageSpin.setModel(currentPageModel);

        JLabel pageSizeLabel = new JLabel("num:");
        JSpinner pageSizeSpin;
        pageSizeSpin = new JSpinner();
        SpinnerNumberModel pageSizeModel;
        pageSizeModel = new SpinnerNumberModel(pageSize, 0, Integer.MAX_VALUE, 1);
        pageSizeSpin.setModel(pageSizeModel);


        currentPageSpin.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Object value = currentPageModel.getValue();
                if (value instanceof Integer) {
                    currentNum = (int) value;
                } else {
                    currentNum = ((Double) value).intValue();
                }
                load();
            }
        });

        pageSizeSpin.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Object value = pageSizeModel.getValue();
                if (value instanceof Integer) {
                    pageSize = (int) value;
                } else {
                    pageSize = ((Double) value).intValue();
                }
                load();
            }
        });
        envCombo = new JComboBox<>(new String[]{"DEV","YD01","YD02","YD03"});
        envCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                loadActionPower();
            }
        });
        JComponent toolbarContainer = new JPanel();
        toolbarContainer.add(currentPageLabel);
        toolbarContainer.add(currentPageSpin);
        toolbarContainer.add(pageSizeLabel);
        toolbarContainer.add(pageSizeSpin);
        toolbarContainer.add(envCombo);
        JButton refreshBtn = new JButton("刷新");
        toolbarContainer.add(refreshBtn);
        refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                load();
            }
        });

        ;

        jTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = jTable.getSelectedRow();
                    Vector vector = tableModel.getDataVector().elementAt(selectedRow);
                    String status = checkStatus(vector.get(1), vector.get(2), vector.get(3));
                    if (ITEM_OCCUPY.equals(status)) {
                    } else {
                        onChoosen(((BigDecimal) vector.get(0)).intValue());
                    }
                }
            }
        });
        jTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                    loadActionPower();
            }
        });

        jTable.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                loadActionPower();
            }
        });


        jPanel.add(panel, BorderLayout.CENTER);

        jPanel.add(toolbarContainer, BorderLayout.SOUTH);


        return jPanel;
    }
    private void loadActionPower(){
        int selectedRow = jTable.getSelectedRow();
        if(selectedRow==-1){
            return ;
        }
        Object valueAt = tableModel.getValueAt(selectedRow, 0);
        EcasActionPower ecasActionPower = null;
        try {
            String selectedItem = (String) envCombo.getSelectedItem();
            ecasActionPower = instance.queryActionPower(999, ((BigDecimal)valueAt).intValue(), null,selectedItem);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        if(ecasActionPower!=null){

            actionPowerPathTE.setText(ecasActionPower.getPath());
            actionPowerdescriptionTE.setText(ecasActionPower.getDescription());
            actionPowerEnableTE.setText(ecasActionPower.getEnabled()+"");
            actionPowerModuleNameTE.setText(ecasActionPower.getModuleName());
            actionPowerWeightTE.setText(ecasActionPower.getWeight()+"");
            actionPowerMenuIdTE.setText(ecasActionPower.getMenuId()+"");
        }else{
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

    public void load() {
        Runnable r = () -> {
            tableModel.setRowCount(0);
            tableModel.addRow(new Object[]{"loading..."});


            List<Map<String, Object>> apps = null;
            try {
                apps = instance.queryActionPowerUniq(null, currentNum, pageSize, "@YD01","@YD02", "@YD03", 999);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            tableModel.setRowCount(0);

            for (Map<String, Object> app : apps) {

                tableModel.addRow(new Object[]{app.get("RN"), app.get("dev"), app.get("yd01"), app.get("yd03"),app.get("description")});
            }
        };
        Executors.newSingleThreadExecutor().execute(r);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new EcasMenuSelector();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
