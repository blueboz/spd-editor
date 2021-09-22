package cn.boz.jb.plugin.idea.widget;

import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import com.intellij.ui.AddEditRemovePanel;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.table.JBTable;
import com.intellij.ui.table.TableView;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.BorderLayout;
import java.io.File;

public class SpdEditor extends JComponent {

    ChartPanel chartPanel;
    JBSplitter jbSplitter;
    JBTable jbTable;
    DefaultTableModel defaultTableModel;
    public SpdEditor(){
        jbSplitter = new JBSplitter();
        chartPanel = new ChartPanel();
        this.setLayout(new BorderLayout());


         jbTable = new JBTable() {
            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                return super.getCellEditor(row, column);
            }
         };
         defaultTableModel = new DefaultTableModel(null,new String[]{"property","value"}){
            @Override
            public boolean isCellEditable(int row, int column) {
                if(column==0){
                    return false;
                }
                return true;

            }

        };
        defaultTableModel.addRow(new Object[]{"username","jaychou"});
        defaultTableModel.addRow(new Object[]{"age","27"});
        defaultTableModel.addRow(new Object[]{"gender","male"});
        jbTable.setModel(defaultTableModel);


        this.add(jbSplitter,BorderLayout.CENTER);
        jbSplitter.setFirstComponent(chartPanel);
        jbSplitter.setSecondComponent(jbTable);
    }

    public void loadFromFile(File file){
        chartPanel.loadFromFile(file);
    }

    public boolean isModified() {
        return chartPanel.isModified();
    }
}
