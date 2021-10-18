package cn.boz.jb.plugin.floweditor.gui.process.fragment;

import cn.boz.jb.plugin.floweditor.gui.control.SqlAggregator;
import cn.boz.jb.plugin.floweditor.gui.process.bridge.RectBridge;
import cn.boz.jb.plugin.floweditor.gui.property.Property;
import cn.boz.jb.plugin.floweditor.gui.property.impl.TextFieldProperty;
import cn.boz.jb.plugin.floweditor.gui.shape.HiPoint;
import cn.boz.jb.plugin.floweditor.gui.shape.Rect;
import cn.boz.jb.plugin.floweditor.gui.utils.IcoMoonUtils;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class CallActivity extends RectBridge implements SqlAggregator {

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
        chartPanel.setFontExt(IcoMoonUtils.getFont16());
        chartPanel.drawString(this.getX() + 1, this.getY() + 1, IcoMoonUtils.getPlus());
        chartPanel.resetFont();
        chartPanel.drawString(this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.getName());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        CallActivity callActivity = new CallActivity();
        callActivity.setX(this.x);
        callActivity.setY(this.y);
        callActivity.setWidth(this.width);
        callActivity.setHeight(this.height);

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

    @Override
    public Property[] getPropertyEditors() {
        Property[] ps = new Property[]{
                new TextFieldProperty("name", this),
                new TextFieldProperty("calledElement", this),
        };
        return ps;
    }

    @Override
    public String toSql() {
        return String.format("INSERT INTO ENGINE_TASK (ID_, TYPE_, TITLE_, EXPRESSION_, RETURNVALUE_, BUSSINESKEY_, BUSSINESDESC_,\n" +
                        "RIGHTS_, VALIDSECOND_, LISTENER_, OPENSECOND_, BUSSINESID_, TASKLISTENER_)\n" +
                        "VALUES ('%s', 'CALL', '%s', '%s', null, null, null, null, 10000, null, 60, null, null);", this.getId()
                , this.getName(), this.getCalledElement());
    }
}
