package cn.boz.jb.plugin.floweditor.gui.widget;

import cn.boz.jb.plugin.floweditor.gui.property.Property;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class SpdTableCellRender implements TableCellRenderer {
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
