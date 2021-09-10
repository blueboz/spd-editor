package cn.boz.jb.plugin.floweditor.gui.process.fragment;

import cn.boz.jb.plugin.floweditor.gui.process.bridge.CircleBridge;
import cn.boz.jb.plugin.floweditor.gui.shape.HiPoint;
import cn.boz.jb.plugin.floweditor.gui.shape.Rect;
import cn.boz.jb.plugin.floweditor.gui.utils.FontUtils;
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
        chartPanel.setFontExt(FontUtils.FA);
        chartPanel.drawString(this.getX()+1, this.getY() + 1, this.getWidth(), this.getHeight(), FontUtils.play());
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
        this.setX(hiPoint.x);
        this.setY(hiPoint.y);
        this.setWidth(35);
        this.setHeight(35);
        this.setName("Start");
    }

    @Override
    public void init(Rect rect) {
        this.setX(rect.getX());
        this.setY(rect.getY());
        this.setWidth(35);
        this.setHeight(35);
        this.setName("Start");

    }


    @Override
    public String getIdPrefix() {
        return "startevent";
    }

}
