package cn.boz.jb.plugin.floweditor.gui.widget;

import cn.boz.jb.plugin.floweditor.gui.property.Property;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

public class MyTable extends JTable {

    public static int DEFAULT_ROW_HEIGHT=28;
    private MyTableCellEditor myTableCellEditor;
    private MyTableModel myTableModel;
    private MyTableCellRender myTableCellRender;
    private List<Property> myProperties = new ArrayList<>();

    public MyTable() {
        myTableCellEditor = new MyTableCellEditor();
        myTableModel = new MyTableModel();
        myTableCellRender = new MyTableCellRender();
        this.setModel(myTableModel);
        //初始化各行高度
        for (int i = 0; i < myProperties.size(); i++) {
            this.setRowHeight(i, myProperties.get(i).getRowHeight());
        }
        this.getTableHeader().setPreferredSize(new Dimension(0,DEFAULT_ROW_HEIGHT));
    }


    public void clearProperies() {
        //编辑器是否应该重新获取
        if(this.isEditing()){
            this.getCellEditor().stopCellEditing();
        }

        myProperties.clear();
    }

    public void addProperty(Property property) {
        myProperties.add(property);
    }


    @Override
    public TableCellEditor getCellEditor() {
        return super.getCellEditor();
    }


    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        //这里的作用基本上是用于记录row,与column,方便后面进行渲染
        //在editor里面是获取不到proeprty
        return myTableCellEditor;
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {

        return myTableCellRender;
    }

    public void resetRowHeight() {
        this.setRowHeight(38);
        for (int i = 0; i < myProperties.size(); i++) {
            this.setRowHeight(i, myProperties.get(i).getRowHeight());
        }
    }


    public class MyTableModel extends AbstractTableModel {

        private String columnNames[] = new String[]{"属性名", "属性值"};


        @Override
        public int getRowCount() {
            //这里面需要维护
            //这里有数据内容,能够对数据进行定制型设置
            return myProperties.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }


        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            //数据流向是、
            return myProperties.get(rowIndex);
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return false;
            }
            return true;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            Property property = myProperties.get(rowIndex);
            property.setValue(aValue);
        }
    }

}
