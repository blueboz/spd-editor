package cn.boz.jb.plugin.idea.widget;

import cn.boz.jb.plugin.floweditor.gui.process.fragment.CallActivity;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ServiceTask;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.UserTask;
import cn.boz.jb.plugin.floweditor.gui.shape.Shape;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import com.intellij.ui.SpeedSearchBase;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.util.ListIterator;

public class SpdSpeedSearch extends SpeedSearchBase {

    @NotNull
    @Override
    protected SearchPopup createPopup(String s) {
        SearchPopup popup = super.createPopup(s);
        JLabel jLabel = new JLabel();
        popup.add(jLabel, BorderLayout.SOUTH);
        //参考其他组件的做法是对搜索算法进行抽取，然后自行进行元素过滤
        //根据当前原始是否可见来控制筛选组件是否课件的操作方法
        return popup;
        ///每次键盘敲击都会触发一次selected

    }

    @NotNull
    private IntList findAllFilteredElements(@NotNull String s) {
        IntList indices = new IntArrayList();
        String trimmed = s.trim();
        ListIterator iterator = this.getElementIterator(0);
        while (iterator.hasNext()) {
            Object element = iterator.next();
            if (this.isMatchingElement(element, trimmed)) {
                indices.add(iterator.previousIndex());
            }
        }
        return indices;
    }

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
    protected Object getElementAt(int viewIndex) {
        return chartPanel.getAllElements().get(viewIndex);
    }

    @Override
    protected int getElementCount() {
        return chartPanel.getAllElements().size();
    }

    @Override
    protected void selectElement(Object o, String s) {
        System.out.println("select :" + s + " ->" + ((Shape) o).getName());
        if (o == null) {
            return;
        }
        int i = chartPanel.getAllElements().indexOf(o);
        cidx = i;
        //获取焦点

    }
}
