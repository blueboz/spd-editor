package cn.boz.jb.plugin.floweditor.gui.process.fragment;

import cn.boz.jb.plugin.floweditor.gui.control.SqlAggregator;
import cn.boz.jb.plugin.floweditor.gui.process.Gateway;
import cn.boz.jb.plugin.floweditor.gui.process.bridge.PrismaticBridge;
import cn.boz.jb.plugin.floweditor.gui.property.Property;
import cn.boz.jb.plugin.floweditor.gui.property.PropertyEditorListener;
import cn.boz.jb.plugin.floweditor.gui.property.impl.TextAreaProperty;
import cn.boz.jb.plugin.floweditor.gui.property.impl.TextFieldProperty;
import cn.boz.jb.plugin.floweditor.gui.shape.HiPoint;
import cn.boz.jb.plugin.floweditor.gui.shape.Rect;
import cn.boz.jb.plugin.floweditor.gui.utils.IcoMoonUtils;
import cn.boz.jb.plugin.floweditor.gui.utils.TranslateUtils;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.awt.Cursor;

public class ForeachGateway extends PrismaticBridge implements Gateway, SqlAggregator {

    protected String expression;

    public static Cursor CURSOR = IcoMoonUtils.initCursor(IcoMoonUtils.getForeachGateway());

    @Override
    public String indicatorFont() {
        return  IcoMoonUtils.getCircle();

    }

    @Override
    public void drawContent(ChartPanel chartPanel) {
        super.drawContent(chartPanel);

        chartPanel.markFont();
        chartPanel.setColor(getForegroundColor());
        chartPanel.setFontExt(IcoMoonUtils.getFont20());
        chartPanel.drawString(this.getX(), this.getY() + 2, this.getWidth(), this.getHeight(), indicatorFont());
        chartPanel.resetFont();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ForeachGateway foreachGateway = new ForeachGateway();

        foreachGateway.setX(this.x);
        foreachGateway.setY(this.y);
        foreachGateway.setWidth(this.width);
        foreachGateway.setHeight(this.height);
        foreachGateway.setExpression(this.expression);

        foreachGateway.setName(this.name);
        return foreachGateway;
    }

    @Override
    public Element buildProcessNode() {
        Element element = DocumentHelper.createElement("foreachGateway");
        element.addAttribute("id", this.getId());
        element.addAttribute("name", TranslateUtils.translateToXmlString(this.getName()));
        element.addAttribute("expression", TranslateUtils.translateToXmlString(this.getExpression()));
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

    @Override
    public String toSql(String processId) {
        return String.format("INSERT INTO ENGINE_TASK (ID_, TYPE_, TITLE_, EXPRESSION_, RETURNVALUE_, BUSSINESKEY_, BUSSINESDESC_," +
                " RIGHTS_, VALIDSECOND_, LISTENER_, OPENSECOND_, BUSSINESID_, TASKLISTENER_) " +
                "VALUES ('%s', 'FOR', '%s', '%s', null, null, null, null, 10000, null, 60, null, null)", processId + "_" + this.id, this.name, this.expression);
    }

    private Property[] ps;

    @Override
    public Property[] getPropertyEditors(PropertyEditorListener propertyEditor) {
        //属性改变的时候，需要通知Chart
        if (ps == null) {
            synchronized (ParallelGateway.class) {
                if (ps == null) {
                    ps = new Property[]{
                            new TextFieldProperty("name", this),
                            new TextAreaProperty("expression", this)
                    };
                }
            }
        }
        return ps;
    }

    public String getExpression() {
        if (expression == null) {
            return "";
        }
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

}

