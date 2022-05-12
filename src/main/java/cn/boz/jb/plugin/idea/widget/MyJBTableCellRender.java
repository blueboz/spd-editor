package cn.boz.jb.plugin.idea.widget;

import cn.boz.jb.plugin.floweditor.gui.property.Property;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;

public class MyJBTableCellRender implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Property property = (Property) value;
        if (column == 0) {
            //渲染标题
            JComponent render = property.getPropertyRender();
            return render;
        } else {
            JComponent propertyRender = property.getValueRender();
            return propertyRender;

        }
    }
}
