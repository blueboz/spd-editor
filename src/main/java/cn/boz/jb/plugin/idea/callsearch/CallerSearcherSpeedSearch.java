package cn.boz.jb.plugin.idea.callsearch;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.TableSpeedSearch;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class CallerSearcherSpeedSearch extends TableSpeedSearch {

    public CallerSearcherSpeedSearch(JTable table) {
        super(table);
    }

    @Override
    protected void onSearchFieldUpdated(String pattern) {
        super.onSearchFieldUpdated(pattern);
        RowSorter<? extends TableModel> sorter0 = myComponent.getRowSorter();
        if (!(sorter0 instanceof TableRowSorter)) {
            return;
        }
        //noinspection unchecked
        TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) sorter0;
        if (StringUtil.isNotEmpty(pattern)) {

            sorter.setRowFilter(new RowFilter<>() {
                @Override
                public boolean include(Entry<? extends TableModel, ? extends Integer> entry) {
                    return isMatchingRow(entry.getIdentifier(), pattern);
                }
            });
        } else {
            sorter.setRowFilter(null);
        }
    }

    protected boolean isMatchingRow(int modelRow, String pattern) {
        int columns = myComponent.getColumnCount();
        for (int col = 0; col < columns; col++) {
            String str = (String) myComponent.getModel().getValueAt(modelRow, col);
            if (str != null && compare(str, pattern)) {
                return true;
            }
        }
        return false;
    }
}