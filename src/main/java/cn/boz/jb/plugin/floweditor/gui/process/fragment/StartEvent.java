package cn.boz.jb.plugin.floweditor.gui.process.fragment;

import cn.boz.jb.plugin.floweditor.gui.process.bridge.CircleBridge;
import cn.boz.jb.plugin.floweditor.gui.property.Property;
import cn.boz.jb.plugin.floweditor.gui.property.PropertyEditorListener;
import cn.boz.jb.plugin.floweditor.gui.property.impl.LabelProperty;
import cn.boz.jb.plugin.floweditor.gui.property.impl.TextAreaProperty;
import cn.boz.jb.plugin.floweditor.gui.property.impl.TextFieldProperty;
import cn.boz.jb.plugin.floweditor.gui.shape.HiPoint;
import cn.boz.jb.plugin.floweditor.gui.shape.Rect;
import cn.boz.jb.plugin.floweditor.gui.utils.IcoMoonUtils;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.awt.*;

public class StartEvent extends CircleBridge {

    public static Cursor CURSOR = IcoMoonUtils.initCursor(IcoMoonUtils.getStartEvent());
    @Override
    public String indicatorFont() {
        return  IcoMoonUtils.getPlay();

    }
    @Override
    public void drawContent(ChartPanel chartPanel) {
        super.drawContent(chartPanel);
        chartPanel.markFont();
        chartPanel.setColor(getForegroundColor());
        chartPanel.setFontExt(IcoMoonUtils.getFont16());
        chartPanel.drawString(this.getX(), this.getY(), this.getWidth(), this.getHeight(), indicatorFont());
        chartPanel.resetFont();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        StartEvent startEvent = new StartEvent();
        startEvent.setX(this.x);
        startEvent.setY(this.y);
        startEvent.setWidth(this.width);
        startEvent.setHeight(this.height);

        startEvent.setName(this.name);
        return startEvent;
    }

    @Override
    public Element buildProcessNode() {
        Element element = DocumentHelper.createElement("startEvent");
        element.addAttribute("id", this.getId());
        element.addAttribute("name", this.getName());
        return element;
    }

    @Override
    public void init(HiPoint hiPoint) {
        super.init(hiPoint);
        this.setName("Start");
    }

    @Override
    public void init(Rect rect) {
        super.init(rect);
        this.setName("Start");

    }


    @Override
    public String getIdPrefix() {
        return "startevent";
    }
    private Property[] ps;

    @Override
    public Property[] getPropertyEditors(PropertyEditorListener propertyEditor) {
        if (ps == null) {
            synchronized (ServiceTask.class) {
                if (ps == null) {
                    ps = new Property[]{
                            new LabelProperty("id", this, propertyEditor),
                    };
                }
            }
        }
        return ps;
    }

}
