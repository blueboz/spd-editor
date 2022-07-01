package cn.boz.jb.plugin.floweditor.gui.process.fragment;

import cn.boz.jb.plugin.floweditor.gui.control.SqlAggregator;
import cn.boz.jb.plugin.floweditor.gui.process.bridge.RectBridge;
import cn.boz.jb.plugin.floweditor.gui.property.Property;
import cn.boz.jb.plugin.floweditor.gui.property.PropertyEditorListener;
import cn.boz.jb.plugin.floweditor.gui.property.impl.TextFieldProperty;
import cn.boz.jb.plugin.floweditor.gui.shape.HiPoint;
import cn.boz.jb.plugin.floweditor.gui.shape.Rect;
import cn.boz.jb.plugin.floweditor.gui.utils.IcoMoonUtils;
import cn.boz.jb.plugin.floweditor.gui.utils.TranslateUtils;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.awt.Cursor;

public class CallActivity extends RectBridge implements SqlAggregator {

    public static Cursor CURSOR = IcoMoonUtils.initCursor(IcoMoonUtils.getCallActivity());

    private String calledElement;

    public String getCalledElement() {
        return calledElement;
    }

    public void setCalledElement(String calledElement) {
        this.calledElement = calledElement;
    }

    @Override
    public String indicatorFont() {
        return  IcoMoonUtils.getPlus();

    }
    @Override
    public void drawContent(ChartPanel chartPanel) {
        super.drawContent(chartPanel);

        chartPanel.markFont();
        chartPanel.setColor(getForegroundColor());
        chartPanel.setFontExt(IcoMoonUtils.getFont16());
        chartPanel.drawString(this.getX() + 1, this.getY() + 1, indicatorFont());
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
        element.addAttribute("name", TranslateUtils.translateToXmlString(this.getName()));
        element.addAttribute("calledElement", TranslateUtils.translateToXmlString(this.getCalledElement()));
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

    private Property[] ps;


    @Override
    public Property[] getPropertyEditors(PropertyEditorListener propertyEditor) {
        if (ps == null) {
            synchronized (CallActivity.class) {
                if (ps == null) {
                    ps = new Property[]{
                            new TextFieldProperty("name", this, propertyEditor),
                            new TextFieldProperty("calledElement", this, propertyEditor),
                    };
                }
            }
        }

        return ps;
    }


    @Override
    public String toSql(String processId) {
        return String.format("INSERT INTO ENGINE_TASK (ID_, TYPE_, TITLE_, EXPRESSION_, RETURNVALUE_, BUSSINESKEY_, BUSSINESDESC_," +
                        "RIGHTS_, VALIDSECOND_, LISTENER_, OPENSECOND_, BUSSINESID_, TASKLISTENER_)" +
                        "VALUES ('%s', 'CALL', '%s', '%s', null, null, null, null, 10000, null, 60, null, null)", processId + "_" + this.getId()
                , this.getName(), this.getCalledElement());
    }
}
