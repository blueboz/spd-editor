package cn.boz.jb.plugin.floweditor.gui.process.fragment;

import cn.boz.jb.plugin.floweditor.gui.process.bridge.RectBridge;
import cn.boz.jb.plugin.floweditor.gui.property.Property;
import cn.boz.jb.plugin.floweditor.gui.property.impl.TextAreaProperty;
import cn.boz.jb.plugin.floweditor.gui.property.impl.TextFieldProperty;
import cn.boz.jb.plugin.floweditor.gui.shape.HiPoint;
import cn.boz.jb.plugin.floweditor.gui.shape.Rect;
import cn.boz.jb.plugin.floweditor.gui.utils.FontUtils;
import cn.boz.jb.plugin.floweditor.gui.utils.IcoMoonUtils;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class ServiceTask extends RectBridge {

    private String expression;
    private String listener;

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public void drawContent(ChartPanel chartPanel) {
        super.drawContent(chartPanel);
        chartPanel.markFont();
        chartPanel.setColor(getForegroundColor());
        chartPanel.setFontExt(IcoMoonUtils.getFont16());
        chartPanel.drawString(this.getX() + 1, this.getY() + 1, IcoMoonUtils.getGear());
        chartPanel.resetFont();
        chartPanel.drawString(this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.getName());

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ServiceTask serviceTask = new ServiceTask();

        serviceTask.setX(this.x);
        serviceTask.setY(this.y);
        serviceTask.setWidth(this.width);
        serviceTask.setHeight(this.height);
        serviceTask.setActiveBackgroundColor(this.activeBackgroundColor);
        serviceTask.setActiveForegroundColor(this.activeForegroundColor);
        serviceTask.setBackgroundColor(this.backgroundColor);
        serviceTask.setForegroundColor(this.foregroundColor);
        serviceTask.setName(this.name);
        serviceTask.setExpression(this.expression);
        return serviceTask;
    }

    public String getListener() {
        return listener;
    }

    public void setListener(String listener) {
        this.listener = listener;
    }

    @Override
    public Element buildProcessNode() {
        Element element = DocumentHelper.createElement("serviceTask");
        element.addAttribute("id", this.getId());
        element.addAttribute("name", this.getName());
        element.addAttribute("expression", this.getExpression());
        element.addAttribute("listener", this.getListener());
        return element;
    }

    @Override
    public String getIdPrefix() {
        return "servicetask";
    }

    @Override
    public void init(HiPoint hiPoint) {
        super.init(hiPoint);
        this.setName("Service Task");

    }

    @Override
    public void init(Rect rect) {
        super.init(rect);
        this.setName("Service Task");
    }

    @Override
    public Property[] getPropertyEditors() {
        Property[] ps = new Property[]{
                new TextAreaProperty("expression", this),
                new TextFieldProperty("listener", this),
        };
        return ps;
    }
}

