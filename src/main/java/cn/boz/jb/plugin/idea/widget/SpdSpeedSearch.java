package cn.boz.jb.plugin.idea.widget;

import cn.boz.jb.plugin.floweditor.gui.process.fragment.CallActivity;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ServiceTask;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.UserTask;
import cn.boz.jb.plugin.floweditor.gui.shape.Shape;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import com.intellij.ui.SpeedSearchBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public class SpdSpeedSearch extends SpeedSearchBase {

    private ChartPanel chartPanel;

    private int cidx = -1;

    public SpdSpeedSearch(@NotNull ChartPanel component) {
        //怎样与list中的内容进行交互的呢，进行筛选控制的呢
        super(component);
        chartPanel = component;
        //需要为指定控件设置事件,消息事件
    }

    @Override
    protected int getSelectedIndex() {
        return cidx;
    }

    @Override
    protected @Nullable String getElementText(Object o) {
        if (o == null) {
            return "";
        }
        if (!(o instanceof Shape)) {
            return "";
        }
        Shape shape = (Shape) o;
        StringBuilder searchString = new StringBuilder();
        searchString.append(shape.getName());
        if (shape instanceof UserTask) {
            searchString.append(((UserTask) shape).getExpression());
        } else if (shape instanceof ServiceTask) {
            searchString.append(((ServiceTask) shape).getExpression());
        } else if (shape instanceof CallActivity) {
            searchString.append(((CallActivity) shape).getCalledElement());
        }
        return searchString.toString();
    }


    @Override
    protected Object @NotNull [] getAllElements() {
        return chartPanel.getAllElements().toArray();
    }

    @Override
    protected int getElementCount() {
        return chartPanel.getAllElements().size();
    }

    @Override
    protected void selectElement(Object o, String s) {
        if (o == null) {
            return;
        }
        if ("".equals(s)) {
            return;
        }
        chartPanel.selectShape((Shape) o);

    }
}
