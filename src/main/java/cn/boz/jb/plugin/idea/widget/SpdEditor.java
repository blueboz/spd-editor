package cn.boz.jb.plugin.idea.widget;

import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.table.JBTable;

import javax.swing.JComponent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.BorderLayout;
import java.io.File;

public class SpdEditor extends JComponent {

    ChartPanel chartPanel;
    JBSplitter jbSplitter;
    JBTable jbTable;
    JBPanel<JBPanel> jbPanelJBPanel;
    DefaultTableModel defaultTableModel;
    public SpdEditor(){
        jbSplitter = new JBSplitter();
        chartPanel = new ChartPanel();
        jbPanelJBPanel = new JBPanel<>();
        this.setLayout(new BorderLayout());


         jbTable = new JBTable() {
            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                return super.getCellEditor(row, column);
            }
         };
         defaultTableModel = new DefaultTableModel(null,new String[]{"属性","值"}){
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
        defaultTableModel.addRow(new Object[]{"home","japan"});
        jbTable.setModel(defaultTableModel);
        jbTable.setRowHeight(38);

        jbPanelJBPanel.setLayout(new BorderLayout());
        jbPanelJBPanel.add(jbTable.getTableHeader(),BorderLayout.NORTH);
        jbPanelJBPanel.add(jbTable,BorderLayout.CENTER);

        this.add(jbSplitter,BorderLayout.CENTER);
        jbSplitter.setFirstComponent(chartPanel);
        jbSplitter.setSecondComponent(jbPanelJBPanel);
    }

    public void loadFromFile(File file){
        chartPanel.loadFromFile(file);
    }

    public boolean isModified() {
        return chartPanel.isModified();
    }

    public ChartPanel getChartPanel() {
        return chartPanel;
    }

    public void setChartPanel(ChartPanel chartPanel) {
        this.chartPanel = chartPanel;
    }
}
