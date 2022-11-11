package cn.boz.jb.plugin.idea.callsearch;


import cn.boz.jb.plugin.floweditor.gui.utils.FunctionIconUtils;
import cn.boz.jb.plugin.idea.bean.EngineAction;
import cn.boz.jb.plugin.idea.bean.EngineTask;
import cn.boz.jb.plugin.idea.bean.XfundsBatch;
import cn.boz.jb.plugin.idea.widget.SimpleIconControl;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.speedSearch.SpeedSearchUtil;
import icons.SpdEditorIcons;
import org.jdesktop.swingx.painter.AbstractLayoutPainter;
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
        myComponent.setFont(table.getFont());
        myComponent.setTextAlign(SwingConstants.LEFT);

        if (column == 0) {

            myComponent.setTextAlign(SwingConstants.CENTER);

            myComponent.setFont(FunctionIconUtils.getFont(14));
            if ("task".equals(value)) {
                myComponent.append(FunctionIconUtils.ICO_GEAR);
            } else if ("action".equals(value)) {
                myComponent.append(FunctionIconUtils.ICO_ACTION);
            }else if("batch".equals(value)){
                myComponent.append(FunctionIconUtils.ICO_BATCH);
            }
        } else if(column==1){
            JCheckBox jCheckBox = new JCheckBox();
            jCheckBox.setBackground(bg);
            jCheckBox.setForeground(fg);
            if (value instanceof EngineTask) {
                jCheckBox.getModel().setSelected(((EngineTask)value).isChecked());
            } else if (value instanceof EngineAction) {
                jCheckBox.getModel().setSelected(((EngineAction) value).isChecked());
            }else if(value instanceof XfundsBatch){
                jCheckBox.getModel().setSelected(((XfundsBatch)value).isChecked());
            }
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