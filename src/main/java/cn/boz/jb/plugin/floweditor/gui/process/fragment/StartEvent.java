package cn.boz.jb.plugin.floweditor.gui.process.fragment;

import cn.boz.jb.plugin.floweditor.gui.process.bridge.CircleBridge;
import cn.boz.jb.plugin.floweditor.gui.shape.HiPoint;
import cn.boz.jb.plugin.floweditor.gui.shape.Rect;
import cn.boz.jb.plugin.floweditor.gui.utils.FontUtils;
import cn.boz.jb.plugin.floweditor.gui.utils.IcoMoonUtils;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.awt.Color;

public class StartEvent extends CircleBridge {


    @Override
    public void drawContent(ChartPanel chartPanel) {
        super.drawContent(chartPanel);
        chartPanel.markFont();
        chartPanel.setColor(getForegroundColor());
        chartPanel.setFontExt(IcoMoonUtils.getFont16());
        chartPanel.drawString(this.getX(), this.getY() , this.getWidth(), this.getHeight(), IcoMoonUtils.getPlay());
        chartPanel.resetFont();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        StartEvent startEvent = new StartEvent();
        startEvent.setX(this.x);
        startEvent.setY(this.y);
        startEvent.setWidth(this.width);
        startEvent.setHeight(this.height);
        startEvent.setActiveBackgroundColor(this.activeBackgroundColor);
        startEvent.setActiveForegroundColor(this.activeForegroundColor);
        startEvent.setBackgroundColor(this.backgroundColor);
        startEvent.setForegroundColor(this.foregroundColor);
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
    public void init(HiPoint hiPoint){
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

}
