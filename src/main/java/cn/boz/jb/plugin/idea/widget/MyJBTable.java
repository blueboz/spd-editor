package cn.boz.jb.plugin.idea.widget;

import cn.boz.jb.plugin.floweditor.gui.control.PropertyObject;
import cn.boz.jb.plugin.floweditor.gui.events.ShapeSelectedEvent;
import cn.boz.jb.plugin.floweditor.gui.listener.ShapeSelectedListener;
import cn.boz.jb.plugin.floweditor.gui.property.Property;
import cn.boz.jb.plugin.floweditor.gui.property.PropertyEditorListener;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import cn.boz.jb.plugin.idea.configurable.SpdEditorDBState;
import com.intellij.ui.table.JBTable;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MyJBTable extends JBTable implements ShapeSelectedListener, PropertyEditorListener {

    public static int DEFAULT_ROW_HEIGHT = 28;
    private MyJBTableCellEditor myTableCellEditor;
    private MyTableModel myTableModel;
    private MyJBTableCellRender myTableCellRender;
    private List<Property> myProperties = new ArrayList<>();
    private PropertyObject operatedObject;
    private ChartPanel chartPanel;

    public MyJBTable() {
        myTableCellEditor = new MyJBTableCellEditor();
        myTableModel = new MyTableModel();
        myTableCellRender = new MyJBTableCellRender();
        this.setModel(myTableModel);
        //初始化各行高度
        for (int i = 0; i < myProperties.size(); i++) {
            this.setRowHeight(i, myProperties.get(i).getRowHeight());
        }
        this.getTableHeader().setPreferredSize(new Dimension(0, DEFAULT_ROW_HEIGHT));
//        this.getColumnModel().getColumn(0).setMaxWidth(180);

        TableColumn property = this.getColumn("Property");
        property.setPreferredWidth(80);
        property.setMaxWidth(120);


    }


    public void clearProperies() {
        //编辑器是否应该重新获取
        if (this.isEditing()) {
            this.getCellEditor().stopCellEditing();
        }

        myProperties.clear();
    }

    public List<Property> getMyProperties() {
        return myProperties;
    }

    public void setMyProperties(List<Property> myProperties) {
        this.myProperties = myProperties;
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

    @Override
    public void shapeSelected(ShapeSelectedEvent shapeSelectedEvent) {

        PropertyObject selectedObject = shapeSelectedEvent.getSelectedObject();
        setOperatedObject(selectedObject);
    }

    /**
     * 设置被操作的对象
     *
     * @param selectedObject
     */
    private void setOperatedObject(PropertyObject selectedObject) {
        if (selectedObject == null) {
            this.repaint();
            return;
        }
        //修改操作的对象的时候，是需要重新进行维护的
        if (this.operatedObject == selectedObject) {
            return;
        }
        this.operatedObject = selectedObject;
        this.clearProperies();

        Property[] propertyEditors = operatedObject.getPropertyEditors(this);
        for (Property p : propertyEditors) {
            this.addProperty(p);
        }
        this.resetRowHeight();
    }

    @Override
    public void propertyEdited(Property property, Object operatedObj, Object oldValue, Object newValue) {
        if (oldValue != newValue) {
            //触发函数回调
            if (SpdEditorDBState.getInstance().autoSave) {
                chartPanel.fireSavedListener();
            }
        }
    }

    public void bindChartPanel(ChartPanel chartPanel) {
        this.chartPanel = chartPanel;
    }

    public class MyTableModel extends AbstractTableModel {

        private String columnNames[] = new String[]{"Property", "Value"};


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
