package cn.boz.jb.plugin.idea.toolwindow;

import cn.boz.jb.plugin.idea.configurable.SpdEditorDBState;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.util.PopupUtil;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Executors;

public class MenuIdDialog extends JComponent {

    public static final String ITEM_OCCUPY = "occupy";

    public static final String ITEM_FREE = "free";
;

    private DBUtils instance;
    private DefaultTableModel tableModel;

    private JSpinner currentPageSpin;
    private SpinnerNumberModel currentPageModel;
    private JSpinner pageSizeSpin;
    private SpinnerNumberModel pageSizeModel;
    private Integer applid;

    private JPanel component;

    private Project project;

    private int cpageNum;
    private int pageSize;
    protected MenuIdDialog(Project project, Integer applid) {
        cpageNum=SpdEditorDBState.getInstance(project).pageNum;
        pageSize=SpdEditorDBState.getInstance(project).pageSize;
        this.project=project;
        this.applid = applid;

        try {
            component = createComponent();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.setLayout(new BorderLayout());
        this.add(component, BorderLayout.CENTER);
        this.setPreferredSize(new Dimension(600, 800));
    }

    private JPanel createComponent() throws Exception {
        JPanel jPanel = new JPanel();
        JScrollPane panel = new JBScrollPane();

        instance = DBUtils.getInstance();

        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new Object[]{"rownum", "DEV", "YD01", "YD03", "status"});
        JTable jTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Object getValueAt(int row, int column) {
                if (column == 4) {
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

        panel.setViewportView(jTable);
        jPanel.setLayout(new BorderLayout());

        JLabel currentPageLabel = new JLabel("page:");
        currentPageSpin = new JSpinner();
        currentPageModel = new SpinnerNumberModel(cpageNum, 0, 200, 1);
        currentPageSpin.setModel(currentPageModel);

        JLabel pageSizeLabel = new JLabel("num:");
        pageSizeSpin = new JSpinner();
        pageSizeModel = new SpinnerNumberModel(pageSize, 0, 1000, 1);
        pageSizeSpin.setModel(pageSizeModel);


        currentPageSpin.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Object value = currentPageModel.getValue();
                if (value instanceof Integer) {
                    cpageNum = (int) value;
                } else {
                    cpageNum = ((Double) value).intValue();
                }
                SpdEditorDBState.getInstance(project).pageNum = cpageNum;
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
                SpdEditorDBState.getInstance(project).pageSize = pageSize;
                load();
            }
        });
        JComponent toolbarContainer = new JPanel();
        toolbarContainer.add(currentPageLabel);
        toolbarContainer.add(currentPageSpin);
        toolbarContainer.add(pageSizeLabel);
        toolbarContainer.add(pageSizeSpin);
//        toolbarContainer.add(prev);
//        toolbarContainer.add(next);

        jTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = jTable.getSelectedRow();
                    Vector vector = tableModel.getDataVector().elementAt(selectedRow);
                    String status = checkStatus(vector.get(1), vector.get(2), vector.get(3),vector.get(4));
                    if (ITEM_OCCUPY.equals(status)) {
                    } else {
                        JBPopup popupContainerFor = PopupUtil.getPopupContainerFor(jPanel);
                        popupContainerFor.dispose();
                        onChoosen(((BigDecimal) vector.get(0)).intValue());
                    }
                }
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

    @NotNull
    private String checkStatus(Object dev, Object yd01,Object yd02, Object yd03) {
        if (dev == null && yd01 == null && yd03 == null&&yd02!=null) {
            return ITEM_FREE;
        } else {
            return ITEM_OCCUPY;
        }
    }

    private int selecteditem = 0;

    public int getSelectedItem() {
        return selecteditem;
    }

    public void load() {
        Runnable r = () -> {
            tableModel.setRowCount(0);
            tableModel.addRow(new Object[]{"loading..."});


            List<Map<String, Object>> apps = null;
            try {
                apps = instance.queryEcasMenuIdUniq(project, cpageNum, pageSize, "@YD01", "@YD02","@YD03", applid);
            } catch (Exception ex) {
                DBUtils.dbExceptionProcessor(ex,project);
            }
            tableModel.setRowCount(0);

            for (Map<String, Object> app : apps) {

                tableModel.addRow(new Object[]{app.get("RN"), app.get("dev"), app.get("yd01"),app.get("yd02"), app.get("yd03")});
            }
        };
        Executors.newSingleThreadExecutor().execute(r);
    }


    public void onChoosen(Integer chooseValue) {

    }

    public void loadPrevPage() {
        currentPageModel.setValue(cpageNum - 1);

//        cpageNum--;
//        SpdEditorDBState.getInstance(project).pageNum = cpageNum;
//        load();
    }

    public void loadNextPage() {
//        cpageNum++;
//        SpdEditorDBState.getInstance(project).pageNum = cpageNum;
//        load();
        currentPageModel.setValue(cpageNum + 1);

    }
}