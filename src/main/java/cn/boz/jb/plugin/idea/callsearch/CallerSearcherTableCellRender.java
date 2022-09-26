package cn.boz.jb.plugin.idea.callsearch;


import cn.boz.jb.plugin.idea.widget.SimpleIconControl;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.speedSearch.SpeedSearchUtil;
import icons.SpdEditorIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class CallerSearcherTableCellRender implements TableCellRenderer {
    private final SimpleColoredComponent myComponent = new SimpleColoredComponent();

    @NotNull
    @Override
    public Component getTableCellRendererComponent(@NotNull JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {

        Color bg = isSelected ? table.getSelectionBackground() : table.getBackground();
        Color fg = isSelected ? table.getSelectionForeground() : table.getForeground();
        myComponent.clear();

        if (column == 0) {
            SimpleIconControl simpleIconControl = null;
            if ("task".equals(value)) {
                simpleIconControl = new SimpleIconControl(SpdEditorIcons.GEAR_16_ICON);
            } else if ("action".equals(value)) {
                simpleIconControl = new SimpleIconControl(SpdEditorIcons.ACTION_SCRIPT_16_ICON);

            }
            if (simpleIconControl != null) {
                simpleIconControl.setBackground(bg);
                simpleIconControl.setForeground(fg);
                return simpleIconControl;
            }
        } else if(column==1){
            JCheckBox jCheckBox = new JCheckBox();
            jCheckBox.getModel().setSelected((Boolean) value);
            jCheckBox.setBackground(bg);
            jCheckBox.setForeground(fg);

            return jCheckBox;
        }else {
            if (value != null) {
                myComponent.append((String) value);
            } else {
                myComponent.append((String) "");
            }
        }
        myComponent.setBackground(bg);
        myComponent.setForeground(fg);

        SpeedSearchUtil.applySpeedSearchHighlighting(table, myComponent, true, hasFocus);
        return myComponent;

    }
}