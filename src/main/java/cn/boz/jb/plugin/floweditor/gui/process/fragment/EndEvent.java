package cn.boz.jb.plugin.floweditor.gui.process.fragment;

import cn.boz.jb.plugin.floweditor.gui.process.bridge.CircleBridge;
import cn.boz.jb.plugin.floweditor.gui.shape.HiPoint;
import cn.boz.jb.plugin.floweditor.gui.shape.Rect;
import cn.boz.jb.plugin.floweditor.gui.utils.FontUtils;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.awt.Color;

public class EndEvent extends CircleBridge {

    @Override
    public void drawContent(ChartPanel chartPanel) {
        super.drawContent(chartPanel);
        chartPanel.markFont();
        chartPanel.setColor(getForegroundColor());
        chartPanel.setFontExt(FontUtils.FA);
        chartPanel.drawString(this.getX()-1, this.getY() , this.getWidth(), this.getHeight(), FontUtils.stop());
        chartPanel.resetFont();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        EndEvent endEvent = new EndEvent();
        endEvent.setX(this.x);
        endEvent.setY(this.y);
        endEvent.setWidth(this.width);
        endEvent.setHeight(this.height);
        endEvent.setActiveBackgroundColor(this.activeBackgroundColor);
        endEvent.setActiveForegroundColor(this.activeForegroundColor);
        endEvent.setBackgroundColor(this.backgroundColor);
        endEvent.setForegroundColor(this.foregroundColor);
        endEvent.setName(this.name);
        return endEvent;
    }

    @Override
    public Element buildProcessNode() {
        Element element = DocumentHelper.createElement("endEvent");
        element.addAttribute("id", this.getId());
        element.addAttribute("name", this.getName());
        return element;
    }

    @Override
    public String getIdPrefix() {
        return "endevent";
    }

    @Override
    public void init(HiPoint hiPoint) {
        super.init(hiPoint);
        this.setName("End");
    }

    @Override
    public void init(Rect rect) {
        super.init(rect);
        this.setName("End");
    }
}