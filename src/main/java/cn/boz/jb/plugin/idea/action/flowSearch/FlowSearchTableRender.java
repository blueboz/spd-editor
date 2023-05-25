package cn.boz.jb.plugin.idea.action.flowSearch;

import cn.boz.jb.plugin.floweditor.gui.process.fragment.CallActivity;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.EndEvent;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ExclusiveGateway;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ForeachGateway;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ParallelGateway;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.SequenceFlow;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ServiceTask;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.StartEvent;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.UserTask;
import cn.boz.jb.plugin.floweditor.gui.shape.Label;
import cn.boz.jb.plugin.floweditor.gui.utils.IcoMoonUtils;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.speedSearch.SpeedSearchUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class FlowSearchTableRender implements TableCellRenderer {

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
            myComponent.setFont(IcoMoonUtils.getFont16());
            if (ServiceTask.class.getSimpleName().equals(value)) {
                myComponent.append(IcoMoonUtils.getServiceTask());
            } else if (UserTask.class.getSimpleName().equals(value)) {
                myComponent.append(IcoMoonUtils.getUserTask());
            } else if (CallActivity.class.getSimpleName().equals(value)) {
                myComponent.append(IcoMoonUtils.getCallActivity());
            } else if (ExclusiveGateway.class.getSimpleName().equals(value)) {
                myComponent.append(IcoMoonUtils.getExclusiveGateway());
            } else if (ParallelGateway.class.getSimpleName().equals(value)) {
                myComponent.append(IcoMoonUtils.getParallelGateway());
            } else if (ForeachGateway.class.getSimpleName().equals(value)) {
                myComponent.append(IcoMoonUtils.getForeachGateway());
            } else if (SequenceFlow.class.getSimpleName().equals(value)) {
                myComponent.append(IcoMoonUtils.getSequenceFlow());
            } else if (StartEvent.class.getSimpleName().equals(value)) {
                myComponent.append(IcoMoonUtils.getStartEvent());
            } else if (EndEvent.class.getSimpleName().equals(value)) {
                myComponent.append(IcoMoonUtils.getEndEvent());
            } else if (Label.class.getSimpleName().equals(value)) {
                myComponent.append(IcoMoonUtils.getHText());
            } else {
                System.out.println("unrecognized " + value);
            }
        } else {
            Font font = table.getFont();
            myComponent.setFont(font);

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
