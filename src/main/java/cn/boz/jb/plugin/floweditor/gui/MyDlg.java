package cn.boz.jb.plugin.floweditor.gui;

import com.intellij.designer.DesignerEditorPanelFacade;
import com.intellij.designer.LightToolWindow;
import com.intellij.designer.LightToolWindowManager;
import com.intellij.designer.ToggleEditorModeAction;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.wm.ToolWindowAnchor;
import org.jdesktop.swingx.table.DatePickerCellEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

public class MyDlg extends JFrame {

    private JTable table;
    private JScrollPane jScrollPane;
    private JButton jButton;

    public MyDlg() {
        this.setLayout(new BorderLayout());


        table = new JTable(){

            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (column == 1) {
                    TableCellRenderer tableCellRenderer = new TableCellRenderer() {

                        @Override
                        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                            JCheckBox jCheckBox = new JCheckBox();
                            jCheckBox.setSelected((Boolean) value);
                            return jCheckBox;
                        }
                    };
                    return tableCellRenderer;
                }
                return super.getCellRenderer(row, column);
            }

            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                if(column==1){
                    DefaultCellEditor defaultCellEditor = new DefaultCellEditor(new JCheckBox());
                    return defaultCellEditor;
                }
                new TableCellEditor(){

                    @Override
                    public Object getCellEditorValue() {
                        return null;
                    }

                    @Override
                    public boolean isCellEditable(EventObject anEvent) {
                        return false;
                    }

                    @Override
                    public boolean shouldSelectCell(EventObject anEvent) {
                        return false;
                    }

                    @Override
                    public boolean stopCellEditing() {
                        return false;
                    }

                    @Override
                    public void cancelCellEditing() {

                    }

                    @Override
                    public void addCellEditorListener(CellEditorListener l) {

                    }

                    @Override
                    public void removeCellEditorListener(CellEditorListener l) {

                    }

                    @Override
                    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                        return null;
                    }
                };


                return super.getCellEditor(row, column);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                if(column==0){
                    return false;
                }
                return super.isCellEditable(row, column);
            }
        };

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jScrollPane = new JScrollPane();
        jScrollPane.setViewportView(table);
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        tableModel.setRowCount(0);//清空数据

        tableModel.setColumnIdentifiers(new Object[]{"书名", "时间"});
        tableModel.addRow(new Object[]{"java入门到精通", true});
        tableModel.addRow(new Object[]{"C语言从入门到精通", false});
        tableModel.addRow(new Object[]{"PHP开发", true});
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                //被改变的行，如果只有一行，两者相同
                int firstRow = e.getFirstRow();
                int lastRow = e.getLastRow();
                //被改变的列
                int column = e.getColumn();
                int type = e.getType();
                //INSERT,UPDATE,DELETE

            }
        });
        table.setRowHeight(80);
        table.setModel(tableModel);

        jButton = new JButton("Remove");
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = table.getSelectedRows();
                DefaultTableModel dft= (DefaultTableModel) table.getModel();
                for (int i = 0; i <selectedRows.length ; i++) {
                    dft.removeRow(selectedRows[i]);
                }
                table.setModel(dft);
            }
        });
        this.add(jButton,BorderLayout.NORTH);
        this.add(jScrollPane);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                //注册字体
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                MyDlg myDlg = new MyDlg();
                myDlg.setVisible(true);
                Rectangle rectangle = ge.getMaximumWindowBounds();
                myDlg.setBounds(rectangle.x + rectangle.width / 4, rectangle.y + rectangle.height / 4, rectangle.width / 2, rectangle.height / 2);
                myDlg.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
