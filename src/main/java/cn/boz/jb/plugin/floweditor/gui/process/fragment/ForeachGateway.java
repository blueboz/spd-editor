package cn.boz.jb.plugin.floweditor.gui.process.fragment;

import cn.boz.jb.plugin.floweditor.gui.process.Gateway;
import cn.boz.jb.plugin.floweditor.gui.process.bridge.PrismaticBridge;
import cn.boz.jb.plugin.floweditor.gui.shape.HiPoint;
import cn.boz.jb.plugin.floweditor.gui.shape.Rect;
import cn.boz.jb.plugin.floweditor.gui.utils.FontUtils;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.awt.Color;

public class ForeachGateway extends PrismaticBridge implements Gateway {


    @Override
    public void drawContent(ChartPanel chartPanel) {
        super.drawContent(chartPanel);

        chartPanel.markFont();
        chartPanel.setColor(getForegroundColor());
        chartPanel.setFontExt(FontUtils.FA_25);
        chartPanel.drawString(this.getX(),this.getY()+2,this.getWidth(),this.getHeight(), FontUtils.circleo());
        chartPanel.resetFont();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ForeachGateway foreachGateway = new ForeachGateway();

        foreachGateway.setX(this.x);
        foreachGateway.setY(this.y);
        foreachGateway.setWidth(this.width);
        foreachGateway.setHeight(this.height);
        foreachGateway.setActiveBackgroundColor(this.activeBackgroundColor);
        foreachGateway.setActiveForegroundColor(this.activeForegroundColor);
        foreachGateway.setBackgroundColor(this.backgroundColor);
        foreachGateway.setForegroundColor(this.foregroundColor);
        foreachGateway.setName(this.name);
        return foreachGateway;
    }

    @Override
    public Element buildProcessNode() {
        Element element = DocumentHelper.createElement("foreachGateway");
        element.addAttribute("id", this.getId());
        element.addAttribute("name", this.getName());
        return element;
    }

    @Override
    public String getIdPrefix() {
        return "forgateway";
    }

    @Override
    public void init(HiPoint hiPoint) {
        super.init(hiPoint);
        this.setName("Exclusive Gateway");
    }

    @Override
    public void init(Rect rect) {
        super.init(rect);
        this.setName("Exclusive Gateway");

    }
}
