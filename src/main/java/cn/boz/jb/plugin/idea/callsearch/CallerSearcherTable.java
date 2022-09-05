package cn.boz.jb.plugin.idea.callsearch;

import com.intellij.ui.table.JBTable;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class CallerSearcherTable extends JBTable {
    public CallerSearcherTable(TableModel model) {
        this(model,null);

    }
    private String qualifierName;
    private String queryName;

    public String getQualifierName() {
        return qualifierName;
    }

    public void setQualifierName(String qualifierName) {
        this.qualifierName = qualifierName;
    }

    public String getQueryName() {
        return queryName;
    }

    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }

    public CallerSearcherTable(TableModel model, TableColumnModel columnModel) {
        super(model, columnModel);
        new CallerSearcherSpeedSearch(this);
        final TableColumn c0 = getColumnModel().getColumn(0);
        c0.setMaxWidth(24);
        c0.setWidth(24);
        c0.setMinWidth(24);

        final TableColumn c1 = getColumnModel().getColumn(1);
        //设置ID列宽度
        c1.setWidth(210);
        c1.setMinWidth(250);
        setRowSorter(new TableRowSorter<>(model));

    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        return new CallerSearcherTableCellRender();
    }
}
