package cn.boz.jb.plugin.floweditor.gui.process.fragment;

import cn.boz.jb.plugin.floweditor.gui.process.bridge.RectBridge;
import cn.boz.jb.plugin.floweditor.gui.shape.HiPoint;
import cn.boz.jb.plugin.floweditor.gui.shape.Rect;
import cn.boz.jb.plugin.floweditor.gui.utils.FontUtils;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class CallActivity extends RectBridge {

    private String calledElement;

    public String getCalledElement() {
        return calledElement;
    }

    public void setCalledElement(String calledElement) {
        this.calledElement = calledElement;
    }


    @Override
    public void drawContent(ChartPanel chartPanel) {
        super.drawContent(chartPanel);

        chartPanel.markFont();
        chartPanel.setColor(getForegroundColor());
        chartPanel.setFontExt(FontUtils.FA);
        chartPanel.drawString(this.getX()+1,this.getY()+1, FontUtils.plus());
        chartPanel.resetFont();
        chartPanel.drawString(this.getX(),this.getY(),this.getWidth(),this.getHeight(),this.getName());
    }
    @Override
    public Object clone() throws CloneNotSupportedException {
        CallActivity callActivity = new CallActivity();
        callActivity.setX(this.x);
        callActivity.setY(this.y);
        callActivity.setWidth(this.width);
        callActivity.setHeight(this.height);
        callActivity.setActiveBackgroundColor(this.activeBackgroundColor);
        callActivity.setActiveForegroundColor(this.activeForegroundColor);
        callActivity.setBackgroundColor(this.backgroundColor);
        callActivity.setForegroundColor(this.foregroundColor);
        callActivity.setName(this.name);
        callActivity.setCalledElement(this.calledElement);
        return callActivity;
    }

    @Override
    public Element buildProcessNode() {
        Element element = DocumentHelper.createElement("callActivity");
        element.addAttribute("id", this.getId());
        element.addAttribute("name", this.getName());
        element.addAttribute("calledElement", this.getCalledElement());
        return element;
    }

    @Override
    public String getIdPrefix() {
        return "callactivity";
    }

    @Override
    public void init(Rect rect) {
        super.init(rect);
        this.setName("Call activity");

    }

    @Override
    public void init(HiPoint hiPoint) {
        super.init(hiPoint);
        this.setName("Call activity");
    }
}
